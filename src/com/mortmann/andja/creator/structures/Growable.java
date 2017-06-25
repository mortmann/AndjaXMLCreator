package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;

import com.mortmann.andja.creator.other.Fertility;

public class Growable extends OutputStructure {
	@Element public float growTime = 5f;
	@Element public Fertility fer;
	@Element public int ageStages = 2;
	public Growable(){
		forMarketplace = false;
		maxNumberOfWorker = 0;
		tileWidth = 1;
		tileHeight = 1;
		myBuildingTyp = BuildingTyp.Free;
		BuildTyp = BuildTypes.Drag;
		maxOutputStorage = 1;
	}
}
