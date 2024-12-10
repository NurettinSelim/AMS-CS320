package model;

public class Passenger extends User {
    private String name;
    private String surname;

    public Passenger(int id, String email, String password, String name, String surname) {
        super(id, email, password, "passenger");
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
