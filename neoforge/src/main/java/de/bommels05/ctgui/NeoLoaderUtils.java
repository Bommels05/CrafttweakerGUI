package de.bommels05.ctgui;

import com.blamejared.crafttweaker.api.fluid.CTFluidIngredient;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.tag.CraftTweakerTagRegistry;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import de.bommels05.ctgui.api.FluidAmountedIngredient;
import de.bommels05.ctgui.api.SpecialAmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.compat.mekanism.ChemicalAmountedIngredient;
import de.bommels05.ctgui.compat.mekanism.MekanismRecipeUtils;
import de.bommels05.ctgui.compat.minecraft.custom.TagRecipe;
import de.bommels05.ctgui.registry.RecipeSerializers;
import de.bommels05.ctgui.registry.RecipeTypes;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.stack.EmiRegistryAdapter;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.FluidEmiStack;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.client.recipe_viewer.emi.ChemicalEmiStack;
import mekanism.common.recipe.upgrade.MekanismShapedRecipe;
import mekanism.common.registries.MekanismRecipeSerializersInternal;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.nio.file.Path;

public class NeoLoaderUtils implements LoaderUtils {

    @Override
    public boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    @Override
    public void setEditMode(boolean value) {
        NeoConfig.setEditMode(value);
    }

    @Override
    public void setListButton(boolean value) {
        NeoConfig.setListButton(value);
    }

    @Override
    public RecipeSerializer<TagRecipe> getTagRecipeSerializer() {
        return RecipeSerializers.TAG.get();
    }

    @Override
    public RecipeType<TagRecipe> getTagRecipeType() {
        return RecipeTypes.TAG.get();
    }

    @Override
    public <T> Object stackFromType(T type) {
        if (type instanceof Fluid fluid) {
            return new FluidStack(fluid, 1);
        } else if (ModList.get().isLoaded("mekanism") && type instanceof Chemical<?> chemical) {
            return MekanismRecipeUtils.from(chemical, 1);
        }
        return type;
    }

    @Override
    public SpecialAmountedIngredient<?, ?> getRightImplementation(SpecialAmountedIngredient<?, ?> ingredient) {
        if (ingredient.isStack()) {
            if (ingredient.getStack() instanceof FluidStack stack) {
                return new FluidAmountedIngredient(stack, ingredient.shouldUseAmount() ? ingredient.getAmount() : stack.getAmount());
            } else if (ModList.get().isLoaded("mekanism") && ingredient.getStack() instanceof ChemicalStack<?> stack) {
                return new ChemicalAmountedIngredient<>(stack, ingredient.shouldUseAmount() ? ingredient.getAmount() : (int) stack.getAmount());
            }
        } else {
            TagKey<?> tag = ingredient.getTag();
            if (tag.isFor(Registries.FLUID)) {
                return new FluidAmountedIngredient((TagKey<Fluid>) tag, ingredient.getAmount());
            } else if (ModList.get().isLoaded("mekanism") && (
                    tag.isFor(MekanismAPI.GAS_REGISTRY_NAME) ||
                            tag.isFor(MekanismAPI.INFUSE_TYPE_REGISTRY_NAME) ||
                            tag.isFor(MekanismAPI.SLURRY_REGISTRY_NAME) ||
                            tag.isFor(MekanismAPI.PIGMENT_REGISTRY_NAME))) {
                return new ChemicalAmountedIngredient(tag, ingredient.getAmount());
            }
        }
        return ingredient;
    }

    @Override
    public MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public <S> Object getEmiIngredient(S stack) {
        if (stack instanceof FluidStack fluidStack) {
            return EmiStack.of(fluidStack.getFluid(), fluidStack.getAmount());
        } else if (ModList.get().isLoaded("mekanism")) {
            if (stack instanceof ChemicalStack<?> chemicalStack) {
                return ChemicalEmiStack.create(chemicalStack);
            }
        }
        throw new IllegalArgumentException("Unsupported ingredient");
    }

    @Override
    public Object getFromEmiStack(Object stack) {
        if (stack instanceof FluidEmiStack fluidStack) {
            return new FluidStack((Fluid) fluidStack.getKey(), fluidStack.getAmount() == 0 ? 1 : (int) fluidStack.getAmount());
        }
        if (ModList.get().isLoaded("mekanism") && stack instanceof ChemicalEmiStack<?> emiStack) {
            return MekanismRecipeUtils.from(emiStack.getKey(), emiStack.getAmount());
        }
        return null;
    }

