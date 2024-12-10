package model;

public class Manager extends User {
    public Manager(int id, String email, String password) {
        super(id, email, password, "manager");
    }
}
