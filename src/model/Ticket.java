package model;

public class Ticket {
    private int id;
    private int flightId;
    private int userId;
    private String seatType; // "ECONOMY" or "BUSINESS"
    private String seatNumber;
    private double price;

    public Ticket(int id, int flightId, int userId, String seatType, String seatNumber, double price) {
        this.id = id;
        this.flightId = flightId;
        this.userId = userId;
        this.seatType = seatType;
        this.seatNumber = seatNumber;
        this.price = price;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}