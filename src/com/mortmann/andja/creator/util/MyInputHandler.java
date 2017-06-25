package com.mortmann.andja.creator.util;

import java.util.ArrayDeque;

import com.mortmann.andja.creator.GUI;

import javafx.event.Event;
import javafx.event.EventHandler;
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

}
