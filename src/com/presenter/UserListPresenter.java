package com.presenter;

import com.RentalTool;
import com.models.UserModel;
import com.vaadin.data.HasValue;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import com.views.UserListView;


public class UserListPresenter implements UserListView.UserListViewListener {

    private UserModel model;
    private UserListView view;

    public UserListPresenter(UserModel model, UserListView view) {
        this.model = model;
        this.view = view;
        view.addListener(this);
    }

    @Override
    public void userButton(Button.ClickEvent event) {
        String buttonId = event.getButton().getId();
        switch (buttonId) {
            case "create":
                UI.getCurrent().getNavigator().navigateTo(RentalTool.HOME + "/" + RentalTool.CREATE_USER);
                break;
            case "edit":
                String userId = event.getButton().getData().toString();
                if (userId != null) {
                    UI.getCurrent().getNavigator().navigateTo(RentalTool.HOME + "/" + RentalTool.CREATE_USER + "/" + userId);
                }
                break;
        }
    }

    @Override
    public void filter (Grid grid) {
        final HeaderRow filterRow = grid.getHeaderRow(1);
        final ListDataProvider<UserModel> dataProvider = (ListDataProvider<UserModel>) grid.getDataProvider();

        // reset filter
        dataProvider.setFilter(s -> true);

        for (Component column : filterRow.getComponents()) {
            switch (column.getId()) {

                case "ID":
                    TextField id = (TextField) column;
                    System.out.println(column.getId() + "value: " + id.getValue());
                    dataProvider.addFilter(UserModel::getUserID, s -> s.toString().contains(id.getValue()));
                    break;
                case "Email":
                    TextField email = (TextField) column;
                    System.out.println(column.getId() + "value: " + email.getValue());
                    dataProvider.addFilter(UserModel::getEmail, s -> s.contains(email.getValue()));
                    break;
                case "Firstname":
                    TextField firstName = (TextField) column;
                    System.out.println(column.getId() + "value: " + firstName.getValue());
                    dataProvider.addFilter(UserModel::getFirstname, s -> s.contains(firstName.getValue()));
                    break;
                case "Lastname":
                    TextField lastName = (TextField) column;
                    System.out.println(column.getId() + "value: " + lastName.getValue());
                    dataProvider.addFilter(UserModel::getLastname, s -> s.contains(lastName.getValue()));
                    break;
                case "Department":
                    TextField department = (TextField) column;
                    System.out.println(column.getId() + "value: " + department.getValue());
                    dataProvider.addFilter(UserModel::getDepartment, s -> s.contains(department.getValue()));
                    break;
                case "User-Role":
                    ComboBox userRole = (ComboBox) column;
                    System.out.println(column.getId() + "value: " + userRole.getValue());
                    if (userRole.getValue() != null) {
                        dataProvider.addFilter(UserModel::getUserRole, s -> s.equals(userRole.getValue()));
                    }
                    break;
                case "Status":
                    ComboBox status = (ComboBox) column;
                    System.out.println(column.getId() + "value: " + status.getValue());
                    if (status.getValue() != null) {
                        dataProvider.addFilter(UserModel::getStatus, s -> s.equals(status.getValue()));
                    }
                    break;
            }
        }
    }
}