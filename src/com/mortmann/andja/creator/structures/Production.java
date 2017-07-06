package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.*;

import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.util.FieldInfo;

@Root(strict=false)
public class Production extends OutputStructure {
//	@ElementArray public int[] needIntake;
//	@ElementArray public int[] maxIntake;
	@FieldInfo(required=true)@ElementArray(entry="Item") public Item[] intake;
	
	public Production(){
		maxOutputStorage = 5; // hardcoded 5 ? need this to change?
		hasHitbox = true;
		myBuildingTyp = BuildingTyp.Blocking;
		BuildTyp = BuildTypes.Single;
		canTakeDamage = true;
		maxNumberOfWorker = 1;
	}
	
	
}
