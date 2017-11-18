package com.views;

import com.SecurePageComponent;
import com.models.AvailabilityModel;
import com.models.ReservationModel;
import com.vaadin.data.*;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.ArrayList;
import java.util.List;

public class ReservationFormViewImpl extends SecurePageComponent implements ReservationFormView, Button.ClickListener {

    private Label errorMsg = new Label("");
    private Label successMsg = new Label("");

    private DateField dateFromField = new DateField("Date from");
    private DateField dateToField = new DateField("Date to");

    private ReservationModel reservation = new ReservationModel();

    private ComboBox<Boolean> statusField = new ComboBox<>("Status");
    private ComboBox<String> gadgetCategoryField = new ComboBox<>("Select Gadget-Category");
    private ComboBox<Integer> gadgetField = new ComboBox<>("Select Gadget");

    public ReservationFormViewImpl(ReservationModel reservation) {
        this.reservation = reservation;

        // write availability
        Boolean updateSuccess = AvailabilityModel.prepareAvailability();

        // TODO catch error when saving availability

        // creating layout
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();

        // adding layout
        setCompositionRoot(layout);

        // adding form
        setForm(layout, this.reservation);
    }

    /**
     * Sets an error message
     *
     * @param value the message to be displayed as error message
     */
    public void setErrorMsg(String value) {
        if (value.length() > 0) {
                errorMsg.setValue(value);
                errorMsg.setVisible(true);
        }
    }

    /**
     * Sets a success message
     *
     * @param value the message to be displayed as success message
     */
    public void setSuccessMsg(String value) {
        if ( value.length() > 0) {
                successMsg.setValue(value);
                successMsg.setVisible(true);
        }
    }

    /**
     * Creates a Form to update or create a Reservation
     *
     * @param layout the complete where the form should be added
     * @param reservation the reservationModel
     */
    private void setForm(HorizontalLayout layout, ReservationModel reservation) {

        Label header = new Label("");
        header.setStyleName("h1 bold align-center");
        header.setSizeFull();

        CssLayout selectedGadgetsArea = new CssLayout();

        // do some styling to fields
        dateFromField.setWidth(100, Unit.PERCENTAGE);
        dateToField.setWidth(100, Unit.PERCENTAGE);
        gadgetCategoryField.setWidth(100, Unit.PERCENTAGE);
        gadgetField.setWidth(100, Unit.PERCENTAGE);
        statusField.setWidth(100, Unit.PERCENTAGE);
        statusField.setItemCaptionGenerator(new ItemCaptionGenerator<Boolean>() {
            @Override
            public String apply(Boolean aBoolean) {
                return aBoolean ? "active" : "cancelled";
            }
        });

        List<Boolean> statusSelect = new ArrayList<>();
        statusSelect.add(0, true);
        statusSelect.add(1, false);

        statusField.setItems(statusSelect);

        // TODO select DISTINCT Gadget Categories

        // TODO add placeholders to Fields

        // TODO replace getGadgetName function
        gadgetField.setItemCaptionGenerator(new ItemCaptionGenerator<Integer>() {
            @Override
            public String apply(Integer integer) {
                // TODO rewrite same function in GadgetModel
                return getGadgetName(integer);
            }
        });
        List<Integer> gadgetSelect = new ArrayList<>();
        gadgetSelect.add(0, 1);
        gadgetSelect.add(1, 2);
        gadgetSelect.add(2, 3);
        gadgetSelect.add(3, 4);

        gadgetField.setItems(gadgetSelect);
        gadgetField.addValueChangeListener(event -> {
            System.out.println("selected item: " + event.getValue());
            addGadgetSelection(event.getValue(), selectedGadgetsArea);
        });

        createGadgetView(reservation.getGadgets(), selectedGadgetsArea, null);

        // define error message label
        errorMsg.setStyleName("failure align-center");
        errorMsg.setWidth(100, Unit.PERCENTAGE);
        errorMsg.setVisible(false);

        // define success message label
        successMsg.setStyleName("success align-center");
        successMsg.setWidth(100, Unit.PERCENTAGE);
        successMsg.setVisible(false);

        // create new form
        FormLayout form = new FormLayout();
        form.setWidth(100, Unit.PERCENTAGE);
        form.setMargin(true);

        // bind form to userModel
        Binder<ReservationModel> binder = new Binder<>();
        binder.forField(dateFromField)
                .bind(ReservationModel::getDateFrom, ReservationModel::setDateFrom);
        binder.forField(dateToField)
                .bind(ReservationModel::getDateTo, ReservationModel::setDateTo);
        binder.forField(statusField)
                .bind(ReservationModel::getStatus, ReservationModel::setStatus);
        binder.setBean(reservation);

        // adding button with form validation
        Button createButton = new Button("",
                event -> {
                    // reset error and success messages
                    errorMsg.setVisible(false);
                    successMsg.setVisible(false);
                    buttonClick(event);
                });
        createButton.setStyleName("primary align-center");
        createButton.setWidth(100, Unit.PERCENTAGE);

        // set correct captures
        if (reservation.getReservationId() != null) {
            header.setValue("Update Reservation with ID: " + reservation.getReservationId().toString());
            createButton.setCaption("Update Reservation");
        } else {
            createButton.setCaption("Reservation User");
            header.setValue("Create new Reservation");
            statusField.setValue(true);
        }

        // add all components to form
        form.addComponents(
                header,
                errorMsg,
                successMsg,
                dateFromField,
                dateToField,
                gadgetCategoryField,
                gadgetField,
                selectedGadgetsArea,
                statusField,
                createButton
        );
        form.setComponentAlignment(header, Alignment.TOP_CENTER);
        layout.addComponent(form);
        layout.setComponentAlignment(form, Alignment.TOP_CENTER);
    }

