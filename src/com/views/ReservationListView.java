package com.views;

import com.vaadin.navigator.View;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

public interface ReservationListView extends View {

    interface ReservationListViewListener {
        void userButton(Button.ClickEvent event);
        void filter(Grid grid);
    }
    public void addListener(ReservationListViewListener listener);
}