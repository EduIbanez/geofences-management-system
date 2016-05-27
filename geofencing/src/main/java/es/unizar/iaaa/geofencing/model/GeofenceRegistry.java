package es.unizar.iaaa.geofencing.model;

import java.sql.Time;
import java.util.Set;

public class GeofenceRegistry {

    private Set<Notification> notifications;
    private Time time;
    private Integer seconds;

    public GeofenceRegistry(Set<Notification> notifications, Time time, Integer seconds) {
        this.notifications = notifications;
        this.time = time;
        this.seconds = seconds;
    }

    public Set<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(Set<Notification> notifications) {
        this.notifications = notifications;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }
}
