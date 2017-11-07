package com.views;

import com.models.UserModel;
import com.vaadin.navigator.View;

public interface UserFormView extends View {

    public void setErrorMsg (String value);

    public void setSuccessMsg (String value);

    interface UserViewListener {
        void buttonClick(
            UserModel userModel
        );
    }
    public void addListener (UserFormView.UserViewListener listener);
}
