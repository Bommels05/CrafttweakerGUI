package de.bommels05.ctgui.api;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class RecipeTypeManager {

    private static final List<SupportedRecipeType<?>> types = new ArrayList<>();

    public static boolean isTypeSupported(ResourceLocation id) {
        return types.stream().anyMatch(type -> type.getId().equals(id));
    }

    public static SupportedRecipeType<?> getType(ResourceLocation id) {
        return types.stream().filter(type -> type.getId().equals(id)).findAny().orElse(null);
    }

    public static void addType(SupportedRecipeType<?> type) {
        types.add(type);
    }

    public static List<SupportedRecipeType<?>> getTypes() {
        return new ArrayList<>(types);
    }

}
