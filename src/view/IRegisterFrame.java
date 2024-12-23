package view;

import javax.accessibility.Accessible;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.ImageObserver;
import java.io.Serializable;

public interface IRegisterFrame extends ImageObserver, MenuContainer, Serializable, Accessible, WindowConstants, RootPaneContainer {
    void toggleAdminMode();

    void handleRegister(ActionEvent e);

    void handleCancel();
}
