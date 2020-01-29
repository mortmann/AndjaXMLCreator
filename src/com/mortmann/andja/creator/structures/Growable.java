package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.other.Fertility;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false)
public class Growable extends OutputStructure {
	@FieldInfo(order = 0,compareType=Fertility.class) @Element(required = false) public String fertility;

	@FieldInfo(required=true) @Element public int ageStages = 2;
	public Growable(){
		forMarketplace = false;
		canBeBuildOver = true;
		maxNumberOfWorker = 0;
		tileWidth = 1;
		tileHeight = 1;
		structureTyp = StructureTyp.Free;
		buildTyp = BuildTypes.Drag;
		maxOutputStorage = 1;
	}
	protected Tabable OutputStructureDependsOnTabable(Tabable t) {
		if(t.getClass() == Fertility.class && fertility.equals(t.GetID()))
			return this;
		return null;
	}
	@Override
	public void OutputStructureUpdateDependables(Tabable t, String ID) {
		if(t.getClass() == Fertility.class && fertility.equals(ID)) {
			fertility = t.GetID();
		}
	}
	@Override
	public String GetButtonColor() {
		return "#badc58";
	}
}
