package com.mortmann.andja.creator.util.history;

import java.lang.reflect.Field;
import java.util.LinkedList;


public class ObjectHistory {
	public ObjectHistory(Object object) {
		this.object = object;
	}
	public LinkedList<Change> changes = new LinkedList<Change>();
	public int currentIndex = -1;
	public Object object;
	
	boolean isChanged = true;
	public int lastSavedIndex = -1;
	public boolean SavedChanges() {
		return currentIndex == lastSavedIndex;
	}
	public void AddChange(Change last) {
		//current is not the last one in the Changes -- So erase already undone changes
		if(currentIndex < changes.size() - 1) {
			for (int i = changes.size()-1; i > currentIndex; i--) {
				changes.remove(i);
			}
		}
		changes.addLast(last);
		if(changes.size()>10000)
			changes.removeFirst();
		currentIndex = changes.size() - 1;
		isChanged = false;
	}
	public void Undo() {
		if(currentIndex < 0)
			return;
		changes.get(currentIndex).Undo();
		currentIndex--;
		isChanged = lastSavedIndex == currentIndex;
	}
	public void Do() {
		if(currentIndex >= changes.size() - 1)
			return;
		currentIndex++;
		changes.get(currentIndex).Do();
		
		isChanged = lastSavedIndex == currentIndex;
	}
	public void Saved() {
		lastSavedIndex = currentIndex;
	}
	public void CustomTextFix(Object input, String text) {
		changes.removeIf((Change x)->CustomTextFixCheck(x,input,text));
		currentIndex = changes.size() - 1;
	}
	private boolean CustomTextFixCheck(Change test, Object input, String text) {
		if(test.changable == input) {
			if(text.contains((String)test.change)){
				return true;
			}
			if(((String)test.change).contains(text)){
				return true;
			}
		}
		return false;
	}
	public void AddToLastChange(Field field, Object tabable, Object newV, Object oldV) {
		changes.getLast().Add(field, tabable, newV, oldV);
	}
}
