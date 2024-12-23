package view;

import model.Flight;

import javax.accessibility.Accessible;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.util.List;

public interface IManageFlightsPanel extends ImageObserver, MenuContainer, Serializable, Accessible {
    void refreshFlights();

    void updateTableModel(List<Flight> flights);

    void showAddFlightDialog();

    void editSelectedFlight();

    void showEditFlightDialog(Flight flight);

    void deleteSelectedFlight();
}