    @Override
    public void emiInit(Object registry) {
        EmiInitRegistry reg = (EmiInitRegistry) registry;
        //Mekanism currently doesn't support add them in 1.20.4
        //This hopefully won't cause problems when they add support
        if (ModList.get().isLoaded("mekanism")) {
            reg.addRegistryAdapter(EmiRegistryAdapter.simple(Gas.class, MekanismAPI.GAS_REGISTRY, (chemical, nbt, amount) -> new ChemicalEmiStack.GasEmiStack(chemical, amount)));
            reg.addRegistryAdapter(EmiRegistryAdapter.simple(InfuseType.class, MekanismAPI.INFUSE_TYPE_REGISTRY, (chemical, nbt, amount) -> new ChemicalEmiStack.InfusionEmiStack(chemical, amount)));
            reg.addRegistryAdapter(EmiRegistryAdapter.simple(Slurry.class, MekanismAPI.SLURRY_REGISTRY, (chemical, nbt, amount) -> new ChemicalEmiStack.SlurryEmiStack(chemical, amount)));
            reg.addRegistryAdapter(EmiRegistryAdapter.simple(Pigment.class, MekanismAPI.PIGMENT_REGISTRY, (chemical, nbt, amount) -> new ChemicalEmiStack.PigmentEmiStack(chemical, amount)));
        }
    }

    @Override
    public ShapedRecipe tryGetFromMekanismRecipe(Recipe<?> recipe) {
        if (ModList.get().isLoaded("mekanism") && recipe instanceof MekanismShapedRecipe r) {
            return r.getInternal();
        }
        return null;
    }

    @Override
    public String getMekanismCraftTweakerString(ShapedRecipe recipe, String id) {
        JsonElement json = MekanismRecipeSerializersInternal.MEK_DATA.get().codec().encode(new MekanismShapedRecipe(recipe), JsonOps.INSTANCE, null).getOrThrow(false, s -> {});
        json.getAsJsonObject().add("type", new JsonPrimitive("mekanism:mek_data"));
        return "<recipetype:minecraft:crafting>.addJsonRecipe(\"" + id + "\", " + json + ");";
    }

    /**
     * Returns the CraftTweaker representation of the fluid stack
     * @param stack The fluid stack
     * @return The CraftTweaker representation of the fluid stack
     */
    public static String getCTString(FluidStack stack) {
        return IFluidStack.of(stack).getCommandString();
    }

    /**
     * Returns the CraftTweaker representation of the fluid ingredient
     * @param stack The fluid ingredient
     * @return The CraftTweaker representation of the fluid ingredient
     */
    public static String getCTString(FluidAmountedIngredient stack) {
        if (stack.isStack()) {
            return IFluidStack.of(stack.shouldUseAmount() ? stack.getStack().copyWithAmount(stack.getAmount()) : stack.getStack()).getCommandString();
        } else {
            return new CTFluidIngredient.FluidTagWithAmountIngredient(CraftTweakerTagRegistry.INSTANCE.knownTagManager(Registries.FLUID).tag(stack.getTag()).withAmount(stack.getRightAmount())).getCommandString();
        }
    }

    /**
     * Function to use in a scroll amount area with fluid stacks
     * @param stack The original fluid stack
     * @param up Whether to increase or decrease the amount
     * @return A new fluid stack with the amount changed
     */
    public static FluidAmountedIngredient fluidAmountSetter(FluidAmountedIngredient stack, boolean up) {
        return stack.withAmount(Math.max(1, (stack.getRightAmount() == 1 ? (Screen.hasControlDown() ? 1 : 0) : stack.getRightAmount()) + SupportedRecipeType.getFluidScrollAmount(up)));
    }

    /**
     * Function to use in a scroll amount area with fluids that only changes the amount by 1
     * @param stack The original fluid stack
     * @param up Whether to increase or decrease the amount
     * @return A new fluid stack with the amount changed by 1
     */
    public static FluidAmountedIngredient limitedFluidAmountSetter(FluidAmountedIngredient stack, boolean up) {
        return stack.withAmount(Math.max(1, stack.getRightAmount() + (up ? 1 : -1)));
    }
}