    private void createGadgetView(ArrayList<Integer> gadgetList, CssLayout layout, Integer skipGadget) {

        ArrayList<Integer> resultList = new ArrayList<Integer>();

        for(Integer gadget : gadgetList) {
            if (gadget.equals(skipGadget)) {
                continue;
            }
            Button selectedItem = new Button();
            selectedItem.addStyleNames(ValoTheme.MENU_BADGE);
            selectedItem.setIcon(VaadinIcons.CLOSE);
            selectedItem.setData(gadget);
            selectedItem.addClickListener(event -> {
                removeGadgetSelection(gadget, layout);
            });
            selectedItem.setCaption(getGadgetName(gadget));
            layout.addComponent(selectedItem);
            resultList.add(gadget);
        }
        reservation.setGadgets(resultList);
    }

    private String getGadgetName(Integer gadgetId) {
        switch (gadgetId) {
            case 1: return ("Gadget 1");
            case 2: return ("Gadget 2");
            case 3: return ("Gadget 3");
            case 4: return ("Gadget 4");
        }
        return "";
    }

    private void addGadgetSelection(Integer gadgetId, CssLayout layout) {
        // if null selection
        if (gadgetId == null) {
            return;
        }

        ArrayList<Integer> oldGadgets = new ArrayList<>();
        oldGadgets.addAll(reservation.getGadgets());

        // check if item is already in list
        if (oldGadgets.contains(gadgetId)) {
            System.out.println("gadget already in list");
            setErrorMsg("gadget already in list");
            return;
        }
        oldGadgets.add(gadgetId);
        layout.removeAllComponents();

        createGadgetView(oldGadgets, layout, null);
    }

    private void removeGadgetSelection(Integer gadgetId, CssLayout layout) {

        layout.removeAllComponents();
        ArrayList<Integer> oldGadgets = new ArrayList<>();
        oldGadgets.addAll(reservation.getGadgets());
        reservation.setGadgets(new ArrayList<>());
        layout.removeAllComponents();

        createGadgetView(oldGadgets, layout, gadgetId);
    }

    List<ReservationViewListener> listeners = new ArrayList<ReservationViewListener>();
    public void addListener(ReservationViewListener listener) {
        listeners.add(listener);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        for (ReservationViewListener listener: listeners)
            listener.buttonClick(this.reservation);
    }
}