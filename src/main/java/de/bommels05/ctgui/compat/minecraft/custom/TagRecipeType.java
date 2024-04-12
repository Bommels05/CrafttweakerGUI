package de.bommels05.ctgui.compat.minecraft.custom;

import de.bommels05.ctgui.api.AmountedIngredient;
import de.bommels05.ctgui.api.SupportedRecipeType;
import de.bommels05.ctgui.api.UnsupportedRecipeException;
import de.bommels05.ctgui.api.UnsupportedViewerException;
import de.bommels05.ctgui.api.option.BooleanRecipeOption;
import de.bommels05.ctgui.api.option.RecipeIdFieldRecipeOption;
import de.bommels05.ctgui.emi.EmiEditingTagRecipe;
import dev.emi.emi.api.recipe.EmiIngredientRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.recipe.EmiTagRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TagRecipeType extends SupportedRecipeType<TagRecipe> {

    private final BooleanRecipeOption<TagRecipe> item = new BooleanRecipeOption<>(Component.translatable("ctgui.editing.options.tag_item"));
    private final RecipeIdFieldRecipeOption<TagRecipe> name = new RecipeIdFieldRecipeOption<>(Component.translatable("ctgui.editing.options.tag_name"), name -> {
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
        super(new ResourceLocation("emi:tag"));

        //todo enable this when fluids are supported
        /*addOption(item, (r, item) -> {
            if (item) {
                return new TagRecipe(TagKey.create(Registries.ITEM, r.id), List.of(), List.of());
            } else {
                return new TagRecipe(List.of(), List.of(), TagKey.create(Registries.FLUID, r.id));
            }
        });*/
        addOption(name, (r, name) -> {
            if (ResourceLocation.isValidResourceLocation(name) && !name.isEmpty()) {
                if (r.item) {
                    return new TagRecipe(TagKey.create(Registries.ITEM, new ResourceLocation(name)), r.items, r.itemTags);
                } else {
                    return new TagRecipe(r.fluidTags, r.fluids, TagKey.create(Registries.FLUID, new ResourceLocation(name)));
                }
            } else {
                r.valid = false;
                return r;
            }
        });
    }

    @Override
    public TagRecipe onInitialize(TagRecipe recipe) throws UnsupportedRecipeException {
        super.onInitialize(recipe);

        if (recipe == null) {
            name.set("ctgui:example_tag");
            return new TagRecipe(TagKey.create(Registries.ITEM, new ResourceLocation("ctgui:example_tag")), List.of(), List.of());
        }
        name.set(recipe.id.toString());
        //We return the old recipe here so the custom emi recipe implementation is used and not the original
        return recipe;
    }

    public void lateInit(List<EmiIngredient> ingredients, int pageHeight, int page) {
        int pageMultiplier = page * (pageHeight * 8);
        clearAreas();
        for (int i = 0; i < ingredients.size() && i / 8 <= pageHeight; i++) {
            int index = i;
            addAreaEmptyRightClick(i % 8 * 18, i / 8 * 18 + 24, 17, 17, (r, am) -> {
                if (r.item) {
                    if (am.isEmpty()) {
                        if (index + pageMultiplier < r.itemTags.size()) {
                            List<TagKey<Item>> tags = new ArrayList<>(r.itemTags);
                            tags.remove(index + pageMultiplier);
                            return new TagRecipe(TagKey.create(Registries.ITEM, r.id), r.items, tags);
                        } else if ((index + pageMultiplier) - r.itemTags.size() < r.items.size()) {
                            List<ItemStack> items = new ArrayList<>(r.items);
                            items.remove((index + pageMultiplier) - r.itemTags.size());
                            return new TagRecipe(TagKey.create(Registries.ITEM, r.id), items, r.itemTags);
                        } else {
                            return r;
                        }
                    } else if (am.isTag()) {
                        List<TagKey<Item>> tags = new ArrayList<>(r.itemTags);
                        tags.add(((Ingredient.TagValue) am.ingredient().getValues()[0]).tag());
                        return new TagRecipe(TagKey.create(Registries.ITEM, r.id), r.items, tags);
                    } else {
                        List<ItemStack> items = new ArrayList<>(r.items);
                        items.add(am.withAmount(1).asStack());
                        return new TagRecipe(TagKey.create(Registries.ITEM, r.id), items, r.itemTags);
                    }
                } else {
                    //todo add this when fluids are supported
                    return null;
                }
            }, r -> {
                if (r.item) {
                    if (index + pageMultiplier < r.itemTags.size()) {
                        return new AmountedIngredient(Ingredient.of(r.itemTags.get(index + pageMultiplier)), 1);
                    } else if ((index + pageMultiplier) - r.itemTags.size() < r.items.size()) {
                        return AmountedIngredient.of(r.items.get((index + pageMultiplier) - r.itemTags.size()));
                    }
                    return AmountedIngredient.empty();
                } else {
                    //todo add this when fluids are supported
                    return null;
                }
            });
        }
    }

    @Override
    public ItemStack getMainOutput(TagRecipe recipe) {
        //Because the recipe id field is overridden this is just used for the icon in the changed recipes list
        return new ItemStack(Items.NAME_TAG);
    }

    @Override
    public boolean isValid(TagRecipe recipe) {
        return recipe.valid;
    }

    @Override
    public EmiEditingTagRecipe getEmiRecipe(TagRecipe recipe) throws UnsupportedViewerException {
        if (recipe.item) {
            return new EmiEditingTagRecipe(this, TagKey.create(Registries.ITEM, recipe.id), recipe.items, recipe.itemTags);
        } else {
            return new EmiEditingTagRecipe(this, recipe.fluidTags, recipe.fluids, TagKey.create(Registries.FLUID, recipe.id));
        }
    }

    @Override
    public Function<EmiRecipe, TagRecipe> getAlternativeEmiRecipeGetter() {
        return recipe -> recipe instanceof EmiTagRecipe ? new TagRecipe(((EmiTagRecipe) recipe).key) : null;
    }

    @Override
    public String getCraftTweakerRemoveString(TagRecipe recipe, ResourceLocation id) {
        return "<tag:items:" + recipe.id + ">.clear();";
    }

    @Override
    public String getCraftTweakerString(TagRecipe recipe, String id) {
        StringBuilder builder = new StringBuilder();
        for (TagKey<Item> tag : recipe.itemTags) {
            builder.append("<tag:items:" + recipe.id + ">.add(<tag:items:" + tag.location() + ">);");
        }
        for (ItemStack item : recipe.items) {
            builder.append("<tag:items:" + recipe.id + ">.add(" + getCTString(item) + ");");
        }
        return builder.toString();
    }
}
