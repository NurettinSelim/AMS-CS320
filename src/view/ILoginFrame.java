package view;

import model.User;

import javax.accessibility.Accessible;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.ImageObserver;
import java.io.Serializable;

public interface ILoginFrame extends ImageObserver, MenuContainer, Serializable, Accessible, WindowConstants, RootPaneContainer {
    void handleLogin(ActionEvent e);

    void handleRegister(ActionEvent e);

    void openMainFrame(User user);
}
