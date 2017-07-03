package com.mortmann.andja.creator.util;

import java.util.ArrayDeque;

import com.mortmann.andja.creator.GUI;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class MyInputHandler implements EventHandler<Event> {

	ArrayDeque<Event> events;
	
	public MyInputHandler() {
		events = new ArrayDeque<>();
	}
	
	@Override
	public void handle(Event event) {
		if(event instanceof KeyEvent){
			if (((KeyEvent)event).getCode() == KeyCode.Z && ((KeyEvent)event).isControlDown()) { 
				ReverseEvent();
				//reverse
				//make redo
				return; //stop not save it as last 
		    }
			if (((KeyEvent)event).getCode() == KeyCode.S&& KeyEvent.KEY_PRESSED == ((KeyEvent)event).getEventType() && ((KeyEvent)event).isControlDown()) { 
				GUI.Instance.SaveCurrentTab();
				//reverse
				//make redo
				event.consume();
				return; //stop not save it as last 
		    }
		}
		if(event.getTarget()==null){
			return;//there is no target so no need to save event for reverse
		}
		if(event instanceof KeyEvent){
			if(((KeyEvent)event).getCode() == KeyCode.S&&((KeyEvent)event).isControlDown() || KeyEvent.KEY_PRESSED != ((KeyEvent)event).getEventType() ){
				return;
			}
			GUI.Instance.changedCurrentTab();
		}
		events.push(event);
	}

	private void ReverseEvent() {
		System.out.println("REVERSE");
		Event last = events.getLast();
		if(last instanceof KeyEvent){
//			KeyEvent k = (KeyEvent) last;
			EventTarget t = last.getTarget();
			if(t instanceof TextField){
				TextField tf = (TextField) t;
				tf.setText(tf.getText().substring(0, tf.getText().length()-1));
			}
			else if(t instanceof CheckBox){
				((CheckBox)t).setSelected(!((CheckBox)t).isSelected());
			}
			else if(t instanceof ComboBox){
				//idk
				
			}
		}
	}

}
