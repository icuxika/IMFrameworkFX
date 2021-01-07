package com.jfoenix.control.event;

import javafx.event.Event;
import javafx.event.EventType;

public class JFXAutoCompleteEvent<T> extends Event {

	private T object;

	public JFXAutoCompleteEvent(EventType<? extends Event> eventType, T object) {
		super(eventType);
		this.object = object;
	}

	public T getObject() {
		return object;
	}

	//TODO: more events to be added
	public static final EventType<JFXAutoCompleteEvent> SELECTION =
			new EventType<JFXAutoCompleteEvent>(Event.ANY, "JFX_AUTOCOMPLETE_SELECTION");
}
