package com.gmail.arduino;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * Created by rayanral on 15/03/15.
 */
public class InputEvent extends Event {

    private Float value;

    public InputEvent(Float value) {
        super(EventType.ROOT);
        this.value = value;
    }

    public Float getValue() {
        return value;
    }
}
