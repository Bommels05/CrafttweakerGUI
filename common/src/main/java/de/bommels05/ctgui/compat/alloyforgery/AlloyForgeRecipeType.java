package de.bommels05.ctgui.compat.alloyforgery;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.IntegerRecipeOption;
import de.bommels05.ctgui.api.option.RangedRecipeOption;
import dev.emi.emi.api.recipe.EmiRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import wraith.alloyforgery.compat.emi.AlloyForgeryEmiRecipe;
import wraith.alloyforgery.recipe.AlloyForgeRecipe;
import wraith.alloyforgery.recipe.OutputData;
import wraith.alloyforgery.recipe.RawAlloyForgeRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class AlloyForgeRecipeType extends SupportedRecipeType<AlloyForgeRecipe> {
    private final IntegerRecipeOption<AlloyForgeRecipe> fuelPerTick = new IntegerRecipeOption<>(Component.translatable("ctgui.editing.options.alloy_forgery.fuel_per_tick"), 0);
    private final IntegerRecipeOption<AlloyForgeRecipe> minForgeTier = new IntegerRecipeOption<>(Component.translatable("ctgui.editing.options.alloy_forgery.min_forge_tier"), 1);
    private final RangedRecipeOption<AlloyForgeRecipe> overrideCount = new RangedRecipeOption<>(Component.translatable("ctgui.editing.options.alloy_forgery.override_count"), 0, 10);
    private int currentPage = 0;

    public AlloyForgeRecipeType() {
        super(CraftTweakerGUI.rl("alloy_forgery:alloy_forge"));

        for(int i = 0; i < 10; i++) {
            int x = 7 + i % 5 * 18;
            int y = 35 + i / 5 * 18;
            int slot = i;

            addAreaScrollAmountEmptyRightClick(x, y, 18, 18, (r, am) -> {
                return createRecipe(CraftTweakerGUI.setMapEntry(r.getIngredientsMap(), slot, am.ingredient(), am.amount(), am.isEmpty()),
                        r.getBaseResult(), r.getMinForgeTier(), r.getFuelPerTick(), r.getTierOverrides());
            }, r -> {
                if (r.getIngredientsMap().size() > slot) {
                    Map.Entry<Ingredient, Integer> ingredient = new ArrayList<>(r.getIngredientsMap().entrySet()).get(slot);
                    return new AmountedIngredient(ingredient.getKey(), ingredient.getValue());
                } else {
                    return AmountedIngredient.empty();
                }
            });
        }

        addAreaScrollAmountEmptyRightClick(104, 38, 25, 25, (r, am) -> {
            if (currentPage > 0) {
                return createRecipe(r.getIngredientsMap(), r.getBaseResult(), r.getMinForgeTier(), r.getFuelPerTick(),
                        CraftTweakerGUI.setMapEntry(r.getTierOverrides(), currentPage - 1, null, am.asStack(), false));
            }
            return createRecipe(r.getIngredientsMap(), am.asStack(), r.getMinForgeTier(), r.getFuelPerTick(), r.getTierOverrides());
        }, r -> {
            if (currentPage > 0) {
                return AmountedIngredient.of(r.getTierOverrides().values().asList().get(currentPage - 1));
            }
            return AmountedIngredient.of(r.getBaseResult());
        });

        addOption(fuelPerTick, (r, fuelPerTick) -> {
            return createRecipe(r.getIngredientsMap(), r.getBaseResult(), r.getMinForgeTier(), fuelPerTick, r.getTierOverrides());
        });
        addOption(minForgeTier, (r, minForgeTier) -> {
            if (currentPage > 0) {
                return createRecipe(r.getIngredientsMap(), r.getBaseResult(), r.getMinForgeTier(), r.getFuelPerTick(),
                        CraftTweakerGUI.setMapEntry(r.getTierOverrides(), currentPage - 1, new AlloyForgeRecipe.OverrideRange(minForgeTier), null, false));
            }
            return createRecipe(r.getIngredientsMap(), r.getBaseResult(), minForgeTier, r.getFuelPerTick(), r.getTierOverrides());
        }, (r, minForgeTier) -> {
            int i = 1;
            for (AlloyForgeRecipe.OverrideRange range : r.getTierOverrides().keySet()) {
                if (range.lowerBound() == minForgeTier && i != currentPage) {
                    return false;
                }
                i++;
            }
            return true;
        });
        addOption(overrideCount, (r, overrideCount) -> {
            currentPage = 0;
            minForgeTier.set(r.getMinForgeTier());
            ArrayList<Map.Entry<AlloyForgeRecipe.OverrideRange, ItemStack>> oldOverrides = new ArrayList<>(r.getTierOverrides().entrySet());
            HashMap<AlloyForgeRecipe.OverrideRange, ItemStack> overrides = new HashMap<>();
            for (int i = 0; i < overrideCount; i++) {
                if (oldOverrides.size() > i) {
                    Map.Entry<AlloyForgeRecipe.OverrideRange, ItemStack> override = oldOverrides.get(i);
                    overrides.put(override.getKey(), override.getValue());
                } else {
                    overrides.put(new AlloyForgeRecipe.OverrideRange(2 + i), ItemStack.EMPTY);
                }
            }
            return createRecipe(r.getIngredientsMap(), r.getBaseResult(), r.getMinForgeTier(), r.getFuelPerTick(),overrides);
        });
    }

    @Override
    public AlloyForgeRecipe onInitialize(AlloyForgeRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        currentPage = 0;

        if (recipe == null) {
            fuelPerTick.set(1);
            return createRecipe(Map.of(), ItemStack.EMPTY, 1, 1, Map.of());
        }
        if (recipe.secondaryID().orElse(nullRl()).equals(CraftTweakerGUI.rl("blasting_error_marker"))) {
            throw new UnsupportedRecipeException(Component.translatable("ctgui.editing.vanilla_blasting_recipe"));
        }

        fuelPerTick.set(recipe.getFuelPerTick());
        minForgeTier.set(recipe.getMinForgeTier());
        overrideCount.set(recipe.getTierOverrides().size());

        return null;
    }

    @Override
    public boolean isValid(AlloyForgeRecipe recipe) {
        return !recipe.getIngredientsMap().isEmpty() && !recipe.getBaseResult().isEmpty() && recipe.getTierOverrides().values().stream().noneMatch(ItemStack::isEmpty);
    }

    @Override
    public Object getEmiRecipe(AlloyForgeRecipe recipe) throws UnsupportedViewerException {
        return new AlloyForgeryEmiRecipe(new RecipeHolder<>(null, recipe));
    }

    @Override
    public Function<EmiRecipe, AlloyForgeRecipe> getAlternativeEmiRecipeGetter() {
        return recipe -> {
            if (recipe.getBackingRecipe() != null) {
                if (recipe.getBackingRecipe().value() instanceof AlloyForgeRecipe r) {
                    return r;
                }
                return new AlloyForgeRecipe(Map.of(), ItemStack.EMPTY, 1, 1, Map.of(), Optional.of(CraftTweakerGUI.rl("blasting_error_marker")));
            }
            return null;
        };
    }

    @Override
    public String getCraftTweakerString(AlloyForgeRecipe recipe, String id) {
        return getCTJsonString(recipe, id);
    }

    @Override
    public AlloyForgeRecipe makeOriginalRecipeSavable(AlloyForgeRecipe r) {
        return createRecipe(r.getIngredientsMap(), r.getBaseResult(), r.getMinForgeTier(), r.getFuelPerTick(), r.getTierOverrides());
    }

    public void setCurrentPage(int currentPage, int minForgeTier) {
        this.currentPage = currentPage;
        if (this.minForgeTier.get() != minForgeTier) {
            this.minForgeTier.set(minForgeTier);
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    private AlloyForgeRecipe createRecipe(Map<Ingredient, Integer> inputs, ItemStack output, int minForgeTier, int fuelPerTick, Map<AlloyForgeRecipe.OverrideRange, ItemStack> overrides) {
        Map<AlloyForgeRecipe.OverrideRange, AlloyForgeRecipe.PendingOverride> pendingOverrides = new HashMap<>();
        overrides.forEach((range, stack) -> pendingOverrides.put(range,
                new AlloyForgeRecipe.PendingOverride(stack.getItem(), stack.getCount(), stack.getComponentsPatch())));

        OutputData outputData = new OutputData(output.getCount(), output.getComponentsPatch(), output.getItem(), null, null);
        RawAlloyForgeRecipe raw = new RawAlloyForgeRecipe(inputs, outputData, minForgeTier, fuelPerTick, pendingOverrides);
        return new AlloyForgeRecipe(Optional.of(raw), inputs, output, minForgeTier, fuelPerTick, overrides);
    }
}
