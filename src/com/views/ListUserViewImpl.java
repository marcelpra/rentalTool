package com.views;

import com.models.UserModel;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListUserViewImpl extends CustomComponent implements View, ListUserView, Button.ClickListener {

    public ListUserViewImpl() {

        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();

        List<UserModel> users = UserModel.getUsers();
        ListDataProvider<UserModel> dataProvider = new ListDataProvider<>(users);

        Grid<UserModel> grid = new Grid<>();
        grid.setDataProvider(dataProvider);
        grid.setItems(users);
        grid.addColumn(UserModel::getUserId).setCaption("ID");
        grid.addColumn(UserModel::getFirstname).setCaption("Firsname");
        grid.addColumn(UserModel::getLastname).setCaption("Lastname");

        layout.addComponent(grid);
        setCompositionRoot(layout);
    }

    List<ListUserViewListener> listeners = new ArrayList<ListUserViewListener>();

    public void addListener(ListUserViewListener listener) {
        listeners.add(listener);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        for (ListUserViewListener listener: listeners)
            listener.buttonClick();
    }
}