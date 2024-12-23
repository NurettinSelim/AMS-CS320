package view;

import javax.accessibility.Accessible;
import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.Serializable;

public interface IHintTextField extends ImageObserver, MenuContainer, Serializable, Scrollable, Accessible, SwingConstants {
    String getText();
}
