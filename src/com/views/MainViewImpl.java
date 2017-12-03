package com.views;

import com.RentalTool;
import com.SecurePageComponent;
import com.models.GadgetModel;
import com.models.ReservationModel;
import com.models.UserModel;
import com.presenter.*;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.HorizontalLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainViewImpl extends SecurePageComponent implements View, MainView, Button.ClickListener {

    private UserModel user = new UserModel();
    private ReservationModel reservation = new ReservationModel();
    private GadgetModel gadget = new GadgetModel();

    private static final String[] VIEWS = new String[] {
        RentalTool.HOME,
        RentalTool.CREATE_USER,
        RentalTool.LIST_USERS,
        RentalTool.LOGIN,
        RentalTool.CREATE_RESERVATION,
        RentalTool.LIST_RESERVATION,
        RentalTool.ADD_GADGET,
        RentalTool.LIST_GADGETS
    };

    /**
     * Sets initial data to view when it will change
     *
     * @param event the ViewChange event
     */
    public void enter(ViewChangeListener.ViewChangeEvent event) {

        // check access
        checkAccess();

        this.user = new UserModel();
        this.reservation = new ReservationModel();
        this.gadget = new GadgetModel();

        String viewName = RentalTool.HOME;
        Integer id = null;

        // check url params
        if(event.getParameters() != null){
            String[] msgs = event.getParameters().split("/");

            // check view
            if (msgs.length > 0) {
                if (Arrays.asList(VIEWS).contains(msgs[0])) {
                    viewName = msgs[0];
                }
            }

            // check for id
            if (msgs.length > 1 && msgs[1] != null && !msgs[1].trim().isEmpty()) {
                id = Integer.valueOf(msgs[1]);
            }
        }

        // create view for dashboard
        createView(viewName, id);
    }

    /**
     * Creates the dashboard view depending on URL-Fragments
     *
     * @param viewName of dashboard view
     * @param viewId Id of Model we could use in dashboard view
     */
    private void createView(String viewName, Integer viewId) {

        System.out.println("navigate to: " + viewName);

        setHeight("100%");
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.addStyleName("mainview");
        layout.setSpacing(false);

        MenuBarView menu = new MenuBarView();
        menu.setWidth(null);
        menu.setHeight("100%");
        layout.addComponent(menu);

        // set view variables
        switch (viewName) {
            case RentalTool.HOME:
                String sessionUser = String.valueOf(VaadinSession.getCurrent().getAttribute("userID"));
                if (sessionUser != null || !sessionUser.isEmpty()) {
                    this.user = user.getUser(Integer.valueOf(sessionUser));
                    ReservationListViewImpl ownReservationList = new ReservationListViewImpl(this.user.getUserID());
                    UserFormViewImpl main3 = new UserFormViewImpl(this.user);
                    new UserPresenter(this.user, main3);
                    new ReservationListPresenter(this.reservation, ownReservationList);
                    ownReservationList.setSizeFull();
                    ownReservationList.setWidth("100%");
                    layout.addComponent(ownReservationList);
                    layout.setExpandRatio(ownReservationList, 1.0f);
                }
                break;
            case RentalTool.CREATE_USER:
                if (viewId != null) {
                    this.user = this.user.getUser(viewId);
                }
                UserFormViewImpl userForm = new UserFormViewImpl(this.user);
                new UserPresenter(this.user, userForm);
                userForm.setSizeFull();
                userForm.setWidth("100%");
                layout.addComponent(userForm);
                layout.setExpandRatio(userForm, 1.0f);
                break;
            case RentalTool.LIST_USERS:
                UserListViewImpl userList = new UserListViewImpl();
                new UserListPresenter(this.user, userList);
                userList.setSizeFull();
                userList.setWidth("100%");
                layout.addComponent(userList);
                layout.setExpandRatio(userList, 1.0f);
                break;
            case RentalTool.CREATE_RESERVATION:
                if (viewId != null) {
                    this.reservation = this.reservation.getReservation(viewId);
                }
                ReservationFormViewImpl reservationForm = new ReservationFormViewImpl(this.reservation);
                UserFormViewImpl main = new UserFormViewImpl(this.user);
                new UserPresenter(this.user, main);
                new ReservationPresenter(this.reservation, reservationForm);
                reservationForm.setSizeFull();
                reservationForm.setWidth("100%");
                layout.addComponent(reservationForm);
                layout.setExpandRatio(reservationForm, 1.0f);
                break;
            case RentalTool.LIST_RESERVATION:
                ReservationListViewImpl reservationList = new ReservationListViewImpl(null);
                UserFormViewImpl main2 = new UserFormViewImpl(this.user);
                new UserPresenter(this.user, main2);
                new ReservationListPresenter(this.reservation, reservationList);
                reservationList.setSizeFull();
                reservationList.setWidth("100%");
                layout.addComponent(reservationList);
                layout.setExpandRatio(reservationList, 1.0f);
                break;
            case RentalTool.ADD_GADGET:
                if (viewId != null) {
                    this.gadget = this.gadget.getGadget(viewId);
                }
                GadgetFormViewImpl gadgetForm = new GadgetFormViewImpl(this.gadget);
                UserFormViewImpl main3 = new UserFormViewImpl(this.user);
                new UserPresenter(this.user, main3);
                new GadgetPresenter(this.gadget, gadgetForm);
                gadgetForm.setSizeFull();
                gadgetForm.setWidth("100%");
                layout.addComponent(gadgetForm);
                layout.setExpandRatio(gadgetForm, 1.0f);
                break;
            case RentalTool.LIST_GADGETS:
                GadgetListViewImpl gadgetList = new GadgetListViewImpl();
                UserFormViewImpl main4 = new UserFormViewImpl(this.user);
                new UserPresenter(this.user, main4);
                new GadgetListPresenter(this.gadget, gadgetList);
                gadgetList.setSizeFull();
                gadgetList.setWidth("100%");
                layout.addComponent(gadgetList);
                layout.setExpandRatio(gadgetList, 1.0f);
                break;
        }

        setCompositionRoot(layout);
    }

    List<MainViewListener> listeners = new ArrayList<MainViewListener>();

    public void addListener(MainViewListener listener) {
        listeners.add(listener);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        for (MainViewListener listener: listeners)
            listener.buttonClick();
    }
}
