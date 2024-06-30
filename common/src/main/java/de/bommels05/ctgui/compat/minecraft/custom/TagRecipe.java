package de.bommels05.ctgui.compat.minecraft.custom;

import de.bommels05.ctgui.CraftTweakerGUI;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.tags.TagManager;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TagRecipe implements Recipe<Container> {

    protected final ResourceLocation id;
    protected final boolean item;
    //This is not serialized because it is just used to indicate an incomplete tag name while editing
    protected boolean valid = true;
    protected final List<ItemStack> items;
    protected final List<Fluid> fluids;
    protected final List<TagKey<Item>> itemTags;
    protected final List<TagKey<Fluid>> fluidTags;

    public TagRecipe(TagKey<Item> tag, List<ItemStack> items, List<TagKey<Item>> tags) {
        this.id = tag.location();
        this.item = true;
        this.items = items;
        this.itemTags = tags;
        this.fluids = null;
        this.fluidTags = null;
    }

    public TagRecipe(List<TagKey<Fluid>> tags, List<Fluid> fluids, TagKey<Fluid> tag) {
        this.id = tag.location();
        this.item = false;
        this.fluids = fluids;
        this.fluidTags = tags;
        this.items = null;
        this.itemTags = null;
    }

    public TagRecipe(TagKey<?> tag) {
        this.id = tag.location();
        if (tag.registry().equals(Registries.ITEM)) {
            this.item = true;
            this.items = new ArrayList<>();
            this.itemTags = new ArrayList<>();
            this.fluids = null;
            this.fluidTags = null;
        } else if (tag.registry().equals(Registries.FLUID)) {
            this.item = false;
            this.fluids = new ArrayList<>();
            this.fluidTags = new ArrayList<>();
            this.items = null;
            this.itemTags = null;
        } else {
            throw new IllegalArgumentException("Tag must be an item or fluid tag");
        }

        //Tag editing is cursed in general...
        Map<ResourceLocation, List<TagLoader.EntryWithSource>> tags = new TagLoader<>(null, TagManager.getTagDir(tag.registry()))
                .load(CraftTweakerGUI.getLoaderUtils().getServer().getResourceManager());
        List<TagLoader.EntryWithSource> entries = tags.get(tag.location());
        if (entries != null) {
            for (TagEntry entry : entries.stream().map(TagLoader.EntryWithSource::entry).toList()) {
                if (entry.tag) {
                    if (item) {
                        itemTags.add(TagKey.create(Registries.ITEM, entry.id));
                    } else {
                        fluidTags.add(TagKey.create(Registries.FLUID, entry.id));
                    }
                } else {
                    Optional<?> optional = BuiltInRegistries.REGISTRY.get(tag.registry().location()).getOptional(entry.id);
                    if (optional.isPresent()) {
                        if (item) {
                            items.add(new ItemStack((Item) optional.get()));
                        } else {
                            fluids.add((Fluid) optional.get());
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return false;
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CraftTweakerGUI.getLoaderUtils().getTagRecipeSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return CraftTweakerGUI.getLoaderUtils().getTagRecipeType();
    }

}
