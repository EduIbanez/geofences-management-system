package es.unizar.iaaa.geofencing.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import es.unizar.iaaa.geofencing.view.View;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="NOTIFICATIONS")
public class Notification {

    private Long id;
    private String sender;
    private String receiver;
    private String status;
    private String title;
    private String body;

    public Notification(){}

    public Notification(@JsonProperty("id") Long id, @JsonProperty("sender") String sender,
                        @JsonProperty("receiver") String receiver, @JsonProperty("status") String status,
                        @JsonProperty("title") String title, @JsonProperty("body") String body) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        this.title = title;
        this.body = body;
    }

    @Id
    @GeneratedValue
    @Column(name = "ID", unique = true, nullable = false)
    @JsonView(View.NotificationBaseView.class)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "SENDER", nullable = false, length = 30)
    @JsonView(View.NotificationBaseView.class)
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Column(name = "RECEIVER", nullable = false, length = 30)
    @JsonView(View.NotificationCompleteView.class)
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Column(name = "STATUS", nullable = false, length = 15)
    @JsonView(View.NotificationBaseView.class)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "TITLE", nullable = false, length = 30)
    @JsonView(View.NotificationCompleteView.class)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "BODY", nullable = false, length = 150)
    @JsonView(View.NotificationCompleteView.class)
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String toString() {
        return "Notification(id: "+id+" sender: "+sender+" receiver: "+receiver+" status: "+status+
                " title: "+title+" body: "+body+")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification user = (Notification) o;
        return id == user.id &&
                Objects.equals(sender, user.sender) &&
                Objects.equals(receiver, user.receiver) &&
                Objects.equals(status, user.status) &&
                Objects.equals(title, user.title) &&
                Objects.equals(body, user.body);
    }
}