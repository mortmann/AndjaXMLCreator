package com.mortmann.andja.creator.util.history;


public interface Changeable {
	public void Do(Object change);
	public void Undo(Object change);
	/**
	 * DO NOT FORGET -- GUI.Instance.UpdateCurrentTab();
	 * Update the current tab so stuff can redraw
	 * @param change
	 * @param old
	 */
	public void OnChange(Object change, Object old);
	/**
	 * Object - can be anything.
	 * Do not trust anything.
	 * Ever.
	 * Be sure what you will do.
	 * Because no one else is.
	 * @param changeListener
	 * @param first => first being executed
	 */
	public void AddChangeListener(ChangeListenerHistory changeListener, boolean first);
}
