package com.github.eventlogprocessor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Event {

    @JsonProperty("id")
    private String id;

    @JsonProperty("state")
    private State state;

    @JsonProperty("type")
    private String type;

    @JsonProperty("host")
    private String host;

    @JsonProperty("timestamp")
    private long timestamp;
}
