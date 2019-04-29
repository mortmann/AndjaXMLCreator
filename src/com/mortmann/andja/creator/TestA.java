package com.mortmann.andja.creator;

import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;

import com.mortmann.andja.creator.ui.UIElement;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;


@Root(strict=false)
public class TestA {
	@ElementList(required=false) public ArrayList<TestA> childs; 
	@Transient public TestA parent;
	@Transient protected TitledPane title;
	@Transient protected GridPane gridpane;
	public TestA() {
		title = new TitledPane();
		gridpane = new GridPane();
		childs = new ArrayList<>();
		childs.add(new TestA("a",this));
		childs.add(new TestA("a"));
		childs.add(new TestA("a"));
		childs.add(new TestA("a"));
		childs.add(new TestB("a",this));

	}
	public TestA(String string) {
	}
	public TestA(String string, TestA testA) {
		parent = testA;
	}
}
