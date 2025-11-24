package de.bommels05.ctgui.emi;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.ViewerUtils;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipeType;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiResolutionRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiRegistryAdapter;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.TagEmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.recipe.EmiTagRecipe;
import dev.emi.emi.registry.EmiTags;
import dev.emi.emi.screen.WidgetGroup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class EmiEditingTagRecipe extends EmiTagRecipe {

    private final List<EmiIngredient> ingredients = new ArrayList<>();
    private final EmiIngredient ingredient;
    private final TagRecipeType type;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public EmiEditingTagRecipe(TagRecipeType type, TagKey<?> key, List<? extends TagKey<?>> tags, List<?> entries) {
        super(key);
        this.type = type;
        for (TagKey<?> tag : tags) {
            List<EmiStack> values = EmiTags.getRawValues(tag);
            ingredients.add(new TagEmiIngredient(tag, values.isEmpty() ? List.of(EmiStack.of(Items.BARRIER)) : values, 1));
        }

        for (Object entry : entries) {
            if (entry instanceof Item item) {
                ingredients.add(EmiStack.of(item));
            } else {
                ingredients.add(((EmiRegistryAdapter) EmiTags.ADAPTERS_BY_REGISTRY.get(BuiltInRegistries.REGISTRY.get(key.registry().location()))).of(entry, DataComponentPatch.EMPTY, 1));
            }
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
        type.lateInit(getIngredients(), (widgets.getHeight() - 42) / 18, page, key.isFor(Registries.ITEM));
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
        return CraftTweakerGUI.rl(CraftTweakerGUI.MOD_ID, super.getId().getPath());
    }

    public List<EmiIngredient> getIngredients() {
        if (ingredients.isEmpty()) {
            return List.of(EmiStack.of(Items.BARRIER));
        }
        return ingredients;
    }
}
