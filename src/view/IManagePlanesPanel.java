package view;

import model.Plane;

import javax.accessibility.Accessible;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.util.List;

public interface IManagePlanesPanel extends ImageObserver, MenuContainer, Serializable, Accessible {
    void refreshPlanes();

    void updateTableModel(List<Plane> planes);

    void showAddPlaneDialog();

    void editSelectedPlane();

    void deleteSelectedPlane();

    void loadPlanes();
}
