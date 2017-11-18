package com.views;

import com.vaadin.navigator.View;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

public interface UserListView extends View {

    interface UserListViewListener {
        void userButton(Button.ClickEvent event);
        void filter(Grid grid);
    }
    public void addListener(UserListViewListener listener);
}