package de.bommels05.ctgui.compat.farmersdelight;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.RangedRecipeOption;
import de.bommels05.ctgui.api.option.StringRecipeOption;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiStonecuttingRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.crafting.ingredient.ChanceResult;
import vectorwing.farmersdelight.integration.emi.recipe.CuttingEmiRecipe;

import java.util.Optional;

public class CuttingRecipeType extends SupportedRecipeType<CuttingBoardRecipe> {
    private final RangedRecipeOption<CuttingBoardRecipe> outputCount = new RangedRecipeOption<>(Component.translatable("ctgui.editing.options.output_count"), 1, 4);
    private final StringRecipeOption<CuttingBoardRecipe> sound = new StringRecipeOption<>(Component.translatable("ctgui.editing.options.farmers_delight.sound"), s -> s.isBlank() || ResourceLocation.tryParse(s) != null);

    public CuttingRecipeType() {
        super(CraftTweakerGUI.rl("farmersdelight:cutting"));

        initAreas();
        addOption(outputCount, (r, outputCount) -> {
            initAreas();
            return new CuttingBoardRecipe(r.getGroup(), r.getIngredients().get(0), r.getTool(),
                    CraftTweakerGUI.copyWithSize(r.getRollableResults(), ChanceResult.EMPTY, outputCount), r.getSoundEvent());
        });
        addOption(sound, (r, sound) -> {
            return new CuttingBoardRecipe(r.getGroup(), r.getIngredients().get(0), r.getTool(), r.getRollableResults(),
                    sound.isBlank() ? Optional.empty() : Optional.of(SoundEvent.createVariableRangeEvent(ResourceLocation.parse(sound))));
        });
    }

    private void initAreas() {
        clearAreas();
        addAreaEmptyRightClick(11, 19, 17, 17, (r, am) -> {
            return new CuttingBoardRecipe(r.getGroup(), am.ensureAmount(1, 1).ingredient(), r.getTool(), r.getRollableResults(), r.getSoundEvent());
        }, r -> {
            return new AmountedIngredient(r.getIngredients().get(0), 1);
        });
        addAreaEmptyRightClick(11, 0, 17, 17, (r, am) -> {
            return new CuttingBoardRecipe(r.getGroup(), r.getIngredients().get(0), am.ensureAmount(1, 1).ingredient(), r.getRollableResults(), r.getSoundEvent());
        }, r -> {
            return new AmountedIngredient(r.getTool(), 1);
        });

        int size = outputCount.get();
        int centerX = size > 1 ? 1 : 10;
        int centerY = size > 2 ? 1 : 10;

        for(int i = 0; i < size; ++i) {
            int xOffset = centerX + (i % 2 == 0 ? 0 : 19);
            int yOffset = centerY + i / 2 * 19;
            int slot = i;
            addAreaScrollAmountEmptyRightClick(69 + xOffset, 3 + yOffset, 17, 17, (r, am) -> {
                NonNullList<ChanceResult> results = CraftTweakerGUI.copyWithSize(r.getRollableResults(), ChanceResult.EMPTY);
                results.set(slot, new ChanceResult(am.asStack(), 1));
                return new CuttingBoardRecipe(r.getGroup(), r.getIngredients().get(0), r.getTool(), results, r.getSoundEvent());
            }, r -> {
                return AmountedIngredient.of(r.getResults().get(slot));
            });
        }
    }

    @Override
    public CuttingBoardRecipe onInitialize(CuttingBoardRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);
        if (recipe == null) {
            return new CuttingBoardRecipe("", Ingredient.EMPTY, Ingredient.EMPTY, NonNullList.withSize(1, ChanceResult.EMPTY), Optional.empty());
        }
        outputCount.set(recipe.getRollableResults().size());
        initAreas();
        sound.set(recipe.getSoundEvent().map(SoundEvent::getLocation).map(ResourceLocation::toString).orElse(""));
        return null;
    }

    @Override
    public boolean isValid(CuttingBoardRecipe recipe) {
        return !recipe.getIngredients().get(0).isEmpty() && recipe.getResults().stream().noneMatch(ItemStack::isEmpty);
    }

    @Override
    public Object getEmiRecipe(CuttingBoardRecipe recipe) throws UnsupportedViewerException {
        return new CuttingEmiRecipe(nullRl(), EmiIngredient.of(recipe.getTool()), EmiIngredient.of(recipe.getIngredients().get(0)), recipe.getRollableResults().stream().map((result) -> EmiStack.of(result.stack()).setChance(result.chance())).toList());
    }

    @Override
    public String getCraftTweakerString(CuttingBoardRecipe recipe, String id) {
        return getCTJsonString(recipe, id);
    }
}
