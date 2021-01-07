package com.jfoenix.control.event;

import javafx.event.Event;
import javafx.event.EventType;

public class JFXDialogEvent extends Event {

    private static final long serialVersionUID = 1L;

    /**
     * Construct a new JFXDialog {@code Event} with the specified event type
     *
     * @param eventType the event type
     */
    public JFXDialogEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    /**
     * This event occurs when a JFXDialog is closed, no longer visible to the user
     * ( after the exit animation ends )
     */
    public static final EventType<JFXDialogEvent> CLOSED =
            new EventType<>(Event.ANY, "JFX_DIALOG_CLOSED");

    /**
     * This event occurs when a JFXDialog is opened, visible to the user
     * ( after the entrance animation ends )
     */
    public static final EventType<JFXDialogEvent> OPENED =
            new EventType<>(Event.ANY, "JFX_DIALOG_OPENED");


}
