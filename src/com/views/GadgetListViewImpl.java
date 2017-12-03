package com.views;

import com.models.GadgetModel;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.Header;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

import java.util.ArrayList;
import java.util.List;

public class GadgetListViewImpl extends CustomComponent implements View, GadgetListView, Button.ClickListener {

    private final Grid<GadgetModel> grid = new Grid<>();

    public GadgetListViewImpl() {

        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("100%");

        Label label = new Label("");
        label.setStyleName("h1 bold align-center");
        label.setHeight(null);
        label.setValue("Gadget List");
        layout.addComponent(label);

        List<GadgetModel> gadgets = GadgetModel.getGadgets();
        ListDataProvider<GadgetModel> dataProvider = new ListDataProvider<>(gadgets);

        grid.setDataProvider(dataProvider);
        grid.setItems(gadgets);
        grid.addComponentColumn(data -> {
            Integer gadgetID = data.getGadgetID();
            Button editButton = new Button(VaadinIcons.EDIT);
            editButton.addStyleNames(ValoTheme.BUTTON_PRIMARY);
            editButton.setSizeFull();
            editButton.setId("edit");
            editButton.setData(gadgetID);
            editButton.addClickListener(event -> buttonClick(event));
            return editButton;
        }).setCaption("Edit").setId("Edit");
        grid.addColumn(GadgetModel::getCategory).setCaption("Category").setId("Category");
        grid.addColumn(GadgetModel::getDescription).setCaption("Description").setId("Description");
        grid.addColumn(GadgetModel::getInventory_No).setCaption("Inventory_No").setId("Inventory_No");
        grid.addComponentColumn(data -> {
            Boolean status = data.getGadget_active();
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
    private void createFilterHeaderRow(Grid grid, ListDataProvider<GadgetModel> data) {
        HeaderRow headerRow = grid.appendHeaderRow();
        Header.Row.Cell cell = (Header.Row.Cell) headerRow.getCell("Category");
        cell.setComponent(filterField("Category"));
        cell = (Header.Row.Cell) headerRow.getCell("Description");
        cell.setComponent(filterField("Description"));
        cell = (Header.Row.Cell) headerRow.getCell("Edit");
        cell.setComponent(addGadget());
        cell = (Header.Row.Cell) headerRow.getCell("Inventory_No");
        cell.setComponent(filterField("Inventory_No"));
    }

    /**
     * Method that return Button for creating a new User
     *
     * @return Button for creating User
     */
    private Button addGadget() {
        Button createButton = new Button("Gadget", VaadinIcons.PLUS);
        createButton.addStyleNames(ValoTheme.BUTTON_PRIMARY, ValoTheme.BUTTON_SMALL);
        createButton.setWidth(100, Unit.PERCENTAGE);
        createButton.setId("add");
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
            /*case "User-Role":
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
                return userRoleField;*/
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

    List<GadgetListViewListener> listeners = new ArrayList<GadgetListViewListener>();

    public void addListener(GadgetListViewListener listener) {
        listeners.add(listener);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        for (GadgetListViewListener listener: listeners) {
            listener.gadgetButton(event);
        }
    }

    private void filter(Grid grid) {
        for (GadgetListViewListener listener: listeners) {
            listener.filter(grid);
        }
    }
}