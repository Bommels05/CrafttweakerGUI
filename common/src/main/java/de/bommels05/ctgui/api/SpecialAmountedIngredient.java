package de.bommels05.ctgui.api;

import com.google.common.base.Preconditions;
import de.bommels05.ctgui.ViewerUtils;
import net.minecraft.tags.TagKey;

import java.util.List;

/**
 * A non Item-Ingredient that can either be a stack or a tag both with an amount
 * @param <S> The type of the stack e.g. FluidStack
 * @param <T> The type of the tag (=The type of the Registry of the tag) e.g. Fluid
 */
public class SpecialAmountedIngredient<S, T> {

    private final S stack;
    private final TagKey<T> tag;
    private final int amount;

    protected SpecialAmountedIngredient(S stack, TagKey<T> tag, int amount) {
        Preconditions.checkArgument(!(stack instanceof TagKey<?>), "Wrong constructor used for tag");
        Preconditions.checkArgument(!((stack == null && tag == null) || (stack != null && tag != null)), "Either stack or tag must be null and the other one must not be null");
        this.stack = stack;
        this.tag = tag;
        this.amount = amount;
    }

    public SpecialAmountedIngredient(S stack, int amount) {
        this(stack, null, amount);
        Preconditions.checkArgument(amount > 0, "Amount must be greater than 0");
    }

    public SpecialAmountedIngredient(S stack) {
        this(stack, null, -1);
    }

    public SpecialAmountedIngredient(TagKey<T> tag, int amount) {
        this(null, tag, amount);
        Preconditions.checkArgument(amount > 0, "Amount must be greater than 0");
    }

    public SpecialAmountedIngredient<S, T> withAmount(int amount) {
        Preconditions.checkArgument(amount > 0, "Amount must be greater than 0");
        return new SpecialAmountedIngredient<>(stack, tag, amount);
    }

    public S toStack() {
        try {
            return getStacks().get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalStateException("Empty Tag was not ignored", e);
        }
    }

    public List<S> getStacks() {
        return stack != null ? List.of(stack) : ViewerUtils.of(tag);
    }

    public S getStack() {
        return stack;
    }

    public TagKey<T> getTag() {
        return tag;
    }

    public int getAmount() {
        Preconditions.checkState(shouldUseAmount(), "Stack amount should be used instead");
        return amount;
    }

    /**
     * @return Whether to use the amount of this SpecialAmountedIngredient or else the amount of the stack
     */
    public boolean shouldUseAmount() {
        return amount != -1;
    }

    public boolean isStack() {
        return stack != null;
    }

    public boolean isTag() {
        return tag != null;
    }

    public boolean isStackEmpty() {
        return false;
    }
}
