package com.mortmann.andja.creator.util;


import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.util.history.ChangeHistory;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class MyInputHandler implements EventHandler<Event> {
	
	public MyInputHandler() {
		
	}
	
	@Override
	public void handle(Event event) {
		if(event instanceof KeyEvent){
			if (((KeyEvent)event).getCode() == KeyCode.Z && ((KeyEvent)event).isControlDown()) { 
				ChangeHistory.Undo();
				return; 
		    }
			if (((KeyEvent)event).getCode() == KeyCode.Y && ((KeyEvent)event).isControlDown()) { 
				ChangeHistory.Do();
				return; 
		    }
			if (((KeyEvent)event).getCode() == KeyCode.S&& KeyEvent.KEY_PRESSED == ((KeyEvent)event).getEventType() && ((KeyEvent)event).isControlDown()) { 
				GUI.Instance.SaveCurrentTab();
				event.consume();
				return; 
		    }
		}
		if(event.getTarget()==null){
			return;//there is no target so no need to save event for reverse
		}		
//		if(event instanceof KeyEvent){
//			KeyCode key = ((KeyEvent)event).getCode();
//			if(key == KeyCode.S&&((KeyEvent)event).isControlDown() || KeyEvent.KEY_PRESSED != ((KeyEvent)event).getEventType() ){
//				return;
//			}
//			if(key==KeyCode.CONTROL||key==KeyCode.ALT||key==KeyCode.ALT_GRAPH){
//				return;
//			}
//			GUI.Instance.changedCurrentTab();
//		}

		
	
	}


}
