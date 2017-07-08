package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.other.ItemXML;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false)
public class Farm extends OutputStructure {
	
	@FieldInfo(order = 0,type=Growable.class) @Element(required=false) public int growable;
	
	public Farm(){
		myBuildingTyp = BuildingTyp.Blocking;
		BuildTyp = BuildTypes.Single;
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
