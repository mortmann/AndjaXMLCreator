package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.util.FieldInfo;

@Root(strict=false)
public class Market extends OutputStructure {
	@FieldInfo(required = true, IsEffectable=true) @Element public float takeOverStartGoal = 100;
	
	public Market(){
		hasHitbox = true;
		tileWidth = 4;
		tileHeight = 4;
		buildTyp = BuildTypes.Single;
		myStructureTyp = StructureTyp.Blocking;
		structureRange = 18;
		canTakeDamage = true;
		canBeUpgraded=true;
	}
}
