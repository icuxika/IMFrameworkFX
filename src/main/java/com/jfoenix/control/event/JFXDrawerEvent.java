package com.jfoenix.control.event;

import javafx.event.Event;
import javafx.event.EventType;

public class JFXDrawerEvent extends Event {

    private static final long serialVersionUID = 1L;

    /**
     * Construct a new JFXDrawer {@code Event} with the specified event type
     *
     * @param eventType the event type
     */
    public JFXDrawerEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    /**
     * This event occurs when a JFXDrawer is closed, no longer visible to the user
     * ( after the exit animation ends )
     */
    public static final EventType<JFXDrawerEvent> CLOSED =
            new EventType<>(Event.ANY, "JFX_DRAWER_CLOSED");

    /**
     * This event occurs when a JFXDrawer is drawn, visible to the user
     * ( after the entrance animation ends )
     */
    public static final EventType<JFXDrawerEvent> OPENED =
            new EventType<>(Event.ANY, "JFX_DRAWER_OPENED");

    /**
     * This event occurs when a JFXDrawer is being drawn, visible to the user
     * ( after the entrance animation ends )
     */
    public static final EventType<JFXDrawerEvent> OPENING =
            new EventType<>(Event.ANY, "JFX_DRAWER_OPENING");


    /**
     * This event occurs when a JFXDrawer is being closed, will become invisible to the user
     * at the end of the animation
     * ( after the entrance animation ends )
     */
    public static final EventType<JFXDrawerEvent> CLOSING =
            new EventType<>(Event.ANY, "JFX_DRAWER_CLOSING");


}
