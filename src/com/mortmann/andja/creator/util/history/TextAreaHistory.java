package com.mortmann.andja.creator.util.history;

import java.util.ArrayList;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.ui.UITab;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class TextAreaHistory extends TextArea implements Changeable {
	boolean ignoreChange = false;
    final TextArea myTextArea = this;
	public TextAreaHistory(String content) {
		super(content);
		ignoreChange = true;
		Setup();
	}
	
	public TextAreaHistory() {
		Setup();
	}

	private void Setup() {
		ignoreChange = true;
		textProperty().addListener(new ChangeListener<String>() {
	        public void changed(ObservableValue<? extends String> ov,
	        		String old_val, String new_val) {
	        	OnChange(old_val, new_val);
	        }
        });
		addEventFilter(KeyEvent.ANY, e -> {
            if ((e.getCode() == KeyCode.Z || e.getCode() == KeyCode.Y) && e.isShortcutDown()) {
        		ignoreChange = true;
            }
            if(e.getCode() == KeyCode.TAB && e.isAltDown() == false)
            	e.consume();
        });	
		addEventFilter(KeyEvent.KEY_RELEASED, new TabAndEnterHandler());
		
	}

	@Override
	public void Do(Object change) {
		ignoreChange = true;
//		setText((String)change);
		if(isFocused()==false)
			redo();
		if (changeListeners != null) {
			for (ChangeListenerHistory c : changeListeners) {
				c.changed(null, change, false);
			}
		}
	}

	@Override
	public void Undo(Object change) {
		ignoreChange = true;
//		setText((String)change);
		if(isFocused()==false)
			undo();
		if (changeListeners != null) {
			for (ChangeListenerHistory c : changeListeners) {
				c.changed(null, change, false);
			}
		}
	}
	public void setStartText(String value) {
		ignoreChange = true;
		setText(value);
		
	}

	@Override
	public void OnChange(Object change, Object old) {
		if(ignoreChange) {
			ignoreChange = false;
			return;
		}
		ChangeHistory.AddChange(this, change, old); 
		if (changeListeners != null) {
			for (ChangeListenerHistory c : changeListeners) {
				c.changed(old, change, true);
			}
		}
		GUI.Instance.UpdateCurrentTab();
	}
	ArrayList<ChangeListenerHistory> changeListeners;
	@Override
	public void AddChangeListener(ChangeListenerHistory changeListener, boolean first) {
		if(changeListeners==null)
			changeListeners = new ArrayList<ChangeListenerHistory>();
		if(first)
			changeListeners.add(0,changeListener);
		else
			changeListeners.add(changeListener);
	}

	public void setIgnoreFlag() {
		ignoreChange = true;
	}

	class TabAndEnterHandler implements EventHandler<KeyEvent> {
        private KeyEvent recodedEvent;

        @Override public void handle(KeyEvent event) {
          if (recodedEvent != null) {
            recodedEvent = null;
            return;
          }
      	  Tab t = GUI.Instance.GetCurrentTab();
          Parent parent = myTextArea.getParent();
          if (parent != null) {
            switch (event.getCode()) {
              case TAB:
  				event.consume();
  				if(t instanceof UITab) {
  					((UITab)t).onTabInput();
  				}
                event.consume();
                break;
			default:
				break;
            }
          }  
        }
      }
}
