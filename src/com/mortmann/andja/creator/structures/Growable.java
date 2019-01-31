package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.other.Fertility;
import com.mortmann.andja.creator.util.FieldInfo;

@Root(strict=false)
public class Growable extends OutputStructure {
	@FieldInfo(order = 0,type=Fertility.class) @Element public int fertility=-1;
	@FieldInfo(required=true) @Element public int ageStages = 2;
	public Growable(){
		forMarketplace = false;
		canBeBuildOver = true;
		maxNumberOfWorker = 0;
		tileWidth = 1;
		tileHeight = 1;
		myStructureTyp = StructureTyp.Free;
		buildTyp = BuildTypes.Drag;
		maxOutputStorage = 1;
	}
}
