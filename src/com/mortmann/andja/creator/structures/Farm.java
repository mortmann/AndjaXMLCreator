package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.util.FieldInfo;

@Root(strict=false)
public class Farm extends OutputStructure {
	
	@FieldInfo(order = 0,type=Growable.class) @Element(required=false) public int growable;
	
	public Farm(){
		myBuildingTyp = BuildingTyp.Blocking;
		BuildTyp = BuildTypes.Single;
		hasHitbox = true;
		this.canTakeDamage = true;
	}
}
