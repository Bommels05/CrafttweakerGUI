package de.bommels05.ctgui.compat.mekanism;

import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.stack.EmiRegistryAdapter;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.client.recipe_viewer.emi.ChemicalEmiStack;
import net.neoforged.fml.ModList;

public class MekanismEmiUtils {

    public static void init(EmiInitRegistry reg) {
        //Mekanism currently doesn't support add them in 1.20.4
        //This hopefully won't cause problems when they add support
        reg.addRegistryAdapter(EmiRegistryAdapter.simple(Gas.class, MekanismAPI.GAS_REGISTRY, (chemical, nbt, amount) -> new ChemicalEmiStack.GasEmiStack(chemical, amount)));
        reg.addRegistryAdapter(EmiRegistryAdapter.simple(InfuseType.class, MekanismAPI.INFUSE_TYPE_REGISTRY, (chemical, nbt, amount) -> new ChemicalEmiStack.InfusionEmiStack(chemical, amount)));
        reg.addRegistryAdapter(EmiRegistryAdapter.simple(Slurry.class, MekanismAPI.SLURRY_REGISTRY, (chemical, nbt, amount) -> new ChemicalEmiStack.SlurryEmiStack(chemical, amount)));
        reg.addRegistryAdapter(EmiRegistryAdapter.simple(Pigment.class, MekanismAPI.PIGMENT_REGISTRY, (chemical, nbt, amount) -> new ChemicalEmiStack.PigmentEmiStack(chemical, amount)));
    }

}
