package de.bommels05.ctgui.api;

import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.List;

public class RecipeTypeManager {

    private static final List<SupportedRecipeType<?>> types = new ArrayList<>();

    /**
     * Returns if the given category id is supported by a registered recipe type
     * @param id The recipe viewer category id
     * @return If the category is supported
     */
    public static boolean isTypeSupported(ResourceLocation id) {
        return types.stream().anyMatch(type -> type.getId().equals(id));
    }

    /**
     * Returns the recipe type for the given category id
     * @param id The recipe viewer category id
     * @return The recipe type or null if not supported
     */
    public static SupportedRecipeType<?> getType(ResourceLocation id) {
        return types.stream().filter(type -> type.getId().equals(id)).findAny().orElse(null);
    }

    /**
     * Registers a new recipe type
     * This can technically be called at any time, but it should probably be done either in the mod constructor or in the {@link FMLClientSetupEvent}
     * @param type The recipe type to register
     */
    public static void addType(SupportedRecipeType<?> type) {
        types.add(type);
    }

    /**
     * Returns a list of all registered recipe types
     * @return A list of all registered recipe types
     */
    public static List<SupportedRecipeType<?>> getTypes() {
        return new ArrayList<>(types);
    }

}
