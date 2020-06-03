package com.mortmann.andja.creator.util.history;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Change {
	public Change(Changeable changable, Object change, Object old) {
		this.changable = changable;
		this.change = change;
		this.old = old;
	}
	Changeable changable;
	Object change;
	Object old;
	ArrayList<SubChange> added;
	public void Undo() {
		if(added!=null) {
			for(SubChange c : added)
				c.Undo();
		}
		changable.Undo(change);
	}
	public void Do() {
		if(added!=null) {
			for(SubChange c : added)
				c.Do();
		}
		changable.Do(old);
	}
	public void Add(Field f, Object tabable, Object change, Object oldV) {
		if(added==null)
			added = new ArrayList<>();
		added.add(new SubChange(f, tabable, change, oldV));
	}
	class SubChange {
		Field field;
		Object change;
		Object old;
		Object tabable;
		public SubChange(Field f, Object tabable, Object change, Object oldV) {
			field = f;
			this.change = change;
			old = oldV;
			this.tabable = tabable;
		}
		public void Undo() {
			try {
				field.set(tabable, old);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		public void Do() {
			try {
				field.set(tabable, change);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}