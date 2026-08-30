package de.bommels05.ctgui;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import de.bommels05.ctgui.api.RecipeTypeManager;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ChangedRecipeManager {
    public static final Path CHANGE_FILE = CraftTweakerGUI.getLoaderUtils().getConfigDir().resolve("ctgui/changed_recipes.json");
    public static final Path OLD_CHANGE_FILE = CHANGE_FILE.resolveSibling("changed_recipes.json.old");
    public static final Path LEGACY_CHANGE_FILE = CHANGE_FILE.resolveSibling("changed_recipes.snbt");
    public static final Path SCRIPT_FILE = CraftTweakerGUI.getLoaderUtils().getGameDir().resolve("scripts/ctgui_generated.zs");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<ChangedRecipe<?>> changedRecipes = new ArrayList<>();
    private static boolean savedOld = false;
    static {
        load();
        if (CraftTweakerGUI.isJeiActive()) {
            reInjectAll();
        }
    }

    public static void addChangedRecipe(ChangedRecipe<?> recipe, boolean inject) {
        changedRecipes.add(recipe);
        if (inject && !recipe.wasExported() && recipe.type != ChangedRecipe.Type.REMOVED) {
            CraftTweakerGUI.getViewerUtils().inject(recipe);
        }
        save();
    }

    public static void addChangedRecipe(ChangedRecipe<?> recipe) {
        addChangedRecipe(recipe, true);
    }

    public static void removeChangedRecipe(ChangedRecipe<?> recipe) {
        changedRecipes.remove(recipe);
        if (!recipe.wasExported() && recipe.getType() != ChangedRecipe.Type.REMOVED) {
            CraftTweakerGUI.getViewerUtils().unInject(recipe);
        }
        save();
    }

    public static List<ChangedRecipe<?>> getChangedRecipes() {
        return changedRecipes;
    }

    public static ChangedRecipe<?> getAffectingChange(ResourceLocation id) {
        return changedRecipes.stream().filter(change -> change.getOriginalId() != null && change.getOriginalId().equals(id)).findFirst().orElse(null);
    }

    public static boolean idAlreadyUsed(String id) {
        return changedRecipes.stream().anyMatch(changedRecipe -> changedRecipe.getId().replace("minecraft:", "").equals(id.replace("minecraft:", "")));
    }

    public static void save() {
        JsonObject root = new JsonObject();
        JsonArray changes = new JsonArray();
        for (ChangedRecipe<?> change : changedRecipes) {
            try {
                JsonObject changeTag = new JsonObject();
                changeTag.addProperty("type", change.type.name());
                changeTag.addProperty("exported", change.exported);
                changeTag.addProperty("recipeType", change.getRecipeType().getId().toString());
                if (change.type == ChangedRecipe.Type.ADDED) {
                    changeTag.addProperty("id", change.id);
                } else if (change.type == ChangedRecipe.Type.CHANGED) {
                    changeTag.addProperty("id", change.id);
                    changeTag.addProperty("originalId", change.originalId.toString());
                } else {
                    changeTag.addProperty("originalId", change.originalId.toString());
                }
                String serializerName = BuiltInRegistries.RECIPE_SERIALIZER.getKey(change.recipe.getSerializer()).toString();
                changeTag.addProperty("serializer", serializerName);
                changeTag.add("recipe", toJson(change.recipe));
                if (change.type == ChangedRecipe.Type.CHANGED && change.originalRecipe != null) {
                    String oldSerializerName = BuiltInRegistries.RECIPE_SERIALIZER.getKey(change.originalRecipe.getSerializer()).toString();
                    if (!serializerName.equals(oldSerializerName)) {
                        changeTag.addProperty("oldSerializer", oldSerializerName);
                    }
                    changeTag.add("originalRecipe", toJson(change.originalRecipe));
                }
                changes.add(changeTag);
            } catch (Throwable t) {
                LOGGER.error("Could not save recipe change, ignoring", t);
                toastWithChat(Component.translatable("ctgui.saving_error_title"), Component.translatable("ctgui.error_message"));
            }
        }
        root.add("changes", changes);
        try {
            File old = OLD_CHANGE_FILE.toFile();
            if (!savedOld) {
                //Only backup the changes once per session. Else the file is saved too often to be useful, especially in singleplayer
                old.delete();
                CHANGE_FILE.toFile().renameTo(old);
                savedOld = true;
            } else {
                CHANGE_FILE.toFile().delete();
            }

            FileWriter writer = new FileWriter(CHANGE_FILE.toFile());
            GSON.toJson(root, writer);
            writer.close();

            if (Config.saveToast) {
                toastWithChat(Component.translatable("ctgui.changes_saved_title"), Component.translatable("ctgui.changes_saved"));
            }
            LOGGER.info("Saved " + changedRecipes.size() + " recipe changes");
        } catch (IOException e) {
            LOGGER.error("Could not save recipe changes", e);
            toastWithChat(Component.translatable("ctgui.saving_error_title"), Component.translatable("ctgui.error_message"));
        }
    }

    public static void load() {
        int i = 0;
        if (CHANGE_FILE.toFile().exists()) {
            try {
                FileReader reader = new FileReader(CHANGE_FILE.toFile());
                JsonObject root = GSON.fromJson(reader, JsonObject.class);
                reader.close();
                JsonArray changes = root.getAsJsonArray("changes");
                for (JsonElement tag : changes) {
                    try {
                        JsonObject change = tag.getAsJsonObject();
                        ChangedRecipe.Type type = ChangedRecipe.Type.valueOf(change.get("type").getAsString());
                        SupportedRecipeType<?> recipeType = RecipeTypeManager.getType(CraftTweakerGUI.rl(change.get("recipeType").getAsString()));;
                        RecipeSerializer<?> serializer = BuiltInRegistries.RECIPE_SERIALIZER.get(CraftTweakerGUI.rl(change.get("serializer").getAsString()));
                        RecipeSerializer<?> oldSerializer = change.has("oldSerializer") ? BuiltInRegistries.RECIPE_SERIALIZER.get(CraftTweakerGUI.rl(change.get("oldSerializer").getAsString())) : serializer;
                        changedRecipes.add(new ChangedRecipe<>(type, type != ChangedRecipe.Type.REMOVED ? change.get("id").getAsString() : null,
                                type != ChangedRecipe.Type.ADDED ? CraftTweakerGUI.rl(change.get("originalId").getAsString()) : null,
                                fromTag(serializer, JsonOps.INSTANCE, change.get("recipe"), recipeType),
                                type == ChangedRecipe.Type.CHANGED && change.has("originalRecipe") ? fromTag(oldSerializer, JsonOps.INSTANCE, change.get("originalRecipe"), recipeType) : null,
                                recipeType, change.get("exported").getAsBoolean()));
                        i++;
                    } catch (Throwable t) {
                        LOGGER.error("Could not load recipe change, ignoring", t);
                        toastWithChat(Component.translatable("ctgui.loading_error_title"), Component.translatable("ctgui.error_message"));
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Could not load recipe changes", e);
                toastWithChat(Component.translatable("ctgui.loading_error_title"), Component.translatable("ctgui.error_message"));
            }
        } else if (LEGACY_CHANGE_FILE.toFile().exists()) {
            LOGGER.info("Loading recipe changes in SNBT format");
            try {
                BufferedReader reader = Files.newBufferedReader(LEGACY_CHANGE_FILE);
                CompoundTag root = NbtUtils.snbtToStructure(IOUtils.toString(reader));
                ListTag changes = root.getList("changes", Tag.TAG_COMPOUND);
                for (Tag tag : changes) {
                    try {
                        CompoundTag change = (CompoundTag) tag;
                        ChangedRecipe.Type type = ChangedRecipe.Type.valueOf(change.getString("type"));
                        SupportedRecipeType<?> recipeType = RecipeTypeManager.getType(CraftTweakerGUI.rl(change.getString("recipeType")));;
                        RecipeSerializer<?> serializer = BuiltInRegistries.RECIPE_SERIALIZER.get(CraftTweakerGUI.rl(change.getString("serializer")));
                        RecipeSerializer<?> oldSerializer = change.contains("oldSerializer") ? BuiltInRegistries.RECIPE_SERIALIZER.get(CraftTweakerGUI.rl(change.getString("oldSerializer"))) : serializer;
                        changedRecipes.add(new ChangedRecipe<>(type, type != ChangedRecipe.Type.REMOVED ? change.getString("id") : null,
                                type != ChangedRecipe.Type.ADDED ? CraftTweakerGUI.rl(change.getString("originalId")) : null,
                                fromTag(serializer, NbtOps.INSTANCE, change.get("recipe"), recipeType),
                                type == ChangedRecipe.Type.CHANGED && change.contains("originalRecipe") ? fromTag(oldSerializer, NbtOps.INSTANCE, change.get("originalRecipe"), recipeType) : null,
                                recipeType, change.getBoolean("exported")));
                        i++;
                    } catch (Throwable t) {
                        LOGGER.error("Could not load recipe change, ignoring", t);
                        toastWithChat(Component.translatable("ctgui.loading_error_title"), Component.translatable("ctgui.error_message"));
                    }
                }
            } catch (IOException | CommandSyntaxException e) {
                LOGGER.error("Could not load recipe changes", e);
                toastWithChat(Component.translatable("ctgui.loading_error_title"), Component.translatable("ctgui.error_message"));
            }
        }
        if (!SCRIPT_FILE.toFile().exists()) {
            changedRecipes.forEach(recipe -> recipe.setExported(false));
        }
        LOGGER.info("Loaded {} recipe changes", i);
    }

    private static <T> RegistryOps<T> getRegistryOps(DynamicOps<T> ops) {
        if (Minecraft.getInstance().level == null) {
            throw new IllegalStateException("Tried to get RegistryOps before loading the level");
        }
        return RegistryOps.create(ops, Minecraft.getInstance().level.registryAccess());
    }

    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>> JsonElement toJson(T recipe) {
        DataResult<JsonElement> result = ((RecipeSerializer<T>) recipe.getSerializer()).codec().encode(recipe, getRegistryOps(JsonOps.INSTANCE), JsonOps.INSTANCE.mapBuilder()).build((JsonElement) null);
        if (result.isSuccess()) {
            return result.getOrThrow();
        }
        throw new RuntimeException("Recipe could not be serialized: " + result.error().map(DataResult.Error::message).orElse(recipe.toString()));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Recipe<?>, E> T fromTag(RecipeSerializer<?> serializer, DynamicOps<E> ops, E tag, SupportedRecipeType<?> recipeType) {
        AtomicReference<String> error = new AtomicReference<>(tag.toString());
        Optional<T> result = ((RecipeSerializer<T>) serializer).codec().decode(getRegistryOps(ops), ops.getMap(tag).getOrThrow()).resultOrPartial(error::set);
        if (result.isPresent()) {
            try {
                T recipe = result.get();
                T processedRecipe = ((SupportedRecipeType<T>) recipeType).onInitialize(recipe);
                return processedRecipe == null ? recipe : processedRecipe;
            } catch (UnsupportedRecipeException e) {
                //Unsupported recipes shouldn't be able to get saved, so we ignore it
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Recipe could not be deserialized: " + error.get());
    }

    public static void export() {
        File file = SCRIPT_FILE.toFile();
        file.delete();
        boolean shouldSave = false;
        try {
            FileWriter writer = new FileWriter(file);
            writer.append("/*CraftTweaker GUI generated script\n");
            writer.append("  Not intended for manual editing\n");
            writer.append("  Changes will be overridden when exporting again*/\n").append("\n");
            writer.append("import crafttweaker.api.ingredient.type.IIngredientEmpty;\n");
            writer.append("import crafttweaker.api.ingredient.IIngredient;\n\n");
            List<Class<?>> found = new ArrayList<>();
            for (ChangedRecipe<?> change : changedRecipes) {
                if (!found.contains(change.getRecipeType().getClass())) {
                    if (change.getCraftTweakerImportsString() != null) {
                        writer.append(change.getCraftTweakerImportsString() + "\n\n");
                    }
                    found.add(change.getRecipeType().getClass());
                }
            }

            for (ChangedRecipe<?> change : changedRecipes) {
                if (change.type == ChangedRecipe.Type.REMOVED) {
                    writer.append(change.getCraftTweakerRemoveString() + "\n");
                    writer.append("\n");
                } else if (change.type == ChangedRecipe.Type.CHANGED) {
                    //Todo: add a warning and maybe remove this backwards compat at some point
                    writer.append((change.originalRecipe != null ? change.getOriginalRemoveChange() : change).getCraftTweakerRemoveString() + "\n");
                    writer.append(change.getCraftTweakerString() + "\n");
                    writer.append("\n");
                } else {
                    writer.append(change.getCraftTweakerString() + "\n");
                    writer.append("\n");
                }
                if (!change.wasExported()) {
                    change.setExported(true);
                    shouldSave = true;
                }
            }

            writer.flush();
            writer.close();;
        } catch (Throwable t) {
            LOGGER.error("Could not export recipe changes", t);
            toastWithChat(Component.translatable("ctgui.export_error_title"), Component.translatable("ctgui.error_message"));

            changedRecipes.forEach(recipe -> recipe.setExported(false));
            shouldSave = true;
        }

        if (shouldSave) {
            save();
        }
    }

    public static void reInjectAll() {
        for (ChangedRecipe<?> change : changedRecipes) {
            if (change.type != ChangedRecipe.Type.REMOVED && !change.wasExported()) {
                CraftTweakerGUI.getViewerUtils().inject(change);
            }
        }
    }

    private static void toastWithChat(Component title, Component message) {
        Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PERIODIC_NOTIFICATION, title, message));
        Minecraft.getInstance().player.sendSystemMessage(title.copy().append(": ").append(message));
    }

    public static class ChangedRecipe<T extends Recipe<?>> {

        private final Type type;
        private final String id;
        private final ResourceLocation originalId;
        private final T recipe;
        private final T originalRecipe;
        private final SupportedRecipeType<T> recipeType;
        private boolean exported;

        private ChangedRecipe(Type type, String id, ResourceLocation originalId, T recipe, T originalRecipe, SupportedRecipeType<T> recipeType) {
            this(type, id, originalId, recipe, originalRecipe, recipeType, false);
        }

        private ChangedRecipe(Type type, String id, ResourceLocation originalId, T recipe, T originalRecipe, SupportedRecipeType<T> recipeType, boolean exported) {
            this.type = type;
            this.id = id;
            this.originalId = originalId;
            this.recipe = recipe;
            this.originalRecipe = originalRecipe;
            this.recipeType = recipeType;
            this.exported = exported;
        }

        public static <T extends Recipe<?>> ChangedRecipe<T> added(String id, T recipe, SupportedRecipeType<T> recipeType) {
            return new ChangedRecipe<>(Type.ADDED, id, null, recipe, null, recipeType);
        }

        public static <T extends Recipe<?>> ChangedRecipe<T> changed(String id, ResourceLocation originalId, T recipe, T originalRecipe, SupportedRecipeType<T> recipeType) {
            return new ChangedRecipe<>(Type.CHANGED, id, originalId, recipe, originalRecipe, recipeType);
        }

        public static <T extends Recipe<?>> ChangedRecipe<T> removed(ResourceLocation id, T recipe, SupportedRecipeType<T> recipeType) {
            return new ChangedRecipe<>(Type.REMOVED, null, id, recipe, null, recipeType);
        }

        public ChangedRecipe<T> withRecipe(T recipe) {
            return new ChangedRecipe<>(this.type, this.id, this.originalId, recipe, this.originalRecipe, this.recipeType);
        }

        public Component getTitle() {
            return Component.translatable("ctgui.list.change_title", type.getName(),
                    CraftTweakerGUI.getViewerUtils().getCategoryName(recipeType.getId()));
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

        public String getCraftTweakerImportsString() {
            return recipeType.getCraftTweakerImportsString();
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

        public T getOriginalRecipe() {
            return originalRecipe;
        }

        public ChangedRecipe<T> getOriginalRemoveChange() {
            return removed(originalId, originalRecipe, recipeType);
        }

        public SupportedRecipeType<T> getRecipeType() {
            return recipeType;
        }

        public SupportedRecipe<T, ? extends SupportedRecipeType<T>> toSupportedRecipe() throws UnsupportedViewerException {
            return CraftTweakerGUI.getViewerUtils().toSupportedRecipe(recipeType, recipe);
        }

        public void setExported(boolean exported) {
            this.exported = exported;
            if (exported && type != Type.REMOVED && CraftTweakerGUI.isJeiActive()) { //Changes are automatically uninjected in EMI by the reload that actually applies these
                CraftTweakerGUI.getViewerUtils().unInject(this);
            }
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
