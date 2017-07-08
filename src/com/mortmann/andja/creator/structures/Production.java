package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.*;

import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.other.ItemXML;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false)
public class Production extends OutputStructure {
	@FieldInfo(required=true)@ElementArray(entry="Item") public Item[] intake;
	
	public Production(){
		maxOutputStorage = 5; // hardcoded 5 ? need this to change?
		hasHitbox = true;
		myBuildingTyp = BuildingTyp.Blocking;
		BuildTyp = BuildTypes.Single;
		canTakeDamage = true;
		maxNumberOfWorker = 1;
	}
	
	protected Tabable StructureDependsOnTabable(Tabable t) {
		if(t.getClass()==ItemXML.class){
			for (Item item : intake) {
				if(item.ID==t.GetID()){
					return this;
				}
			}
		}
		return OutputDependsOnTabable(t);
	}
	@Override
	public Tabable DependsOnTabable(Tabable t) {
		return StructureDependsOnTabable(t);
	}
}
