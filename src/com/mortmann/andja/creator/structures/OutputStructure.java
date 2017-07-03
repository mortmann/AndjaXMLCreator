package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.*;

import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.util.FieldInfo;

public abstract class OutputStructure extends Structure {
	@Element public float contactRange=0;
	@Element public boolean forMarketplace=true;
	@Element public int maxNumberOfWorker = 1;
	@FieldInfo(required=true) @Element public float produceTime =0;
	@Element(required=false) public int maxOutputStorage;
	@ElementArray(entry="Item",required=false) public Item[] output;
	@Override
	public int GetID() {
		return ID;
	}
}
