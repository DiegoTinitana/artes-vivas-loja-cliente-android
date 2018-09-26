package com.diegotinitana.artesvivas.models;

public class Event {

    String title;
    String date;
    String start;
    String end;
    String id;
    String place;

    public Event(String title, String date, String start, String end, String id, String place) {
        this.title = title;
        this.date = date;
        this.start = start;
        this.end = end;
        this.id = id;
        this.place = place;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}

