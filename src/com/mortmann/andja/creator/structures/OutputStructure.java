package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.*;

import com.mortmann.andja.creator.other.Item;

public abstract class OutputStructure extends Structure {
	@Element public float contactRange=0;
	@Element public boolean forMarketplace=true;
	@Element public int maxNumberOfWorker = 1;
	@Element public float produceTime;
	@Element(required=false) public int maxOutputStorage;
	@ElementArray public Item[] output;
}
