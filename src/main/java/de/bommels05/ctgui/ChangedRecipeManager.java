package de.bommels05.ctgui;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import de.bommels05.ctgui.api.RecipeTypeManager;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.emi.EmiSupportedRecipe;
import dev.emi.emi.api.EmiApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ChangedRecipeManager {

    public static final Path CHANGE_FILE = FMLPaths.CONFIGDIR.get().resolve("ctgui/changed_recipes.snbt");
    public static final Path SCRIPT_FILE = FMLPaths.GAMEDIR.get().resolve("scripts/ctgui_generated.zs");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<ChangedRecipe<?>> changedRecipes = new ArrayList<>();
    private static long lastSave = System.currentTimeMillis();
    static {
        load();
    }

    public static void addChangedRecipe(ChangedRecipe<?> recipe, boolean inject) {
        changedRecipes.add(recipe);
        if (inject && !recipe.wasExported() && recipe.type != ChangedRecipe.Type.REMOVED) {
            CraftTweakerGUI.getViewerUtils().inject(recipe);
        }
        lastSave = 0;
    }

    public static void addChangedRecipe(ChangedRecipe<?> recipe) {
        addChangedRecipe(recipe, true);
    }

    public static void removeChangedRecipe(ChangedRecipe<?> recipe) {
        changedRecipes.remove(recipe);
        if (!recipe.wasExported()) {
            CraftTweakerGUI.getViewerUtils().unInject(recipe);
        }
        lastSave = 0;
    }

    public static List<ChangedRecipe<?>> getChangedRecipes() {
        return changedRecipes;
    }

    public static ChangedRecipe<?> getAffectingChange(ResourceLocation id) {
        return changedRecipes.stream().filter(change -> change.getOriginalId() != null && change.getOriginalId().equals(id)).findFirst().orElse(null);
    }

    public static boolean idAlreadyUsed(String id) {
        return changedRecipes.stream().anyMatch(changedRecipe -> changedRecipe.getId().equals(id));
    }

    public static void save() {
        if (System.currentTimeMillis() - lastSave < 60000) {
            return;
        }
        lastSave = System.currentTimeMillis();
        CompoundTag root = new CompoundTag();
        ListTag changes = new ListTag();
        for (ChangedRecipe<?> change : changedRecipes) {
            try {
                CompoundTag changeTag = new CompoundTag();
                changeTag.putString("type", change.type.name());
                changeTag.putBoolean("exported", change.exported);
                changeTag.putString("recipeType", change.getRecipeType().getId().toString());
                if (change.type == ChangedRecipe.Type.ADDED) {
                    changeTag.putString("id", change.id);
                } else if (change.type == ChangedRecipe.Type.CHANGED) {
                    changeTag.putString("id", change.id);
                    changeTag.putString("originalId", change.originalId.toString());
                } else {
                    changeTag.putString("originalId", change.originalId.toString());
                }
                RecipeSerializer<?> serializer = change.recipe.getSerializer();
                changeTag.putString("serializer", BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer).toString());
                changeTag.put("recipe", toTag(serializer, change.recipe));
                changes.add(changeTag);
            } catch (Throwable t) {
                LOGGER.error("Could not save recipe change, ignoring", t);
                Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PERIODIC_NOTIFICATION, Component.translatable("ctgui.saving_error_title"), Component.translatable("ctgui.error_message")));
            }
        }
        root.put("changes", changes);
        try {

            File old = CHANGE_FILE.getParent().resolve("changed_recipes.snbt.old").toFile();
            old.delete();
            CHANGE_FILE.toFile().renameTo(old);
            NbtToSnbt.writeSnbt(CachedOutput.NO_CACHE, CHANGE_FILE, NbtUtils.structureToSnbt(root));
            if (Config.saveToast) {
                Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PERIODIC_NOTIFICATION, Component.translatable("ctgui.changes_saved_title"), Component.translatable("ctgui.changes_saved")));
            }
            LOGGER.info("Saved " + changedRecipes.size() + " recipe changes");
        } catch (IOException e) {
            LOGGER.error("Could not save recipe changes", e);
            Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PERIODIC_NOTIFICATION, Component.translatable("ctgui.saving_error_title"), Component.translatable("ctgui.error_message")));
        }
    }

    public static void load() {
        int i = 0;
        if (CHANGE_FILE.toFile().exists()) {
            try {
                BufferedReader reader = Files.newBufferedReader(CHANGE_FILE);
                CompoundTag root = NbtUtils.snbtToStructure(IOUtils.toString(reader));
                ListTag changes = root.getList("changes", Tag.TAG_COMPOUND);
                for (Tag tag : changes) {
                    try {
                        CompoundTag change = (CompoundTag) tag;
                        ChangedRecipe.Type type = ChangedRecipe.Type.valueOf(change.getString("type"));
                        SupportedRecipeType<?> recipeType = RecipeTypeManager.getType(new ResourceLocation(change.getString("recipeType")));;
                        changedRecipes.add(new ChangedRecipe<>(type, type != ChangedRecipe.Type.REMOVED ? change.getString("id") : null,
                                type != ChangedRecipe.Type.ADDED ? new ResourceLocation(change.getString("originalId")) : null,
                                fromTag(BuiltInRegistries.RECIPE_SERIALIZER.get(new ResourceLocation(change.getString("serializer"))),
                                        change.get("recipe"), recipeType), recipeType, change.getBoolean("exported")));
                        i++;
                    } catch (Throwable t) {
                        LOGGER.error("Could not load recipe change, ignoring", t);
                    }
                }
            } catch (IOException | CommandSyntaxException e) {
                LOGGER.error("Could not load recipe changes", e);
            }
        }
        if (!SCRIPT_FILE.toFile().exists()) {
            changedRecipes.forEach(recipe -> recipe.setExported(false));
        }
        LOGGER.info("Loaded " + i + " recipe changes");
    }

    @SuppressWarnings("unchecked")
    private static <T extends Recipe<?>> Tag toTag(RecipeSerializer<?> serializer, Recipe<?> recipe) {
        Either<Tag, DataResult.PartialResult<Tag>> result = ((RecipeSerializer<T>) serializer).codec().encode((T) recipe, NbtOps.INSTANCE, null).get();
        if (result.left().isPresent()) {
            return result.left().get();
        }
        throw new RuntimeException("Recipe could not be serialized: " + result.right().map(DataResult.PartialResult::message).orElse(recipe.toString()));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Recipe<?>> T fromTag(RecipeSerializer<?> serializer, Tag tag, SupportedRecipeType<?> recipeType) {
        Either<Pair<T, Tag>, DataResult.PartialResult<Pair<T, Tag>>> result = ((RecipeSerializer<T>) serializer).codec().decode(NbtOps.INSTANCE, tag).get();
        if (result.left().isPresent()) {
            try {
                T recipe = result.left().get().getFirst();
                T processedRecipe = ((SupportedRecipeType<T>) recipeType).onInitialize(recipe);
                return processedRecipe == null ? recipe : processedRecipe;
            } catch (UnsupportedRecipeException e) {
                //Unsupported recipes shouldn't be able to get save, so we ignore it
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Recipe could not be deserialized: " + result.right().map(DataResult.PartialResult::message).orElse(tag.toString()));
    }

    public static void export() {
        File file = SCRIPT_FILE.toFile();
        file.delete();
        try {
            FileWriter writer = new FileWriter(file);
            writer.append("/*CraftTweaker GUI generated script\n");
            writer.append("  Not intended for manual editing\n");
            writer.append("  Changes will be overridden when exporting again*/\n").append("\n");
            writer.append("import crafttweaker.api.ingredient.type.IIngredientEmpty;\n\n");
            for (ChangedRecipe<?> change : changedRecipes) {
                if (change.type == ChangedRecipe.Type.REMOVED) {
                    writer.append(change.getCraftTweakerRemoveString());
                    writer.append("\n");
                } else if (change.type == ChangedRecipe.Type.CHANGED) {
                    writer.append(change.getCraftTweakerRemoveString());
                    writer.append(change.getCraftTweakerString() + "\n");
                    writer.append("\n");
                } else {
                    writer.append(change.getCraftTweakerString() + "\n");
                    writer.append("\n");
                }
                change.setExported(true);
            }

            writer.flush();
            writer.close();;
        } catch (Throwable t) {
            LOGGER.error("Could not export recipe changes", t);
            Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PERIODIC_NOTIFICATION, Component.translatable("ctgui.export_error_title"), Component.translatable("ctgui.error_message")));
        }
    }

    public static void reInjectAll() {
        for (ChangedRecipe<?> change : changedRecipes) {
            if (change.type != ChangedRecipe.Type.REMOVED && !change.wasExported()) {
                CraftTweakerGUI.getViewerUtils().inject(change);
            }
        }
    }

    public static class ChangedRecipe<T extends Recipe<?>> {

        private final Type type;
        private final String id;
        private final ResourceLocation originalId;
        private final T recipe;
        private final SupportedRecipeType<T> recipeType;
        private boolean exported;

        private ChangedRecipe(Type type, String id, ResourceLocation originalId, T recipe, SupportedRecipeType<T> recipeType) {
            this(type, id, originalId, recipe, recipeType, false);
        }

        @SuppressWarnings("unchecked")
        private ChangedRecipe(Type type, String id, ResourceLocation originalId, T recipe, SupportedRecipeType<T> recipeType, boolean exported) {
            this.type = type;
            this.id = id;
            this.originalId = originalId;
            this.recipe = recipe;
            this.recipeType = recipeType;
            this.exported = exported;
        }

        public static <T extends Recipe<?>> ChangedRecipe<T> added(String id, T recipe, SupportedRecipeType<T> recipeType) {
            return new ChangedRecipe<>(Type.ADDED, id, null, recipe, recipeType);
        }

        public static <T extends Recipe<?>> ChangedRecipe<T> changed(String id, ResourceLocation originalId, T recipe, SupportedRecipeType<T> recipeType) {
            return new ChangedRecipe<>(Type.CHANGED, id, originalId, recipe, recipeType);
        }

        public static <T extends Recipe<?>> ChangedRecipe<T> removed(ResourceLocation id, T recipe, SupportedRecipeType<T> recipeType) {
            return new ChangedRecipe<>(Type.REMOVED, null, id, recipe, recipeType);
        }

        public Component getTitle() {
            return Component.translatable("ctgui.list.change_title", type.getName(),
                    EmiApi.getRecipeManager().getCategories().stream().filter(category -> category.getId().equals(recipeType.getId())).findFirst().orElseThrow().getName());
        }

        public String getId() {
            if (type != Type.REMOVED) {
                return id;
            } else {
                return originalId.toString();
            }
        }

        public String getIcon() {
            if (type == Type.ADDED) {
                return "+";
            } else if (type == Type.CHANGED) {
                return "±";
            } else {
                return "-";
            }
        }

        public int getIconColor() {
            if (type == Type.ADDED) {
                return 65280;
            } else if (type == Type.CHANGED) {
                return 16762624;
            } else {
                return 16711680;
            }
        }

        public ItemStack getMainOutput() {
            return recipeType.getMainOutput(recipe);
        }

        public String getCraftTweakerString() {
            return recipeType.getCraftTweakerString(recipe, id);
        }

        public String getCraftTweakerRemoveString() {
            return recipeType.getCraftTweakerRemoveString(recipe, originalId);
        }

        public Type getType() {
            return type;
        }

        public String getNewId() {
            return id;
        }

        public ResourceLocation getOriginalId() {
            return originalId;
        }

        public T getRecipe() {
            return recipe;
        }

        public SupportedRecipeType<T> getRecipeType() {
            return recipeType;
        }

        public SupportedRecipe<T, ? extends SupportedRecipeType<T>> toSupportedRecipe() {
            try {
                return new EmiSupportedRecipe<>(recipeType.getEmiRecipe(recipe));
            } catch (UnsupportedViewerException e) {
                //todo handle viewers
                throw new RuntimeException(e);
            }
        }

        public void setExported(boolean exported) {
            this.exported = exported;
        }

        public boolean wasExported() {
            return exported;
        }

        public enum Type {
            ADDED,
            CHANGED,
            REMOVED;

            public Component getName() {
                return Component.translatable("ctgui." + name().toLowerCase());
            }


        }

    }

}
