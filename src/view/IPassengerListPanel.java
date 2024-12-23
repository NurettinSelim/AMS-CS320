package view;

import model.Flight;
import model.Ticket;
import model.User;

import javax.accessibility.Accessible;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.util.List;

public interface IPassengerListPanel extends ImageObserver, MenuContainer, Serializable, Accessible {
    void refreshPassengers();

    void searchPassengers();

    void updateUserTableModel(List<User> users);

    void loadSelectedPassengerTickets();

    void updateTicketTableModel(List<Ticket> tickets, List<Flight> flights);

    void cancelSelectedTicket();

    void deleteSelectedPassenger();
}
