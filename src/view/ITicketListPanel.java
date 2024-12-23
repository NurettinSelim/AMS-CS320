package view;

import model.Flight;
import model.Ticket;
import model.User;

import javax.accessibility.Accessible;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.util.List;

public interface ITicketListPanel extends ImageObserver, MenuContainer, Serializable, Accessible {
    void refreshTickets();

    void searchTickets();

    void updateTableModel(java.util.List<Ticket> tickets, java.util.List<Flight> flights, List<User> users);

    void cancelSelectedTicket();
}
