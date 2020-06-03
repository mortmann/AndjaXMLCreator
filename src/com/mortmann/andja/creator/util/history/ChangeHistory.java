package com.mortmann.andja.creator.util.history;

import java.lang.reflect.Field;
import java.util.HashMap;

import com.mortmann.andja.creator.GUI;

public class ChangeHistory {
	static Object CurrentTab;
	static HashMap<Object, ObjectHistory> changes = new HashMap<Object, ObjectHistory>(); 
	public ChangeHistory() {
		changes = new HashMap<Object, ObjectHistory>();
	}
	public static void AddObject(Object object) {
		ObjectHistory oh = new ObjectHistory(object);
		changes.put(object , oh);
	}
	public static void AddChange(Changeable changeable, Object change, Object old) {
		Change last = new Change(changeable,change, old);
		changes.get(CurrentTab).AddChange(last);
		GUI.Instance.changedCurrentTab(changes.get(CurrentTab).SavedChanges());
	}
	public static void Undo() {
		changes.get(CurrentTab).Undo();
		GUI.Instance.changedCurrentTab(changes.get(CurrentTab).SavedChanges());
	}
	public static void Do() {
		changes.get(CurrentTab).Do();
		GUI.Instance.changedCurrentTab(changes.get(CurrentTab).SavedChanges());
	}
	public static void ObjectsSaved(Iterable<Object> savedObjects) {
		for (Object object : savedObjects) {
			changes.get(object).Saved();
		}
	}
	public static boolean RemoveObject(Object object, boolean forceDelete) {
		if(changes.get(object).SavedChanges() && forceDelete == false)
			return false;
		changes.remove(object);
		return true;
	}
	public static boolean IsSaved(Object object) {
		return changes.get(object).SavedChanges();
	}
	public static void ObjectSaved(Object object) {
		changes.get(object).Saved();
	}
	public static void CustomTextFix(Object input, String text) {
		changes.get(CurrentTab).CustomTextFix(input, text);
		GUI.Instance.changedCurrentTab(changes.get(CurrentTab).SavedChanges());
	}
	public static void SetCurrentTab(Object newTab) {
		CurrentTab = newTab;
	}
	public static void AddToLastChange(Field field, Object tabable, Object newV, Object oldV) {
		changes.get(CurrentTab).AddToLastChange(field, tabable, newV, oldV);
	}
	
}
