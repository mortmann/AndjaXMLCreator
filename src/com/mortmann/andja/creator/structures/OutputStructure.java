package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.*;

import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.other.ItemXML;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false)
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
	protected Tabable OutputDependsOnTabable(Tabable t) {
		if(t.getClass()==ItemXML.class){
			for (Item item : output) {
				if(item.ID==t.GetID()){
					return this;
				}
			}
		}
		return StructureDependsOnTabable(t);
	}
}
