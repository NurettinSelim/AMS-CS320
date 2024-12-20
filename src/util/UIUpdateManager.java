package util;

import java.util.ArrayList;
import java.util.List;

public class UIUpdateManager {
    private static UIUpdateManager instance;
    private final List<UIUpdateObserver> observers;

    public static final String FLIGHT_UPDATE = "FLIGHT_UPDATE";
    public static final String PLANE_UPDATE = "PLANE_UPDATE";
    public static final String PASSENGER_UPDATE = "PASSENGER_UPDATE";
    public static final String PROFILE_UPDATE = "PROFILE_UPDATE";
    public static final String TICKET_UPDATE = "TICKET_UPDATE";

    private UIUpdateManager() {
        observers = new ArrayList<>();
    }

    public static synchronized UIUpdateManager getInstance() {
        if (instance == null) {
            instance = new UIUpdateManager();
        }
        return instance;
    }

    public void addObserver(UIUpdateObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(UIUpdateObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String updateType) {
        for (UIUpdateObserver observer : observers) {
            observer.onUIUpdate(updateType);
        }
    }
} 