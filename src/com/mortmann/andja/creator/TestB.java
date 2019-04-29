package com.mortmann.andja.creator;

import org.simpleframework.xml.Element;


public class TestB extends TestA {
	@Element(required=false) public String text;
	@Element(required=false) public String hoverOver;

	public TestB(String name, TestA parent) {
		super(name,parent);
	}
}
