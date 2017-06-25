package com.mortmann.andja.creator.util;

import javafx.scene.control.Tab;

public class NotClosableTab extends Tab {
	public NotClosableTab(){
		super();
		setClosable(false);
	}
	public NotClosableTab(String s){
		super(s);
		setClosable(false);
	}
}
