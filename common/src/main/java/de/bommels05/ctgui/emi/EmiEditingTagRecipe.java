package de.bommels05.ctgui.emi;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipeType;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiResolutionRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.TagEmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.recipe.EmiTagRecipe;
import dev.emi.emi.screen.WidgetGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;

public class EmiEditingTagRecipe extends EmiTagRecipe {

    private final List<EmiIngredient> ingredients = new ArrayList<>();
    private EmiIngredient ingredient;
    private TagRecipeType type;

    private EmiEditingTagRecipe(TagRecipeType type, TagKey<?> key, List<TagKey<?>> tags) {
        super(key);
        this.type = type;
        for (TagKey<?> tag : tags) {
            ingredients.add(new TagEmiIngredient(tag, 1));
        }
    }

    public EmiEditingTagRecipe(TagRecipeType type, TagKey<Item> key, List<ItemStack> items, List<TagKey<Item>> tags) {
        this(type, key, (List<TagKey<?>>) (List<?>) tags);
        for (ItemStack item : items) {
            ingredients.add(EmiStack.of(item));
        }

        List<EmiStack> stacks = new ArrayList<>();
        ingredients.stream().map(EmiIngredient::getEmiStacks).forEach(stacks::addAll);
        ingredient = new TagEmiIngredient(key, stacks, 1);
    }

    public EmiEditingTagRecipe(TagRecipeType type, List<TagKey<Fluid>> tags, List<Fluid> fluids, TagKey<Fluid> key) {
        this(type, key, (List<TagKey<?>>) (List<?>) tags);
        for (Fluid fluid : fluids) {
            ingredients.add(EmiStack.of(fluid));
        }

        List<EmiStack> stacks = new ArrayList<>();
        ingredients.stream().map(EmiIngredient::getEmiStacks).forEach(stacks::addAll);
        ingredient = new TagEmiIngredient(key, stacks, 1);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        super.addWidgets(widgets);
        int page;
        if (widgets instanceof WidgetGroup group) {
            page = EmiViewerUtils.getPage(group);
        } else {
            page = 0;
        }
        type.lateInit(getIngredients(), (widgets.getHeight() - 42) / 18, page);
    }

    @Override
    protected List<EmiStack> getStacks() {
        return getIngredients().stream().map(i -> i instanceof EmiStack ? (EmiStack) i : new FakeEmiStack(i)).toList();
    }

    @Override
    protected EmiIngredient getIngredient() {
        return ingredient;
    }

    @Override
    protected EmiRecipe getRecipeContext(EmiStack stack, int offset) {
        return new EmiResolutionRecipe(ingredient, stack);
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(CraftTweakerGUI.MOD_ID, super.getId().getPath());
    }

    public List<EmiIngredient> getIngredients() {
        if (ingredients.isEmpty()) {
            return List.of(EmiStack.of(Items.BARRIER));
        }
        return ingredients;
    }
}
