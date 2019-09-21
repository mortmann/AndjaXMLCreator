package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false)
public class Farm extends OutputStructure {
	
	@FieldInfo(order = 0 ,compareType=Growable.class , required = true) @Element(required=false) public String growable;

	@FieldInfo(order = 0, required = true, IsEffectable=true) @Element(required=false) public int neededHarvestToProduce = 5;
	
	public Farm(){
		myStructureTyp = StructureTyp.Blocking;
		buildTyp = BuildTypes.Single;
		hasHitbox = true;
		this.canTakeDamage = true;
	}
	
	public Tabable DependsOnTabable(Tabable t) {
		if(t.GetID()==growable&&t.getClass()==Growable.class){
			return this;
		}
		return OutputDependsOnTabable(t);
	}
}
