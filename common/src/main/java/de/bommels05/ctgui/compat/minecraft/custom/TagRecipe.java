package de.bommels05.ctgui.compat.minecraft.custom;

import de.bommels05.ctgui.CraftTweakerGUI;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class TagRecipe<T> implements Recipe<RecipeInput> {

    protected final ResourceLocation id;
    protected final Registry<T> registry;
    protected final List<TagKey<T>> tags;
    protected final List<T> entries;
    //This is not serialized because it is just used to indicate an incomplete tag name while editing
    protected boolean valid = true;

    public TagRecipe(ResourceLocation id, Registry<T> registry, List<TagKey<T>> tags, List<T> entries) {
        this.id = id;
        this.registry = registry;
        this.tags = tags;
        this.entries = entries;
    }

    @SuppressWarnings("unchecked")
    public static <T> TagRecipe<T> cast(ResourceLocation id, Registry<?> registry, List<TagKey<?>> tags, List<?> entries) {
        return new TagRecipe<>(id, (Registry<T>) registry, (List<TagKey<T>>) (List<?>) tags, (List<T>) entries);
    }

    @SuppressWarnings("unchecked")
    public TagRecipe(TagKey<T> tag) {
        this.id = tag.location();
        this.registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(tag.registry().location());
        this.tags = new ArrayList<>();
        this.entries = new ArrayList<>();

        //Tag editing is cursed in general...
        Map<ResourceLocation, List<TagLoader.EntryWithSource>> tags = new TagLoader<>(null, Registries.tagsDirPath(tag.registry()))
                .load(CraftTweakerGUI.getLoaderUtils().getServer().getResourceManager());
        List<TagLoader.EntryWithSource> entries = tags.get(tag.location());
        if (entries != null) {
            for (TagEntry entry : entries.stream().map(TagLoader.EntryWithSource::entry).toList()) {
                if (entry.tag) {
                    this.tags.add(TagKey.create(registry.key(), entry.id));
                } else {
                    Optional<T> optional = registry.getOptional(entry.id);
                    optional.ifPresent(this.entries::add);
                }
            }
        }
    }

    //To prevent unchecked casts in TagRecipeType
    protected void forEntryWithId(Consumer<ResourceLocation> consumer) {
        for (T entry : entries) {
            consumer.accept(registry.getKey(entry));
        }
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
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
