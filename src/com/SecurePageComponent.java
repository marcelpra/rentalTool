package com;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;

public class SecurePageComponent extends CustomComponent implements View {

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        System.out.println("secure page called");

        // get user id from session
        // TODO get also access token from session
        VaadinSession session = getSession();

        Object userId = session.getAttribute("userId");

        System.out.println(userId);

        if (userId == null) {
            System.out.println("we should navigate");
            // TODO we should remove all Secured views from navigation
            // UI.getCurrent().getNavigator().removeView("createUser");
            UI.getCurrent().getNavigator().navigateTo("");
        }
        // Integer.parseInt(String)
        // TODO implement session token search in database and validate the token
    }
}
