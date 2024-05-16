package de.bommels05.ctgui;

import com.mojang.datafixers.util.Either;
import de.bommels05.ctgui.api.SpecialAmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.compat.mekanism.MekanismRecipeUtils;
import de.bommels05.ctgui.jei.JeiViewerUtils;
import mekanism.api.chemical.Chemical;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.stream.Streams;

import java.util.List;

public interface ViewerUtils<R> {

    public <T extends Recipe<?>> void inject(ChangedRecipeManager.ChangedRecipe<T> recipe);

    public <T extends Recipe<?>> void unInject(ChangedRecipeManager.ChangedRecipe<T> recipe);

    public boolean isCustomTagRecipe(R recipe);

    public Component getCategoryName(ResourceLocation id);

    public <R2 extends Recipe<?>, T extends SupportedRecipeType<R2>> SupportedRecipe<R2, T> toSupportedRecipe(R recipe);

    public default <R2 extends Recipe<?>, T extends SupportedRecipeType<R2>> SupportedRecipe<R2, T> toSupportedRecipe(T type, R2 recipe) throws UnsupportedViewerException {
        return toSupportedRecipe(getViewerRecipe(type, recipe));
    };

    public <R2 extends Recipe<?>> R getViewerRecipe(SupportedRecipeType<R2> type, R2 recipe) throws UnsupportedViewerException;

    public ViewerSlot newSlot(Ingredient ingredient, int x, int y);

    public ViewerSlot newSlot(ItemStack stack, int x, int y);

    public <S, T> ViewerSlot newSlotSpecial(SpecialAmountedIngredient<S, T> ingredient, int x, int y);

    public <S, T> void renderIngredientSpecial(SpecialAmountedIngredient<S, T> ingredient, GuiGraphics graphics, int x, int y, float partialTick);

    //Rendering
    public boolean keyPressed(int keyCode, int scanCode, int modifiers);

    public boolean charTyped(char c, int modifiers);

    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY);

    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton);

    public boolean mouseScrolled(double mouseX, double mouseY, double amount);

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton);

    public void renderStart(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    public void renderEnd(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    public void init(Screen screen);

    @SuppressWarnings("unchecked")
    public static <S, T, RT extends Registry<?>, RT2 extends Registry<T>> List<S> of(TagKey<T> tag) {
        return (List<S>) Streams.of(((RT2) ((Registry<RT>) BuiltInRegistries.REGISTRY).get((ResourceKey<RT>) tag.registry())).getTagOrEmpty(tag)).map(Holder::value).map(ViewerUtils::stackFromType).toList();
    }

    public static <T> Object stackFromType(T type) {
        if (type instanceof ItemLike item) {
            return new ItemStack(item);
        } else if (type instanceof Fluid fluid) {
            return new FluidStack(fluid, 1);
        } else if (ModList.get().isLoaded("mekanism") && type instanceof Chemical<?> chemical) {
            return MekanismRecipeUtils.from(chemical, 1);
        }
        return type;
    }

}
