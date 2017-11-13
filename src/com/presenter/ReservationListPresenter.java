package com.presenter;

import com.RentalTool;
import com.models.ReservationModel;
import com.models.UserModel;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import com.views.ReservationFormView;
import com.views.ReservationListView;
import com.views.UserListView;
import org.apache.regexp.RE;


public class ReservationListPresenter implements ReservationListView.ReservationListViewListener {

    private ReservationModel model;
    private ReservationListView view;

    public ReservationListPresenter(ReservationModel model, ReservationListView view) {
        this.model = model;
        this.view = view;
        view.addListener(this);
    }

    @Override
    public void userButton(Button.ClickEvent event) {
        String buttonId = event.getButton().getId();
        switch (buttonId) {
            case "create":
                UI.getCurrent().getNavigator().navigateTo(RentalTool.HOME + "/" + RentalTool.CREATE_RESERVATION);
                break;
            case "edit":
                String userId = event.getButton().getData().toString();
                if (userId != null) {
                    UI.getCurrent().getNavigator().navigateTo(RentalTool.HOME + "/" + RentalTool.CREATE_RESERVATION + "/" + userId);
                }
                break;
        }
    }

    @Override
    public void filter (Grid grid) {
        final HeaderRow filterRow = grid.getHeaderRow(1);
        final ListDataProvider<ReservationModel> dataProvider = (ListDataProvider<ReservationModel>) grid.getDataProvider();

        // reset filter
        dataProvider.setFilter(s -> true);

        for (Component column : filterRow.getComponents()) {
            switch (column.getId()) {

                case "ID":
                    TextField id = (TextField) column;
                    System.out.println(column.getId() + "value: " + id.getValue());
                    dataProvider.addFilter(ReservationModel::getReservationId, s -> s.toString().contains(id.getValue()));
                    break;
                case "from":
                    DateField dateFrom = (DateField) column;
                    System.out.println(column.getId() + "value: " + dateFrom.getValue());
                    if (dateFrom.getValue() == null) {
                        break;
                    }
                    dataProvider.addFilter(ReservationModel::getDateFrom, s -> s.isAfter(dateFrom.getValue()));
                    break;
                case "to":
                    DateField dateTo = (DateField) column;
                    System.out.println(column.getId() + "value: " + dateTo.getValue());
                    if (dateTo.getValue() == null) {
                        break;
                    }
                    dataProvider.addFilter(ReservationModel::getDateTo, s -> s.isBefore(dateTo.getValue()));
                    break;
                case "gadgets":
                    TextField gadgets = (TextField) column;
                    System.out.println(column.getId() + "value: " + gadgets.getValue());
                    if (gadgets.getValue() == null || gadgets.getValue().isEmpty()) {
                        break;
                    }
                    // TODO split by comma and add value of with 'and' to filter
                    dataProvider.addFilter(ReservationModel::getGadgets, s -> s.contains(Integer.valueOf(gadgets.getValue())));
                    break;
                case "Status":
                    ComboBox status = (ComboBox) column;
                    System.out.println(column.getId() + "value: " + status.getValue());
                    if (status.getValue() != null) {
                        dataProvider.addFilter(ReservationModel::getStatus, s -> s.equals(status.getValue()));
                    }
                    break;
            }
        }
    }
}