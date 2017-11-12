package com.views;

import com.models.UserModel;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.*;

public class UserListViewImpl extends CustomComponent implements View, UserListView, Button.ClickListener {

    private final Grid<UserModel> grid = new Grid<>();

    public UserListViewImpl() {

        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("100%");

        Label label = new Label("");
        label.setStyleName("h1 bold align-center");
        label.setHeight(null);
        label.setValue("User List");
        layout.addComponent(label);

        List<UserModel> users = UserModel.getUsers();
        ListDataProvider<UserModel> dataProvider = new ListDataProvider<>(users);

        grid.setDataProvider(dataProvider);
        grid.setItems(users);
        grid.addComponentColumn(data -> {
            Integer userId = data.getUserID();
            Button editButton = new Button(VaadinIcons.EDIT);
            editButton.addStyleNames(ValoTheme.BUTTON_PRIMARY);
            editButton.setSizeFull();
            editButton.setId("edit");
            editButton.setData(userId);
            editButton.addClickListener(event -> buttonClick(event));
            return editButton;
        }).setCaption("Edit").setId("Edit");
        grid.addColumn(UserModel::getUserID).setCaption("ID").setId("ID");
        grid.addColumn(UserModel::getEmail).setCaption("Email-Address").setId("Email");
        grid.addColumn(UserModel::getFirstname).setCaption("Firstname").setId("Firstname");
        grid.addColumn(UserModel::getLastname).setCaption("Lastname").setId("Lastname");
        grid.addColumn(UserModel::getDepartment).setCaption("Department").setId("Department");
        grid.addComponentColumn(data -> {
            Boolean status = data.getStatus();
            Label activeLabel = new Label();
            if (status) {
                activeLabel.setValue("active");
                activeLabel.addStyleName(ValoTheme.LABEL_SUCCESS);

            } else {
                activeLabel.setValue("inactive");
                activeLabel.addStyleName(ValoTheme.LABEL_FAILURE);
                activeLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
            }
            activeLabel.setSizeFull();
            activeLabel.addStyleName(ValoTheme.LABEL_TINY);

            return activeLabel;
        }).setCaption("Status").setId("Status");
        grid.addColumn(UserModel::getUserRole).setCaption("User-Role").setId("User-Role");
        grid.setSizeFull();
        grid.setRowHeight(50);
        grid.setWidth("100%");
        createFilterHeaderRow(grid, dataProvider);
        layout.addComponent(grid);

        layout.setExpandRatio(grid, 1.0f);

        setCompositionRoot(layout);
    }

    /**
     * Method that creates second header row with filter possibilities
     *
     * @param grid the grid where the filter header should be added
     * @param data the data provider
     */
    private void createFilterHeaderRow(Grid grid, ListDataProvider<UserModel> data) {
        HeaderRow headerRow = grid.appendHeaderRow();
        Header.Row.Cell cell = (Header.Row.Cell) headerRow.getCell("ID");
        cell.setComponent(filterField("ID"));
        cell = (Header.Row.Cell) headerRow.getCell("Email");
        cell.setComponent(filterField("Email"));
        cell = (Header.Row.Cell) headerRow.getCell("Edit");
        cell.setComponent(createUser());
        cell = (Header.Row.Cell) headerRow.getCell("Firstname");
        cell.setComponent(filterField("Firstname"));
        cell = (Header.Row.Cell) headerRow.getCell("Lastname");
        cell.setComponent(filterField("Lastname"));
        cell = (Header.Row.Cell) headerRow.getCell("Department");
        cell.setComponent(filterField("Department"));
        cell = (Header.Row.Cell) headerRow.getCell("Status");
        cell.setComponent(filterDropDown("Status"));
        cell = (Header.Row.Cell) headerRow.getCell("User-Role");
        cell.setComponent(filterDropDown("User-Role"));
    }

    /**
     * Method that return Button for creating a new User
     *
     * @return Button for creating User
     */
    private Button createUser() {
        Button createButton = new Button("User", VaadinIcons.PLUS);
        createButton.addStyleNames(ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL);
        createButton.setWidth(100, Unit.PERCENTAGE);
        createButton.setId("create");
        createButton.addClickListener(event -> buttonClick(event));
        return createButton;
    }

    /**
     * Method that creates a Dropdown Filter Item for Filter Header
     *
     * @param id of Column
     * @return ComboBox with possible filter values
     */
    private ComboBox filterDropDown(String id) {
        switch (id) {
            case "Status":
                ComboBox<Boolean> statusField = new ComboBox<>();
                List<Boolean> statusSelect = new ArrayList<>();
                statusField.setItemCaptionGenerator((ItemCaptionGenerator<Boolean>) aBoolean -> aBoolean ? "active" : "inactive");
                statusSelect.add(0, true);
                statusSelect.add(1, false);
                statusField.setItems(statusSelect);
                statusField.addValueChangeListener(event -> filter(grid));
                statusField.setStyleName(ValoTheme.COMBOBOX_TINY);
                statusField.setPlaceholder(id);
                statusField.setId(id);
                return statusField;
            case "User-Role":
                ComboBox<String> userRoleField = new ComboBox<>();
                List<String> userRoleSelect = new ArrayList<>();
                userRoleSelect.add(0, UserModel.ROLE_ADMIN);
                userRoleSelect.add(1, UserModel.ROLE_EMPLOYEE);
                userRoleSelect.add(2, UserModel.ROLE_USER);
                userRoleField.setItems(userRoleSelect);
                userRoleField.addValueChangeListener(event -> filter(grid));
                userRoleField.setStyleName(ValoTheme.COMBOBOX_TINY);
                userRoleField.setPlaceholder(id);
                userRoleField.setId(id);
                return userRoleField;
        }
        return new ComboBox();
    }

    /**
     * Method that creates a TextField Filter Item for Filter Header
     *
     * @param id of Column
     * @return Textfield for entering filter values
     */
    private TextField filterField(String id) {
        TextField field = new TextField();
        field.setDescription("filter for " + id);
        field.setPlaceholder(id);
        field.setStyleName(ValoTheme.TEXTFIELD_TINY);
        if (id.equals("ID") || id.equals("Status")) {
            field.setWidth("100px");
        } else {
            field.setWidth(null);
        }
        field.addValueChangeListener(event -> filter(grid));
        field.setId(id);

        return field;
    }

    List<UserListViewListener> listeners = new ArrayList<UserListViewListener>();

    public void addListener(UserListViewListener listener) {
        listeners.add(listener);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        for (UserListViewListener listener: listeners) {
            listener.userButton(event);
        }
    }

    private void filter(Grid grid) {
        for (UserListViewListener listener: listeners) {
            listener.filter(grid);
        }
    }
}