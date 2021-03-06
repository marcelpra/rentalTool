package com;

import com.models.UserModel;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;

public class SecurePageComponent extends CustomComponent implements View {

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        System.out.println("secure page called");

        // check Access
        checkAccess();
    }

    /**
     * Method that checks if the current User has valid access token
     */
    protected void checkAccess() {
        // get user id and token from session
        VaadinSession session = getSession();

        Integer userId = (Integer)session.getAttribute("userID");
        String accessToken = String.valueOf(session.getAttribute("accessToken"));

        if (userId == null || accessToken == null) {
            System.out.println("empty userId or token");
            UI.getCurrent().getNavigator().navigateTo(RentalTool.LOGIN);
            return;
        }

        UserModel user = new UserModel();
        Boolean validate = user.validateSession(userId, accessToken);
        if (!validate) {
            System.out.println("access token expired");
            UI.getCurrent().getNavigator().navigateTo(RentalTool.LOGIN);
        }
    }
}
