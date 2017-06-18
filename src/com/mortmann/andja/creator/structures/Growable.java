package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;

import com.mortmann.andja.creator.other.Fertility;

public class Growable extends Structure {
	@Element public float growTime = 5f;
	@Element public Fertility fer;
	@Element public int ageStages = 2;
}
