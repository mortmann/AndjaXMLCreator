package com.mortmann.andja.creator.util;

import java.util.ArrayDeque;

import com.mortmann.andja.creator.GUI;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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
				event.consume();
				return; 
		    }
		}
		if(event.getTarget()==null){
			return;//there is no target so no need to save event for reverse
		}		
		if(event instanceof KeyEvent){
			KeyCode key = ((KeyEvent)event).getCode();
			if(key == KeyCode.S&&((KeyEvent)event).isControlDown() || KeyEvent.KEY_PRESSED != ((KeyEvent)event).getEventType() ){
				return;
			}
			if(key==KeyCode.CONTROL||key==KeyCode.ALT||key==KeyCode.ALT_GRAPH){
				return;
			}
			GUI.Instance.changedCurrentTab();
		}

		if(event.getTarget() instanceof ComboBox || event.getTarget() instanceof CheckBox||  event.getTarget() instanceof Button){
			Parent curr = ((Node)event.getTarget()).getParent();
			Node root = GUI.Instance.getRoot(); 
			while(curr != root){
				if(curr == GUI.Instance.GetCurrentTab().getContent()){
					GUI.Instance.changedCurrentTab();
					break;
				}
				if(curr!=null){
					curr = curr.getParent();
				} else {
					//temp fix
					GUI.Instance.changedCurrentTab();
					break;
				}
			}
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
