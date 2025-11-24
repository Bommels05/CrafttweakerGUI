package de.bommels05.ctgui.compat.minecraft.custom;

import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.api.*;
import de.bommels05.ctgui.api.option.RecipeIdFieldRecipeOption;
import de.bommels05.ctgui.emi.EmiEditingTagRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.recipe.EmiTagRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

public class TagRecipeType extends SupportedRecipeType<TagRecipe<?>> {

    private final RecipeIdFieldRecipeOption<TagRecipe<?>> name = new RecipeIdFieldRecipeOption<>(Component.translatable("ctgui.editing.options.tag_name"), name -> {
        boolean slash = false;
        for (char c : name.toCharArray()) {
            if (slash && c == '/') {
                return false;
            }
            slash = c == '/';
            if (!ResourceLocation.isAllowedInResourceLocation(c)) {
                return false;
            }
        }
        return true;
    });

    public TagRecipeType() {
        super(CraftTweakerGUI.rl("emi:tag"));

        addOption(name, (r, name) -> {
            if (ResourceLocation.tryParse(name) != null && !name.isEmpty()) {
                return new TagRecipe(ResourceLocation.parse(name), r.registry, r.tags, r.entries);
            } else {
                r.valid = false;
                return r;
            }
        });
    }

    @Override
    public TagRecipe<?> onInitialize(TagRecipe<?> recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);

