package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.util.FieldInfo;

@Root(strict=false)
public class Home extends Structure {
	@FieldInfo(required=true) @Element public int maxLivingSpaces;
	@FieldInfo(required=true, IsEffectable=true) @Element public float increaseTime;
	@FieldInfo(required=true, IsEffectable=true) @Element public float decreaseTime;
	public Home(){
		tileWidth = 3;
		tileHeight = 3;
		buildTyp = BuildTypes.Drag;
		myStructureTyp =	StructureTyp.Blocking;
		structureRange = 0;
		hasHitbox = true;
		canTakeDamage = true;
		maintenanceCost = 0;
	}
	@Override
	public String GetButtonColor() {
		return "#f6e58d";
	}
}
