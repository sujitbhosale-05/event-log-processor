package com.github.eventlogprocessor.model.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "Alerts")
public class Alert {

    @Id
    private String eventId;

    @Column
    private long eventDuration;

    @Column
    private Boolean isAlert;

    @Column
    private String eventType;

    @Column
    private String host;
}