        if (recipe == null) {
            name.set("crafttweaker:example_tag");
            return new TagRecipe<>(CraftTweakerGUI.rl("crafttweaker:example_tag"), BuiltInRegistries.ITEM, List.of(), List.of());
        }
        name.set(recipe.id.toString());
        //We return the old recipe here so the custom emi recipe implementation is used and not the original
        return recipe;
    }

    @SuppressWarnings("unchecked")
    public <T> void lateInit(List<EmiIngredient> ingredients, int pageHeight, int page, boolean item) {
        int pageMultiplier = page * (pageHeight * 8);
        clearAreas();
        for (int i = 0; i < ingredients.size() && i / 8 <= pageHeight; i++) {
            int index = i;
            if (item) {
                addAreaEmptyRightClick(i % 8 * 18, i / 8 * 18 + 24, 17, 17, (recipe, am) -> {
                    TagRecipe<Item> r = (TagRecipe<Item>) recipe;
                    if (am.isEmpty()) {
                        if (index + pageMultiplier < r.tags.size()) {
                            List<TagKey<Item>> tags = new ArrayList<>(r.tags);
                            tags.remove(index + pageMultiplier);
                            return new TagRecipe<>(r.id, r.registry, tags, r.entries);
                        } else if ((index + pageMultiplier) - r.tags.size() < r.entries.size()) {
                            List<Item> entries = new ArrayList<>(r.entries);
                            entries.remove((index + pageMultiplier) - r.tags.size());
                            return new TagRecipe<>(r.id, r.registry, r.tags, entries);
                        } else {
                            return r;
                        }
                    } else if (am.isTag()) {
                        List<TagKey<Item>> tags = new ArrayList<>(r.tags);
                        tags.add(((Ingredient.TagValue) am.ingredient().values[0]).tag());
                        return new TagRecipe<>(r.id, r.registry, tags, r.entries);
                    } else {
                        List<Item> entries = new ArrayList<>(r.entries);
                        entries.add(am.asStack().getItem());
                        return new TagRecipe<>(r.id, r.registry, r.tags, entries);
                    }
                }, recipe -> {
                    TagRecipe<Item> r = (TagRecipe<Item>) recipe;
                    if (index + pageMultiplier < r.tags.size()) {
                        return new AmountedIngredient(Ingredient.of(r.tags.get(index + pageMultiplier)), 1);
                    } else if ((index + pageMultiplier) - r.tags.size() < r.entries.size()) {
                        return AmountedIngredient.of(new ItemStack(r.entries.get((index + pageMultiplier) - r.tags.size())));
                    }
                    return AmountedIngredient.empty();
                });
            } else {
                addAreaEmptyRightClick(i % 8 * 18, i / 8 * 18 + 24, 17, 17, (recipe, ingredient) -> {
                    TagRecipe<T> r = (TagRecipe<T>) recipe;
                    SpecialAmountedIngredient<?, T> am = (SpecialAmountedIngredient<?, T>) ingredient;
                    if (am.isEmpty()) {
                        if (index + pageMultiplier < r.tags.size()) {
                            List<TagKey<T>> tags = new ArrayList<>(r.tags);
                            tags.remove(index + pageMultiplier);
                            return new TagRecipe<>(r.id, r.registry, tags, r.entries);
                        } else if ((index + pageMultiplier) - r.tags.size() < r.entries.size()) {
                            List<T> entries = new ArrayList<>(r.entries);
                            entries.remove((index + pageMultiplier) - r.tags.size());
                            return new TagRecipe<>(r.id, r.registry, r.tags, entries);
                        }
                    } else if (am.getRegistry() == r.registry) {
                        if (am.isTag()) {
                            List<TagKey<T>> tags = new ArrayList<>(r.tags);
                            tags.add(am.getTag());
                            return new TagRecipe<>(r.id, r.registry, tags, r.entries);
                        } else {
                            List<T> entries = new ArrayList<>(r.entries);
                            entries.add(am.getStackAsType());
                            return new TagRecipe<>(r.id, r.registry, r.tags, entries);
                        }
                    }
                    return null;
                }, recipe -> {
                    TagRecipe<T> r = (TagRecipe<T>) recipe;
                    if (index + pageMultiplier < r.tags.size()) {
                        return CraftTweakerGUI.getLoaderUtils().getIngredientFromTag(r.tags.get(index + pageMultiplier), 1);
                    } else if ((index + pageMultiplier) - r.tags.size() < r.entries.size()) {
                        return CraftTweakerGUI.getLoaderUtils().getIngredientFromStack(CraftTweakerGUI.getLoaderUtils().stackFromType(r.entries.get((index + pageMultiplier) - r.tags.size())));
                    }
                    return CraftTweakerGUI.getLoaderUtils().getIngredientFromStack(CraftTweakerGUI.getLoaderUtils().emptyStackFromRegistry(r.registry));
                }, () -> CraftTweakerGUI.getLoaderUtils().getIngredientFromStack(CraftTweakerGUI.getLoaderUtils().emptyStackFromRegistry(BuiltInRegistries.FLUID)));
            }
        }
    }

    @Override
    public ItemStack getMainOutput(TagRecipe<?> recipe) {
        //Because the recipe id field is overridden this is just used for the icon in the changed recipes list
        return new ItemStack(Items.NAME_TAG);
    }

    @Override
    public boolean isValid(TagRecipe<?> recipe) {
        return recipe.valid;
    }

    @Override
    public Object getEmiRecipe(TagRecipe<?> recipe) throws UnsupportedViewerException {
        return new EmiEditingTagRecipe(this, TagKey.create(recipe.registry.key(), recipe.id), recipe.tags, recipe.entries);
    }

    @Override
    public Function<EmiRecipe, TagRecipe<?>> getAlternativeEmiRecipeGetter() {
        return recipe -> recipe instanceof EmiTagRecipe ? new TagRecipe<>(((EmiTagRecipe) recipe).key) : null;
    }

    @Override
    public String getCraftTweakerRemoveString(TagRecipe<?> recipe, ResourceLocation id) {
        return "<tag:" + getCraftTweakerRegistry(recipe) + ":" + recipe.id + ">.clear();";
    }

    @Override
    public String getCraftTweakerString(TagRecipe<?> recipe, String id) {
        StringJoiner builder = new StringJoiner("\n");
        String type = getCraftTweakerRegistry(recipe);
        for (TagKey<?> tag : recipe.tags) {
            String tagName = "<tag:" + type + ":" + tag.location() + ">";
            builder.add("if (" + tagName + ".exists) { <tag:" + type + ":" + recipe.id + ">.add(" + tagName + "); }");
        }
        recipe.forEntryWithId((entryId) -> {
            builder.add("<tag:" + type + ":" + recipe.id + ">.add(<" + type.substring(type.indexOf('/') + 1) + ":" + entryId + ">);");
        });
        return builder.toString();
    }

    private String getCraftTweakerRegistry(TagRecipe<?> recipe) {
        return recipe.registry.key().location().toString().replace(':', '/').replace("minecraft/", "");
    }
}
