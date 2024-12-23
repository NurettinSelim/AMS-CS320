package view;

import model.Ticket;

import javax.accessibility.Accessible;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.util.List;

public interface IProfilePanel extends ImageObserver, MenuContainer, Serializable, Accessible {
    void refreshTickets();

    void updateTableModel(List<Ticket> tickets);

    void showChangePasswordDialog();

    void saveChanges();

    void cancelSelectedTicket();
}
