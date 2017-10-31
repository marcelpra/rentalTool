package com.views;
import com.SecurePageComponent;
import com.models.UserModel;
import com.presenter.UserPresenter;
import com.vaadin.data.*;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import java.util.ArrayList;
import java.util.List;

public class CreateUserViewImpl extends SecurePageComponent implements CreateUserView, Button.ClickListener {

    private Label errorMsg = new Label("");
    private Label successMsg = new Label("");

    private TextField emailField = new TextField("Email");
    private TextField passwordField = new TextField("Password");
    private TextField firstnameField = new TextField("Firstname");
    private TextField lastnameField = new TextField("Lastname");
    private TextField departmentField = new TextField("Department");

    private UserModel user = new UserModel();

    private ComboBox<String> statusField = new ComboBox<>("Status");
    private ComboBox<String> userroleField = new ComboBox<>("Select User-Role");

    public CreateUserViewImpl() {

        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();

        Label header = new Label("Create new User");
        header.setStyleName("h1 bold align-center");
        header.setSizeFull();

        // do some styling to fields
        emailField.setWidth(100, Unit.PERCENTAGE);
        passwordField.setWidth(100, Unit.PERCENTAGE);
        firstnameField.setWidth(100, Unit.PERCENTAGE);
        lastnameField.setWidth(100, Unit.PERCENTAGE);
        departmentField.setWidth(100, Unit.PERCENTAGE);
        statusField.setWidth(100, Unit.PERCENTAGE);
        statusField.setEmptySelectionAllowed(false);
        statusField.setItems("active", "inactive");
        statusField.setValue("active");
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
                .asRequired("Email required")
                .withValidator(new EmailValidator("invalid email"))
                .bind(UserModel::getUsername, UserModel::setUsername);
        binder.forField(firstnameField)
                .asRequired("Forename required")
                .bind(UserModel::getFirstname, UserModel::setFirstname);
        binder.forField(lastnameField)
                .asRequired("Lastname required")
                .bind(UserModel::getLastname, UserModel::setLastname);
        binder.forField(departmentField)
                .asRequired("Department required")
                .bind(UserModel::getDepartment, UserModel::setDepartment);
        binder.forField(passwordField)
                .asRequired("Password required")
                .bind(UserModel::getPassword, UserModel::setPassword);
        binder.forField(userroleField)
                .asRequired("User Role must be selected")
                .bind(UserModel::getUserRole, UserModel::setUserRole);
        binder.setBean(user);

        // adding button with form validation
        binder.readBean(user);
        Button createButton = new Button("Create User",
        event -> {
                // reset error and success messages
                errorMsg.setVisible(false);
                successMsg.setVisible(false);
                try {
                        binder.writeBean(user);
                        buttonClick(event);
                } catch (ValidationException e) {
                        setErrorMsg("User User could not be saved, please check required fields.");
                }
        });
        createButton.setStyleName("primary align-center");
        createButton.setWidth(100, Unit.PERCENTAGE);

        // add all components to form
        form.addComponents(
                header,
                errorMsg,
                successMsg,
                emailField,
                firstnameField,
                lastnameField,
                departmentField,
                passwordField,
                statusField,
                userroleField,
                createButton
        );
        form.setComponentAlignment(header, Alignment.MIDDLE_CENTER);

        // adding form to layout
        layout.addComponent(form);
        layout.setComponentAlignment(form, Alignment.MIDDLE_CENTER);

        setCompositionRoot(layout);
    }

    public void setErrorMsg(String value) {
        if (value.length() > 0) {
                errorMsg.setValue(value);
                errorMsg.setVisible(true);
        }
    }

    public void setSuccessMsg(String value) {
        if ( value.length() > 0) {
                successMsg.setValue(value);
                successMsg.setVisible(true);
        }
    }

    List<UserViewListener> listeners = new ArrayList<UserViewListener>();
    public void addListener(UserViewListener listener) {
            listeners.add(listener);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        for (UserViewListener listener: listeners)
                listener.buttonClick(this.user);
    }
}