package com.kotelking.event.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Application implements Serializable {

    private LocalDateTime appliedTime;

    private List<User> attendees;

    private int id;

    public LocalDateTime getAppliedTime() {
        return appliedTime;
    }

    public void setAppliedTime(LocalDateTime appliedTime) {
        this.appliedTime = appliedTime;
    }

    public List<User> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<User> attendees) {
        this.attendees = attendees;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static Application makeNew(int id, List<User> attendees){

        attendees.forEach(u->u.setId(id));

        Application newApplication =new Application();
        newApplication.setId(id);
        newApplication.setAttendees(attendees);
        newApplication.setAppliedTime(LocalDateTime.now());
        return newApplication;

    }
}
