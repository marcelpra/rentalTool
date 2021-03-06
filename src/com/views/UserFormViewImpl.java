package com.views;
import com.SecurePageComponent;
import com.models.UserModel;
import com.vaadin.data.*;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import java.util.ArrayList;
import java.util.List;

public class UserFormViewImpl extends SecurePageComponent implements UserFormView, Button.ClickListener {

    private Label errorMsg = new Label("");
    private Label successMsg = new Label("");

    private TextField emailField = new TextField("Email");
    private TextField firstnameField = new TextField("Firstname");
    private TextField lastnameField = new TextField("Lastname");
    private TextField departmentField = new TextField("Department");

    private UserModel user = new UserModel();

    private ComboBox<Boolean> statusField = new ComboBox<>("Status");
    private ComboBox<String> userroleField = new ComboBox<>("Select User-Role");

    public UserFormViewImpl(UserModel user) {

        System.out.println("constructor enter");

        this.user = user;

        // creating layout
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();

        // adding layout
        setCompositionRoot(layout);

        // adding form
        setForm(layout, this.user);
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
     * @param user the userModel
     */
    private void setForm(HorizontalLayout layout, UserModel user) {

        Label header = new Label("");
        header.setStyleName("h1 bold align-center");
        header.setSizeFull();

        // do some styling to fields
        emailField.setWidth(100, Unit.PERCENTAGE);
        firstnameField.setWidth(100, Unit.PERCENTAGE);
        lastnameField.setWidth(100, Unit.PERCENTAGE);
        departmentField.setWidth(100, Unit.PERCENTAGE);
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
        userroleField.setWidth(100, Unit.PERCENTAGE);
        userroleField.setEmptySelectionAllowed(false);
        userroleField.setEmptySelectionCaption("please select");
        userroleField.setItems("Admin", "Employee", "User");

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
        Binder<UserModel> binder = new Binder<>();
        binder.forField(emailField)
                .withValidator(new EmailValidator("invalid email"))
                .bind(UserModel::getEmail, UserModel::setEmail);
        binder.forField(firstnameField)
                .bind(UserModel::getFirstname, UserModel::setFirstname);
        binder.forField(lastnameField)
                .bind(UserModel::getLastname, UserModel::setLastname);
        binder.forField(departmentField)
                .bind(UserModel::getDepartment, UserModel::setDepartment);
        binder.forField(userroleField)
                .bind(UserModel::getUserRole, UserModel::setUserRole);
        binder.forField(statusField)
                .bind(UserModel::getStatus, UserModel::setStatus);
        binder.setBean(user);

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
        if (user.getUserID() != null) {
            header.setValue("Update User with ID: " + user.getUserID().toString());
            createButton.setCaption("Update User");
        } else {
            createButton.setCaption("Create User");
            header.setValue("Create new User");
            statusField.setValue(true);
        }

        // add all components to form
        form.addComponents(
                header,
                errorMsg,
                successMsg,
                emailField,
                firstnameField,
                lastnameField,
                departmentField,
                statusField,
                userroleField,
                createButton
        );
        form.setComponentAlignment(header, Alignment.TOP_CENTER);
        layout.addComponent(form);
        layout.setComponentAlignment(form, Alignment.TOP_CENTER);
    }

    List<UserViewListener> listeners = new ArrayList<UserViewListener>();
    public void addListener(UserViewListener listener) {
        listeners.add(listener);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        for (UserViewListener listener: listeners)
            listener.buttonClick(user);
    }
}