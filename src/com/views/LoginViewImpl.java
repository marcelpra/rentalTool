package com.views;

import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.ArrayList;
import java.util.List;

public class LoginViewImpl extends CustomComponent implements View, LoginView, Button.ClickListener {

    private Label errorMsg = new Label("");
    private TextField usernameField = new TextField();
    private TextField passwordField = new PasswordField();

    public LoginViewImpl() {

        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();

        Label loginLabel = new Label("Login");
        loginLabel.setStyleName("h1 bold align-center");
        loginLabel.setSizeFull();

        Label usernameLabel = new Label("Username");
        usernameLabel.setWidth("100%");

        Label passwordLabel = new Label("Password");
        passwordLabel.setWidth("100%");

        Button loginButton = new Button("Login", this);
        loginButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        loginButton.setWidth(100, Unit.PERCENTAGE);

        usernameField.setWidth(100, Unit.PERCENTAGE);
        passwordField.setWidth(100, Unit.PERCENTAGE);

        errorMsg.setStyleName("failure align-center");
        errorMsg.setWidth(100, Unit.PERCENTAGE);
        errorMsg.setVisible(false);

        FormLayout form = new FormLayout();
        form.setWidth(400, Unit.PIXELS);
        form.setMargin(true);

        form.addComponent(loginLabel);
        form.addComponent(errorMsg);
        form.addComponent(usernameLabel);
        form.addComponent(usernameField);
        form.addComponent(passwordLabel);
        form.addComponent(passwordField);
        form.addComponent(loginButton);
        form.setComponentAlignment(loginLabel, Alignment.MIDDLE_CENTER);

        layout.addComponent(form);
        layout.setComponentAlignment(form, Alignment.MIDDLE_CENTER);

        setCompositionRoot(layout);
    }

    public void setDisplay(String value) {
        errorMsg.setValue(value);
        errorMsg.setVisible(true);
    }

    List<LoginViewListener> listeners = new ArrayList<LoginViewListener>();

    public void addListener(LoginViewListener listener) {
        listeners.add(listener);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        for (LoginViewListener listener: listeners)
            listener.buttonClick(this.usernameField.getValue(), this.passwordField.getValue());
    }
}