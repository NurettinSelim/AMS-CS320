package view;

import model.Flight;

import javax.accessibility.Accessible;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.util.List;

public interface IFlightListPanel extends ImageObserver, MenuContainer, Serializable, Accessible {
    void refreshFlights();

    void searchFlights();

    void updateTableModel(List<Flight> flights);

    void bookSelectedFlight();
}
