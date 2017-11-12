package com.views;

import com.RentalTool;
import com.models.UserModel;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

public class MenuBarView extends CustomComponent {

    private UserModel user = new UserModel();

    public MenuBarView() {
        setPrimaryStyleName(ValoTheme.MENU_ROOT);
        setCompositionRoot(buildMenu());
    }

    /**
     * Returns Menu component
     *
     * @return Component - the navigation Menu
     */
    private Component buildMenu() {
        final CssLayout menuContent = new CssLayout();
        menuContent.addStyleName("sidebar");
        menuContent.addStyleName(ValoTheme.MENU_PART);
        menuContent.addStyleName("no-vertical-drag-hints");
        menuContent.addStyleName("no-horizontal-drag-hints");
        menuContent.setWidthUndefined();
        menuContent.setHeight("100%");

        menuContent.addComponent(UserMenu());
        menuContent.addComponent(buildMenuItems());

        return menuContent;
    }

    /**
     *
     * @return Component - the user Menu
     */
    private Component UserMenu() {
        final MenuBar settings = new MenuBar();
        settings.addStyleName("user-menu");

        Integer userId = (Integer)VaadinSession.getCurrent().getAttribute("userID");
        if (userId != null) {
            this.user = this.user.getUser(userId);
        }

        settings.addItem(this.user.getFirstname() + " " + this.user.getLastname(),
                new ThemeResource("img/profile-pic-300px.jpg"), new MenuBar.Command() {
                    @Override
                    public void menuSelected(MenuBar.MenuItem menuItem) {
                        UI.getCurrent().getNavigator().navigateTo(RentalTool.HOME + "/" + RentalTool.CREATE_USER + "/" + user.getUserID());
                    }
                });
        return settings;
    }

    /**
     * Creates a menu item for navigation menu
     *
     * @return Component MenuItem
     */
    private Component buildMenuItems() {
        CssLayout menuItemsLayout = new CssLayout();
        menuItemsLayout.addStyleName("valo-menuitems");
        Label notificationsBadge = new Label();
        notificationsBadge.setId("dashboard-menu-notifications-badge");

        // add User Menu Item
        Component homeItemComponent = ValoMenuItemButton("Home");
        homeItemComponent = buildBadgeWrapper(homeItemComponent,
                notificationsBadge);
        menuItemsLayout.addComponent(homeItemComponent);

        // add User Menu Item
        if (UserModel.validateAccessControl(UserModel.ROLE_ADMIN)) {
            Component userItemComponent = ValoMenuItemButton("Users");
            userItemComponent = buildBadgeWrapper(userItemComponent,
                    notificationsBadge);
            menuItemsLayout.addComponent(userItemComponent);
        }

        // add Reservation Menu Item
        Component reservationItemComponent = ValoMenuItemButton("Reservations");
        reservationItemComponent = buildBadgeWrapper(reservationItemComponent,
                notificationsBadge);
        menuItemsLayout.addComponent(reservationItemComponent);

        // add Gadget Menu Item
        if (UserModel.validateAccessControl(UserModel.ROLE_ADMIN)) {
            Component gadgetItemComponent = ValoMenuItemButton("Gadgets");
            gadgetItemComponent = buildBadgeWrapper(gadgetItemComponent,
                    notificationsBadge);
            menuItemsLayout.addComponent(gadgetItemComponent);
        }

        return menuItemsLayout;
    }

    /**
     * Creates the Button Component for Navigation
     *
     * @param view the view for the dashboard
     * @return Component Button with Caption & ClickEvent
     */
    private Component ValoMenuItemButton(String view) {
        Button menuButton = new Button();
        menuButton.setPrimaryStyleName(ValoTheme.MENU_ITEM);
        switch (view) {
            case "Home":
                menuButton.setIcon(VaadinIcons.HOME);
                menuButton.addClickListener(
                        event -> UI.getCurrent().getNavigator().navigateTo(RentalTool.HOME)
                );
                break;
            case "Users":
                menuButton.setIcon(VaadinIcons.USER);
                menuButton.addClickListener(
                        event -> UI.getCurrent().getNavigator().navigateTo(RentalTool.HOME + "/" + RentalTool.LIST_USERS)
                );
                break;
            case "Reservations":
                menuButton.setIcon(VaadinIcons.CALENDAR);
                menuButton.addClickListener(
                        event -> UI.getCurrent().getNavigator().navigateTo(RentalTool.HOME + "/" + RentalTool.CREATE_USER)
                );
                break;
            case "Gadgets":
                menuButton.setIcon(VaadinIcons.BRIEFCASE);
                menuButton.addClickListener(
                        event -> UI.getCurrent().getNavigator().navigateTo(RentalTool.HOME + "/" + RentalTool.LIST_USERS)
                );
                break;
        }
        menuButton.setCaption(view);

        return menuButton;
    }

    /**
     * Wraps the MenuButton with some Styling
     *
     * @param menuItemButton the created MenuButton
     * @param badgeLabel Label Component
     * @return Component - the wrapped Component
     */
    private Component buildBadgeWrapper(final Component menuItemButton,
                                        final Component badgeLabel) {
        CssLayout dashboardWrapper = new CssLayout(menuItemButton);
        dashboardWrapper.addStyleName("badgewrapper");
        dashboardWrapper.addStyleName(ValoTheme.MENU_ITEM);
        badgeLabel.addStyleName(ValoTheme.MENU_BADGE);
        badgeLabel.setWidthUndefined();
        badgeLabel.setVisible(false);
        dashboardWrapper.addComponent(badgeLabel);
        return dashboardWrapper;
    }
}
