package de.bommels05.ctgui.emi;

import java.util.ArrayList;
import java.util.Collection;

//Used as an Indicator for Mixins
public class TagCollapsingBypassingList<T> extends ArrayList<T> {
    public TagCollapsingBypassingList(Collection<? extends T> c) {
        super(c);
    }

    public TagCollapsingBypassingList() {
        super();
    }
}
