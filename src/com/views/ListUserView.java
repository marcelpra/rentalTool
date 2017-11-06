package com.views;

import com.vaadin.navigator.View;

public interface ListUserView extends View {

    interface ListUserViewListener {
        void buttonClick();
    }
    public void addListener(ListUserViewListener listener);
}