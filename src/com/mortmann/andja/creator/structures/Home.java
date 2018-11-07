package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.util.FieldInfo;

@Root(strict=false)
public class Home extends Structure {
	@FieldInfo(required=true) @Element public int maxLivingSpaces;
	@FieldInfo(required=true) @Element public float increaseTime;
	@FieldInfo(required=true) @Element public float decreaseTime;
	public Home(){
		tileWidth = 3;
		tileHeight = 3;
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
