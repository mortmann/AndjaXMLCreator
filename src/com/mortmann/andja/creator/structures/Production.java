package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.*;

import com.mortmann.andja.creator.other.Item;

public class Production extends OutputStructure {
//	@ElementArray public int[] needIntake;
//	@ElementArray public int[] maxIntake;
	@ElementArray public Item[] intake;
}
