package com.views;

import com.vaadin.navigator.View;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

public interface GadgetListView extends View {

    interface GadgetListViewListener {
        void gadgetButton(Button.ClickEvent event);
        void filter(Grid grid);
    }
    public void addListener(GadgetListViewListener listener);
}