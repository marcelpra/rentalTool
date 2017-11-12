package com.views;

import com.vaadin.navigator.View;

public interface MainView extends View {
    interface MainViewListener {
        void buttonClick();
    }
    public void addListener(MainViewListener listener);
}