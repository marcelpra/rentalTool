package com.views;

import com.SecurePageComponent;
import com.models.GadgetModel;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.List;

public class GadgetFormViewImpl extends SecurePageComponent implements GadgetFormView, Button.ClickListener {

    private Label errorMsg = new Label("");
    private Label successMsg = new Label("");

    private TextField categoryField = new TextField("Category");
    private TextField descriptionField = new TextField("Description");
    private TextField inventory_NoField = new TextField("Inventory_No");

    private GadgetModel gadget = new GadgetModel();

    private ComboBox<Boolean> statusField = new ComboBox<>("Status");

    public GadgetFormViewImpl(GadgetModel gadget) {

        System.out.println("constructor enter");

        this.gadget = gadget;

        // creating layout
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();

        // adding layout
        setCompositionRoot(layout);

        // adding form
        setForm(layout, this.gadget);
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
     * Creates a Form to update or create a User
     *
     * @param layout the complete where the form should be added
     * @param gadget the GadgetModel
     */
    private void setForm(HorizontalLayout layout, GadgetModel gadget) {

        Label header = new Label("");
        header.setStyleName("h1 bold align-center");
        header.setSizeFull();

        System.out.println("");

        // do some styling to fields
        categoryField.setWidth(100, Unit.PERCENTAGE);
        descriptionField.setWidth(100, Unit.PERCENTAGE);
        inventory_NoField.setWidth(100, Unit.PERCENTAGE);
        /*departmentField.setWidth(100, Unit.PERCENTAGE);*/
        statusField.setWidth(100, Unit.PERCENTAGE);
        statusField.setEmptySelectionAllowed(false);
        statusField.setItemCaptionGenerator(new ItemCaptionGenerator<Boolean>() {
            @Override
            public String apply(Boolean aBoolean) {
                return aBoolean ? "active" : "inactive";
            }
        });

        List<Boolean> statusSelect = new ArrayList<>();
        statusSelect.add(0, true);
        statusSelect.add(1, false);

        statusField.setItems(statusSelect);

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

        System.out.println(categoryField.getValue());
        System.out.println(descriptionField.getValue());
        System.out.println(inventory_NoField.getValue());
        System.out.println(statusField.getValue());

        // bind form to gadgetModel
        Binder<GadgetModel> binder = new Binder<>();
        binder.forField(categoryField)
                .bind(GadgetModel::getCategory, GadgetModel::setCategory);
        binder.forField(descriptionField)
                .bind(GadgetModel::getDescription, GadgetModel::setDescription);
        binder.forField(inventory_NoField)
                .bind(GadgetModel::getInventory_No, GadgetModel::setInventory_No);
        binder.forField(statusField)
                .bind(GadgetModel::getGadget_active, GadgetModel::setGadget_active);
        binder.setBean(gadget);

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
        if (gadget.getGadgetID() != null) {
            header.setValue("Update Gadget with ID: " + gadget.getGadgetID().toString());
            createButton.setCaption("Update Gadget");
        } else {
            createButton.setCaption("Add Gadget");
            header.setValue("Add new Gadget");
            statusField.setValue(true);
        }

        // add all components to form
        form.addComponents(
                header,
                errorMsg,
                successMsg,
                categoryField,
                descriptionField,
                inventory_NoField,
                statusField,
                createButton
        );
        form.setComponentAlignment(header, Alignment.TOP_CENTER);
        layout.addComponent(form);
        layout.setComponentAlignment(form, Alignment.TOP_CENTER);
    }

    List<GadgetViewListener> listeners = new ArrayList<GadgetViewListener>();
    public void addListener(GadgetViewListener listener) {
        listeners.add(listener);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        for (GadgetViewListener listener: listeners)
            listener.buttonClick(gadget);
    }
}