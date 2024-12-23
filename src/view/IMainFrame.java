package view;

import javax.accessibility.Accessible;
import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.Serializable;

public interface IMainFrame extends ImageObserver, MenuContainer, Serializable, Accessible, WindowConstants, RootPaneContainer {
    JPanel createSidebarPanel();

    void addNavigationButton(JPanel panel, String text, String cardName);

    void handleLogout();
}
