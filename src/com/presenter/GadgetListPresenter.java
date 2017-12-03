package com.presenter;

import com.RentalTool;
import com.models.GadgetModel;
import com.models.UserModel;
import com.vaadin.data.HasValue;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import com.views.GadgetListView;


public class GadgetListPresenter implements GadgetListView.GadgetListViewListener {

    private GadgetModel model;
    private GadgetListView view;

    public GadgetListPresenter(GadgetModel model, GadgetListView view) {
        this.model = model;
        this.view = view;
        view.addListener(this);
    }

    @Override
    public void gadgetButton(Button.ClickEvent event) {
        String buttonId = event.getButton().getId();
        switch (buttonId) {
            case "add":
                UI.getCurrent().getNavigator().navigateTo(RentalTool.HOME + "/" + RentalTool.ADD_GADGET);
                break;
            case "edit":
                String gadgetID = event.getButton().getData().toString();
                if (gadgetID != null) {
                    UI.getCurrent().getNavigator().navigateTo(RentalTool.HOME + "/" + RentalTool.ADD_GADGET + "/" + gadgetID);
                }
                break;
        }
    }

    @Override
    public void filter(Grid grid) {
        final HeaderRow filterRow = grid.getHeaderRow(1);
        final ListDataProvider<GadgetModel> dataProvider = (ListDataProvider<GadgetModel>) grid.getDataProvider();

        // reset filter
        dataProvider.setFilter(s -> true);

        for (Component column : filterRow.getComponents()) {
            switch (column.getId()) {

                case "Category":
                    TextField category = (TextField) column;
                    System.out.println(column.getId() + "value: " + category.getValue());
                    dataProvider.addFilter(GadgetModel::getCategory, s -> s.contains(category.getValue()));
                    break;
                case "ID":
                    TextField gadgetID = (TextField) column;
                    System.out.println(column.getId() + "value: " + gadgetID.getValue());
                    dataProvider.addFilter(GadgetModel::getGadgetID, s -> s.equals(gadgetID.getValue()));
                    break;
                case "Description":
                    TextField description = (TextField) column;
                    System.out.println(column.getId() + "value: " + description.getValue());
                    dataProvider.addFilter(GadgetModel::getDescription, s -> s.contains(description.getValue()));
                    break;
                case "Inventory_No":
                    TextField inventory_no = (TextField) column;
                    System.out.println(column.getId() + "value: " + inventory_no.getValue());
                    dataProvider.addFilter(GadgetModel::getInventory_No, s -> s.equals(inventory_no.getValue()));
                    break;
                case "Gadget_active":
                    TextField gadget_active = (TextField) column;
                    System.out.println(column.getId() + "value: " + gadget_active.getValue());
                    dataProvider.addFilter(GadgetModel::getGadget_active, s -> s.equals(gadget_active.getValue()));
                    break;
            }
        }
    }
}

