package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;

import com.mortmann.andja.creator.util.FieldInfo;

public class Home extends Structure {
	@FieldInfo(required=true) @Element public int maxLivingSpaces;
	@FieldInfo(required=true) @Element public float increaseSpeed;
	@FieldInfo(required=true) @Element public float decreaseSpeed;
	public Home(){
		tileWidth = 2;
		tileHeight = 2;
		BuildTyp = BuildTypes.Drag;
		myBuildingTyp =	BuildingTyp.Blocking;
		buildingRange = 0;
		hasHitbox = true;
		canTakeDamage = true;
		maintenancecost = 0;
	}
	@Override
	public int GetID() {
		return ID;
	}
}
