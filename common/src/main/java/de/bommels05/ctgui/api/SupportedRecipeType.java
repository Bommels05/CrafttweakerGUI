package de.bommels05.ctgui.api;

import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.ingredient.type.IngredientWithAmount;
import com.blamejared.crafttweaker.api.util.ItemStackUtil;
import com.mojang.datafixers.util.Either;
import de.bommels05.ctgui.CraftTweakerGUI;
import de.bommels05.ctgui.screen.RecipeEditScreen;
import de.bommels05.ctgui.api.option.RecipeOption;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A recipe type that can be edited
 * @param <R> The recipes that this type uses
 */
public abstract class SupportedRecipeType<R extends Recipe<?>> {

    public static final ItemStack UNSET = new ItemStack(Items.BARRIER);
    static {
        CompoundTag display = new CompoundTag();
        display.putString("Name", "Unset");
        UNSET.getOrCreateTag().put("display", display);
    }
    private final ResourceLocation id;
    private final List<Area<R, ?, ?>> areas = new ArrayList<>();
    private final List<RecipeOption<?, R>> options = new ArrayList<>();

    /**
     * @param id The id of the recipe category in the recipe viewer
     */
    protected SupportedRecipeType(ResourceLocation id) {
        this.id = id;
    }

    /**
     * Called when the recipe is loaded into the Editing Screen or loaded from the changed recipes list.
     * This can be used to set the values of {@link RecipeOption}s according to the recipe
     * @param recipe The recipe, null when creating a new recipe of this type
     * @return A new recipe when the recipe was null. Null when the supplied recipe should not be changed or a new recipe if the supplied recipe should be changed before editing
     * @throws UnsupportedRecipeException When the implementation of this recipe is not supported. May not be thrown when the recipe is null
     */
    public R onInitialize(@Nullable R recipe) throws UnsupportedRecipeException {
        options.forEach(RecipeOption::reset);
        return null;
    }

    /**
     * Returns if this recipe can be saved and later exported to CraftTweaker or is still missing something
     * @param recipe The recipe
     * @return If this recipe is "finished" and ready to be saved and later exported
     */
    public abstract boolean isValid(R recipe);

    /**
     * Returns the Emi representation of the recipe
     * @param recipe The recipe
     * @return The Emi representation of the recipe. This has to be an instance of {@link EmiRecipe}!!! but can not be declared as such, because of class loading issues when Emi is not installed.
     * @throws UnsupportedViewerException If the recipe can not be displayed with Emi
     */
    public abstract Object getEmiRecipe(R recipe) throws UnsupportedViewerException;

    /**
     * Returns the CrafTweaker command to add an recipe of this type with the given id
     * @param recipe The recipe to add
     * @param id The id of the recipe to add
     * @return The CraftTweaker command to add the recipe
     */
    public abstract String getCraftTweakerString(R recipe, String id);

    /**
     * Returns the CraftTweaker command to remove the recipe of this type with the given id
     * @param recipe The recipe to remove. Can be used if the id is not enough for this type
     * @param id The id of the recipe to remove
     * @return The CraftTweaker command to remove the recipe
     */
    public String getCraftTweakerRemoveString(R recipe, ResourceLocation id) {
        return "<recipetype:" + this.id + ">.removeByName(\"" + id + "\");\n";
    }

    /**
     * Not required, only used for auto generating recipe ids and the icon in the changed recipes list
     * Returns the recipe result item by default
     * @param recipe The recipe
     * @return The main output of the recipe or ItemStack.EMPTY
     */
    public ItemStack getMainOutput(R recipe) {
        return convertUnset(recipe.getResultItem(regAccess()));
    }

    /**
     * Can be used when the emi recipes does not have a backing recipe or the backing recipe is of the wrong type
     * @return A function that manually gets the recipe from the Emi recipe or returns null to use the backing recipe
     */
    public Function<EmiRecipe, R> getAlternativeEmiRecipeGetter() {
        return null;
    }

    /**
     * Returns the CraftTweaker representation of the list of ingredients
     * @param ingredients The list of ingredients
     * @return The CraftTweaker representation of the list of ingredients
     */
    protected String getCTString(List<Ingredient> ingredients) {
        StringBuilder string = new StringBuilder("[");
        int i = 1;
        for (Ingredient ingredient : ingredients) {
            string.append(getCTString(ingredient));
            if (i < ingredients.size()) {
                string.append(", ");
            }
            i++;
        }
        string.append("]");
        return string.toString();
    }

