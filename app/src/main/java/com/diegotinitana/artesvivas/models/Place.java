package com.diegotinitana.artesvivas.models;

public class Place {

    String address;
    String capacity;
    String name;
    String id;

    public Place(String address, String capacity, String name, String id) {
        this.address = address;
        this.capacity = capacity;
        this.name = name;
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
