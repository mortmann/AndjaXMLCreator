package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict=false)
public class Mine extends OutputStructure{
	@Element public String myRessource;
	
	public Mine(){
		tileWidth = 2;
		tileHeight = 3;
		myStructureTyp = StructureTyp.Blocking;
		buildTyp = BuildTypes.Single;
		hasHitbox = true;
		structureRange = 0;
	}
	
}
