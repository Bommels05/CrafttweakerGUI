package de.bommels05.ctgui.api;

import com.google.common.base.Preconditions;
import de.bommels05.ctgui.ViewerUtils;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;

import java.util.List;

/**
 * A non Item-Ingredient that can either be a stack or a tag both with an amount
 * @param <S> The type of the stack e.g. FluidStack
 * @param <T> The type of the tag (=The type of the Registry of the tag) e.g. Fluid
 */
public abstract class SpecialAmountedIngredient<S, T> {

    private final S stack;
    private final TagKey<T> tag;
    private final int amount;

    protected SpecialAmountedIngredient(S stack, TagKey<T> tag, int amount) {
        Preconditions.checkArgument(!(stack instanceof TagKey<?>), "Wrong constructor used for tag");
        Preconditions.checkArgument(!((stack == null && tag == null) || (stack != null && tag != null)), "Either stack or tag must be null and the other one must not be null");
        Preconditions.checkArgument(amount >= 0, "Amount must be >= 0");
        this.stack = stack;
        this.tag = tag;
        this.amount = amount;
    }

    public abstract SpecialAmountedIngredient<S, T> withAmount(int amount);

    public S toStack() {
        if (!isTagEmpty()) {
            return getStacks().get(0);
        } else {
            throw new IllegalStateException("Empty Tag was not ignored");
        }
    }

    protected List<S> getStacksInternal() {
        return isStack() ? List.of(stack) : ViewerUtils.of(tag);
    }

    public abstract List<S> getStacks();

    public S getStack() {
        return stack;
    }

    public abstract T getStackAsType();

    public abstract Registry<T> getRegistry();

    public TagKey<T> getTag() {
        return tag;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isStack() {
        return stack != null;
    }

    public boolean isTag() {
        return !isStack();
    }

    public boolean isEmpty() {
        return isStack() ? isStackEmpty() : isTagEmpty();
    }

    public boolean isTagEmpty() {
        return getStacks().isEmpty();
    }

    public abstract boolean isStackEmpty();
}
