package com.views;

import com.vaadin.navigator.View;

public interface LoginView extends View {
    public void setDisplay (String value);

    interface LoginViewListener {
        void buttonClick(String username, String password);
    }
    public void addListener (LoginViewListener listener);
}