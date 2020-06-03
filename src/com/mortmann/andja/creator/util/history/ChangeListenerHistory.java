package com.mortmann.andja.creator.util.history;

@FunctionalInterface
public interface ChangeListenerHistory {
	public abstract void changed(Object old, Object changed, boolean newChange);
}
