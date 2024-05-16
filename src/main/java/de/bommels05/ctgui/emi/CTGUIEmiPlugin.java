package de.bommels05.ctgui.emi;

import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import de.bommels05.ctgui.ChangedRecipeManager;
import de.bommels05.ctgui.Config;
import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.ViewerUtils;
import de.bommels05.ctgui.compat.mekanism.MekanismRecipeUtils;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipe;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import de.bommels05.ctgui.api.AmountedIngredient;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiRegistryAdapter;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.FluidEmiStack;
import dev.emi.emi.api.widget.Bounds;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.client.recipe_viewer.emi.ChemicalEmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

@EmiEntrypoint
public class CTGUIEmiPlugin implements EmiPlugin {

    @Override
    @SuppressWarnings({"unchecked"})
    public void register(EmiRegistry registry) {
        registry.addExclusionArea(RecipeEditScreen.class, (screen, consumer) -> consumer.accept(
                new Bounds(screen.getMinX(), screen.getMinY(), screen.getMaxX() - screen.getMinX(), screen.getMaxY() - screen.getMinY())));
        registry.addExclusionArea(RecipeEditScreen.class, (screen, consumer) -> consumer.accept(
                new Bounds(screen.getOptionsMinX(), screen.getOptionsMinY(), screen.getOptionsMaxX() - screen.getOptionsMinX(), screen.getOptionsMaxY() - screen.getOptionsMinY())));
        registry.addExclusionArea(RecipeEditScreen.class, (screen, consumer) -> consumer.accept(
                new Bounds(screen.getTagMinX(), screen.getTagMinY(), screen.getTagMaxX() - screen.getTagMinX(), screen.getTagMaxY() - screen.getTagMinY())));
        registry.addDragDropHandler(RecipeEditScreen.class, (screen, stack, x, y) -> {
            AmountedIngredient ingredient;
            if (stack instanceof FluidEmiStack fluidStack) {
                screen.handleDragAndDropSpecial(x, y, new FluidStack((Fluid) fluidStack.getKey(), fluidStack.getAmount() == 0 ? 1 : (int) fluidStack.getAmount()));
                return true;
            }
            if (ModList.get().isLoaded("mekanism") && stack instanceof ChemicalEmiStack<?> emiStack) {
                ChemicalStack<?> chemicalStack = MekanismRecipeUtils.from(emiStack.getKey(), emiStack.getAmount());
                screen.handleDragAndDropSpecial(x, y, chemicalStack);
                return true;
            }
            ingredient = new AmountedIngredient(Ingredient.of(stack.getEmiStacks().stream().map(EmiStack::getItemStack)), (int) stack.getAmount());
            screen.handleDragAndDrop(x, y, ingredient);
            return true;
        });
        registry.addRecipeDecorator((recipe, widgets) -> {
            ChangedRecipeManager.ChangedRecipe<?> change = ChangedRecipeManager.getAffectingChange(recipe.getId());
            if (change != null && !(change.getRecipe() instanceof TagRecipe && change.wasExported())) {
                widgets.addDrawable(0, 0, recipe.getDisplayWidth(), recipe.getDisplayHeight(), (graphics, mouseX, mouseY, delta) -> {
                    graphics.fill(0, 0, recipe.getDisplayWidth(), recipe.getDisplayHeight(), FastColor.ARGB32.color(157, 148, 60, 60));
                });
                widgets.addTooltipText(List.of(Component.translatable(change.getType() == ChangedRecipeManager.ChangedRecipe.Type.CHANGED ? "ctgui.recipe_changed" : "ctgui.recipe_removed")), 0, 0, recipe.getDisplayWidth(), recipe.getDisplayHeight());
            } else if (recipe.getId() != null) {
                int width = Minecraft.getInstance().font.width("+");
                int x = recipe.getDisplayWidth() + (1 + width) - 8;
                if (recipe.getId().getNamespace().equals(CraftTweakerGUI.MOD_ID)) {
                    widgets.addDrawable(x, 1, width, 9, (graphics, mouseX, mouseY, delta) -> {
                        graphics.drawString(Minecraft.getInstance().font, "+", 0, -6, 65280, false);
                    });
                    widgets.addTooltipText(List.of(Component.translatable("ctgui.recipe_added")), x, -5, width, 6);
                } else if (Config.customRecipeIndicator && recipe.getId().getNamespace().equals(CraftTweakerConstants.MOD_ID)) {
                    widgets.addDrawable(x, 1, width, 9, (graphics, mouseX, mouseY, delta) -> {
                        graphics.drawString(Minecraft.getInstance().font, "+", 0, -6, 16762624, false);
                    });
                    widgets.addTooltipText(List.of(Component.translatable("ctgui.recipe_custom")), x, -5, width, 6);
                }
            }
        });
    }

    @Override
    public void initialize(EmiInitRegistry registry) {
        //Mekanism currently doesn't support add them in 1.20.4
        //This hopefully won't cause problems when they add support
        if (ModList.get().isLoaded("mekanism")) {
            registry.addRegistryAdapter(EmiRegistryAdapter.simple(Gas.class, MekanismAPI.GAS_REGISTRY, (chemical, nbt, amount) -> new ChemicalEmiStack.GasEmiStack(chemical, amount)));
            registry.addRegistryAdapter(EmiRegistryAdapter.simple(InfuseType.class, MekanismAPI.INFUSE_TYPE_REGISTRY, (chemical, nbt, amount) -> new ChemicalEmiStack.InfusionEmiStack(chemical, amount)));
            registry.addRegistryAdapter(EmiRegistryAdapter.simple(Slurry.class, MekanismAPI.SLURRY_REGISTRY, (chemical, nbt, amount) -> new ChemicalEmiStack.SlurryEmiStack(chemical, amount)));
            registry.addRegistryAdapter(EmiRegistryAdapter.simple(Pigment.class, MekanismAPI.PIGMENT_REGISTRY, (chemical, nbt, amount) -> new ChemicalEmiStack.PigmentEmiStack(chemical, amount)));
        }
    }
}