    /**
     * Returns the CraftTweaker representation of the stack
     * @param stack The stack
     * @return The CraftTweaker representation of the stack
     */
    protected String getCTString(ItemStack stack) {
        return ItemStackUtil.getCommandString(stack);
    }

    /**
     * Returns the CraftTweaker representation of the ingredient
     * @param ingredient The ingredient
     * @return The CraftTweaker representation of the ingredient
     */
    protected String getCTString(Ingredient ingredient) {
        return IIngredient.fromIngredient(ingredient).getCommandString();
    }

    /**
     * Returns the CraftTweaker representation of the ingredient with amount
     * @param ingredient The ingredient with amount
     * @return The CraftTweaker representation of the ingredient with amount
     */
    protected String getCTString(AmountedIngredient ingredient) {
        return new IngredientWithAmount(IIngredient.fromIngredient(ingredient.ingredient()), ingredient.amount()).getCommandString();
    }

    public R onDragAndDrop(R recipe, int x, int y, AmountedIngredient ingredient) {
        for (Area<R, ?, ?> area : areas) {
            if (area.inside(x, y) && area.stackSupplier.apply(recipe).left().isPresent()) {
                return area.dragAndDropHandler.apply(recipe, Either.left(ingredient));
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <S, T> R onDragAndDropSpecial(R recipe, int x, int y, SpecialAmountedIngredient<S, T> ingredient) {
        for (Area<R, ?, ?> area : areas) {
            if (area.inside(x, y)) {
                try {
                    return ((Area<R, S, T>) area).dragAndDropHandler.apply(recipe, Either.right(ingredient));
                } catch (ClassCastException ignored) {}
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public R onClick(R recipe, int x, int y, boolean rightClick, RecipeEditScreen<R> screen) {
        for (Area<R, ?, ?> area : areas) {
            if (area.inside(x, y)) {
                R result = area.clickHandler.apply(recipe, rightClick);
                if (result == null) {
                    Either<AmountedIngredient, ? extends SpecialAmountedIngredient<?, ?>> stack = area.stackSupplier.apply(recipe);
                    if (stack.left().isPresent()) {
                        AmountedIngredient ingredient = stack.left().get();
                        if (!ingredient.isEmpty()) {
                            screen.setDragged(ingredient);
                        }
                    } else if (stack.right().orElseThrow().isTag() || !stack.right().orElseThrow().isStackEmpty()) {
                        screen.setDraggedSpecial(stack.right().orElseThrow());
                    }
                }
                return result;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <S, T> R onReleased(R recipe, int x, int y, boolean rightClick, RecipeEditScreen<R> screen) {
        if (!rightClick && screen.getDragged() != null) {
            R result = onDragAndDrop(recipe, x, y, screen.getDragged());
            screen.setDragged(null);
            return result;
        }
        if (!rightClick && screen.getDraggedSpecial() != null) {
            R result = onDragAndDropSpecial(recipe, x, y, screen.getDraggedSpecial());
            screen.setDraggedSpecial(null);
            return result;
        }
        return null;
    }

    public R onScroll(R recipe, int x, int y, boolean up) {
        for (Area<R, ?, ?> area : areas) {
            if (area.inside(x, y)) {
                return area.scrollHandler.apply(recipe, up);
            }
        }
        return null;
    }

    /**
     * Adds a new {@link RecipeOption} to this type
     * @param option The option
     * @param handler The handler that is called when the option is changed and returns the modified recipe
     */
    protected <T> void addOption(RecipeOption<T, R> option, BiFunction<R, T, R> handler) {
        option.addListener(handler);
        options.add(option);
    }

    /**
     * Adds a new area to interact with the recipe in the editing screen without a scroll and click handler
     * @param x The x coordinate of the area
     * @param y The y coordinate of the area
     * @param width The width of the area
     * @param height The height of the area
     * @param dragAndDropHandler The handler that handles Ingredients being dropped into the area and returns a modified recipe if needed
     * @param stackSupplier The supplier that returns the Ingredient that is currently in the area
     */
    protected <T> void addArea(int x, int y, int width, int height, BiFunction<R, T, R> dragAndDropHandler, Function<R, T> stackSupplier) {
        addArea(x, y, width, height, dragAndDropHandler, stackSupplier, (r, rightClick) -> null);
    }

    /**
     * Adds a new area to interact with the recipe in the editing screen without a scroll handler
     * @param x The x coordinate of the area
     * @param y The y coordinate of the area
     * @param width The width of the area
     * @param height The height of the area
     * @param dragAndDropHandler The handler that handles Ingredients being dropped into the area and returns a modified recipe if needed
     * @param stackSupplier The supplier that returns the Ingredient that is currently in the area
     * @param clickHandler The handler that handles left or right mouse clicks on the area and returns a modified recipe if needed
     */
    protected <T> void addArea(int x, int y, int width, int height, BiFunction<R, T, R> dragAndDropHandler, Function<R, T> stackSupplier,
                           BiFunction<R, Boolean, R> clickHandler) {
        addArea(x, y, width, height, dragAndDropHandler, stackSupplier, clickHandler, (r, up) -> null);
    }

    /**
     * Adds a new area to interact with the recipe in the editing screen
     * @param x The x coordinate of the area
     * @param y The y coordinate of the area
     * @param width The width of the area
     * @param height The height of the area
     * @param dragAndDropHandler The handler that handles Ingredients being dropped into the area and returns a modified recipe if needed
     * @param stackSupplier The supplier that returns the Ingredient that is currently in the area
     * @param clickHandler The handler that handles left or right mouse clicks on the area and returns a modified recipe if needed
     * @param scrollHandler The handler that handles mouse scrolls on the area and returns a modified recipe if needed
     */
    @SuppressWarnings("unchecked")
    protected <T, S, TT> void addArea(int x, int y, int width, int height, BiFunction<R, T, R> dragAndDropHandler, Function<R, T> stackSupplier,
                           BiFunction<R, Boolean, R> clickHandler, BiFunction<R, Boolean, R> scrollHandler) {
        try {
            ((BiFunction<R, AmountedIngredient, R>) dragAndDropHandler).apply(null, AmountedIngredient.empty());
            //Probably thrown anyway
            throw new NullPointerException();
        } catch (ClassCastException e) {
            //-> Does not accept AmountedIngredients
            areas.add(Area.createSpecial(x, y, width, height, (BiFunction<R, SpecialAmountedIngredient<S, TT>, R>) dragAndDropHandler, (Function<R, SpecialAmountedIngredient<S, TT>>) stackSupplier, clickHandler, scrollHandler));
        } catch (Throwable t) {
            areas.add(Area.create(x, y, width, height, (BiFunction<R, AmountedIngredient, R>) dragAndDropHandler, (Function<R, AmountedIngredient>) stackSupplier, clickHandler, scrollHandler));
        }
    }

    /**
     * Adds a new area to interact with the recipe in the editing screen without a scroll handler that calls the drag and drop handler with an empty {@link AmountedIngredient} when right-clicked
     * @param x The x coordinate of the area
     * @param y The y coordinate of the area
     * @param width The width of the area
     * @param height The height of the area
     * @param dragAndDropHandler The handler that handles Ingredients being dropped into the area and returns a modified recipe if needed
     * @param stackSupplier The supplier that returns the Ingredient that is currently in the area
     */
    protected void addAreaEmptyRightClick(int x, int y, int width, int height, BiFunction<R, AmountedIngredient, R> dragAndDropHandler, Function<R, AmountedIngredient> stackSupplier) {
        addAreaEmptyRightClick(x, y, width, height, dragAndDropHandler, stackSupplier, (r, up) -> null);
    }

    /**
     * Adds a new area to interact with the recipe in the editing screen that calls the drag and drop handler with an empty {@link AmountedIngredient} when right-clicked
     * @param x The x coordinate of the area
     * @param y The y coordinate of the area
     * @param width The width of the area
     * @param height The height of the area
     * @param dragAndDropHandler The handler that handles {@link AmountedIngredient}s being dropped into the area and returns a modified recipe if needed
     * @param stackSupplier The supplier that returns the {@link AmountedIngredient} that is currently in the area
     * @param scrollHandler The handler that handles mouse scrolls on the area and returns a modified recipe if needed
     */
    protected void addAreaEmptyRightClick(int x, int y, int width, int height, BiFunction<R, AmountedIngredient, R> dragAndDropHandler, Function<R, AmountedIngredient> stackSupplier,
                                          BiFunction<R, Boolean, R> scrollHandler) {
        addAreaEmptyRightClick(x, y, width, height, dragAndDropHandler, stackSupplier, AmountedIngredient::empty, scrollHandler);
    }

    /**
     * Adds a new area to interact with the recipe in the editing screen that calls the drag and drop handler with the empty Ingredient when right-clicked
     * @param x The x coordinate of the area
     * @param y The y coordinate of the area
     * @param width The width of the area
     * @param height The height of the area
     * @param dragAndDropHandler The handler that handles Ingredients being dropped into the area and returns a modified recipe if needed
     * @param stackSupplier The supplier that returns the Ingredient that is currently in the area
     * @param emptyIngredient The supplier that returns the empty ingredient to pass into the drag and drop handler when right-clicked
     * @param scrollHandler The handler that handles mouse scrolls on the area and returns a modified recipe if needed
     */
    protected <T> void addAreaEmptyRightClick(int x, int y, int width, int height, BiFunction<R, T, R> dragAndDropHandler, Function<R, T> stackSupplier,
                                          Supplier<T> emptyIngredient, BiFunction<R, Boolean, R> scrollHandler) {
        addArea(x, y, width, height, dragAndDropHandler, stackSupplier, (recipe, rightClick) -> rightClick ? dragAndDropHandler.apply(recipe, emptyIngredient.get()) : null, scrollHandler);
    }

    /**
     * Adds a new area to interact with the recipe in the editing screen that calls the drag and drop handler with an empty {@link AmountedIngredient} when right-clicked
     * and the {@link AmountedIngredient} returned by the stack supplier with a different amount when scrolled between 1 and 64
     * @param x The x coordinate of the area
     * @param y The y coordinate of the area
     * @param width The width of the area
     * @param height The height of the area
     * @param dragAndDropHandler The handler that handles {@link AmountedIngredient}s being dropped into the area and returns a modified recipe if needed
     * @param stackSupplier The supplier that returns the {@link AmountedIngredient} that is currently in the area
     */
    protected void addAreaScrollAmountEmptyRightClick(int x, int y, int width, int height, BiFunction<R, AmountedIngredient, R> dragAndDropHandler, Function<R, AmountedIngredient> stackSupplier) {
        addAreaEmptyRightClick(x, y, width, height, dragAndDropHandler, stackSupplier, (recipe, up) -> {
            AmountedIngredient ingredient = stackSupplier.apply(recipe);
            return dragAndDropHandler.apply(recipe, ingredient.withAmount(getScrollStackSize(ingredient.amount(), up)));
        });
    }

    /**
     * Adds a new area to interact with the recipe in the editing screen that calls the drag and drop handler with the empty Ingredient when right-clicked
     * and the Ingredient returned by the amount setter
     * @param x The x coordinate of the area
     * @param y The y coordinate of the area
     * @param width The width of the area
     * @param height The height of the area
     * @param dragAndDropHandler The handler that handles {@link AmountedIngredient}s being dropped into the area and returns a modified recipe if needed
     * @param stackSupplier The supplier that returns the {@link AmountedIngredient} that is currently in the area
     * @param emptyIngredient The supplier that returns the empty ingredient to pass into the drag and drop handler when right-clicked
     * @param amountSetter The function that sets the amount of the ingredient dependent on the scroll direction and possibly on the shift (Larger change) and control (Smaller change) keys
     */
    protected <T> void addAreaScrollAmountEmptyRightClick(int x, int y, int width, int height, BiFunction<R, T, R> dragAndDropHandler, Function<R, T> stackSupplier,
                                                          Supplier<T> emptyIngredient, BiFunction<T, Boolean, T> amountSetter) {
        addAreaEmptyRightClick(x, y, width, height, dragAndDropHandler, stackSupplier, emptyIngredient, (recipe, up) -> {
            T ingredient = stackSupplier.apply(recipe);
            return dragAndDropHandler.apply(recipe, amountSetter.apply(ingredient, up));
        });
    }

    /**
     * Increases or decreases the originalSize by 1 or 16 when shift is pressed. It is always between 1 and 64
     * @param originalSize The size to change
     * @param up Whether to increase or decrease the size
     * @return The increased or decreased size between 1 and 64
     */
    protected int getScrollStackSize(int originalSize, boolean up) {
        return Math.min(Math.max(originalSize + ((up ? 1 : -1) * (Screen.hasShiftDown() ? (originalSize == 1 ? 15 : 16) : 1)), 1), 64);
    }

    /**
     * Returns the amount to add to the amount of a fluid when scrolling respecting the shift (Larger value) and control (Smaller value) keys
     * @param up Whether to increase or decrease the amount
     * @return The amount to add to the amount of a fluid
     */
    public static int getFluidScrollAmount(boolean up) {
        int value = Screen.hasShiftDown() ? 1000 : (Screen.hasControlDown() ? 1 : 50);
        return up ? value : -value;
    }

    /**
     * Adds a new area to interact with the recipe in the editing screen
     * @param area The area to add
     */
    protected void addArea(Area<R, ?, ?> area) {
        areas.add(area);
    }

    /**
     * Clears all configured areas
     * Can be used when dynamically changing the areas based on the recipe
     */
    protected void clearAreas() {
        areas.clear();
    }

    public List<Area<R, ?, ?>> getAreas() {
        return areas;
    }

    /**
     * Quickly get the registry access mostly used to get a recipes result item
     * @return The registry access of the current level
     */
    protected RegistryAccess regAccess() {
        return Minecraft.getInstance().level.registryAccess();
    }

    protected ResourceLocation nullRl() {
        return new ResourceLocation(CraftTweakerGUI.MOD_ID, "null");
    }

    protected EmiRecipeCategory getEmiCategory(ResourceLocation id) {
        return EmiApi.getRecipeManager().getCategories().stream().filter(category -> category.getId().equals(id)).findFirst().orElse(null);
    }

    protected ItemStack convertUnset(ItemStack stack) {
        if (ItemStack.isSameItemSameTags(stack, UNSET)) {
            return ItemStack.EMPTY;
        }
        return stack;
    }

    protected ItemStack convertToUnset(ItemStack stack) {
        if (stack.isEmpty()) {
            return UNSET;
        }
        return stack;
    }

    protected AmountedIngredient convertUnset(AmountedIngredient ingredient) {
        if (ItemStack.isSameItemSameTags(ingredient.asStack(), UNSET)) {
            return AmountedIngredient.empty();
        }
        return ingredient;
    }

    protected AmountedIngredient convertToUnset(AmountedIngredient ingredient) {
        if (ingredient.isEmpty()) {
            return AmountedIngredient.of(UNSET);
        }
        return ingredient;
    }

    protected void error(Component message) {
        new UnsupportedRecipeException(message).display();
    }

    public ResourceLocation getId() {
        return id;
    }

    public List<RecipeOption<?, R>> getOptions() {
        return options;
    }

    public record Area<R extends Recipe<?>, S, T>(int x, int y, int width, int height, BiFunction<R, Either<AmountedIngredient, SpecialAmountedIngredient<S, T>>, R> dragAndDropHandler,
                                               Function<R, Either<AmountedIngredient, SpecialAmountedIngredient<S, T>>> stackSupplier,
                                               BiFunction<R, Boolean, R> clickHandler, BiFunction<R, Boolean, R> scrollHandler) {

        public static <R extends Recipe<?>> Area<R, AmountedIngredient, Item> create(int x, int y, int width, int height, BiFunction<R, AmountedIngredient, R> dragAndDropHandler, Function<R, AmountedIngredient> stackSupplier, BiFunction<R, Boolean, R> clickHandler, BiFunction<R, Boolean, R> scrollHandler) {
            return new Area<>(x, y, width, height, (r, ingredient) -> dragAndDropHandler.apply(r, ingredient.left().orElseThrow(ClassCastException::new)), r -> Either.left(stackSupplier.apply(r)), clickHandler, scrollHandler);
        }

        public static <R extends Recipe<?>, S, T> Area<R, S, T> createSpecial(int x, int y, int width, int height, BiFunction<R,SpecialAmountedIngredient<S, T>, R> dragAndDropHandler, Function<R, SpecialAmountedIngredient<S, T>> stackSupplier, BiFunction<R, Boolean, R> clickHandler, BiFunction<R, Boolean, R> scrollHandler) {
            return new Area<>(x, y, width, height, (r, ingredient) -> dragAndDropHandler.apply(r, ingredient.swap().orThrow()), r -> Either.right(stackSupplier.apply(r)), clickHandler, scrollHandler);
        }

        public boolean inside(int x, int y) {
            return (this.x <= x && (this.x + this.width) >= x) && (this.y <= y && (this.y + this.height) >= y);
        }

    }
}
