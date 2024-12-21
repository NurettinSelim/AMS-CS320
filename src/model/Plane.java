package model;

public class Plane {
    private int id;
    private String planeName;
    private int capacity;

    public Plane(int id, String planeName, int capacity) {
        this.id = id;
        this.planeName = planeName;
        this.capacity = capacity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaneName() {
        return planeName;
    }

    public void setPlaneName(String planeName) {
        this.planeName = planeName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isEmpty() {
        return id <= 0 || planeName == null || planeName.isEmpty() || capacity <= 0;
    }
}
