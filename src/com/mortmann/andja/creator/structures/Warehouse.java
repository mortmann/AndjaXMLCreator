package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.util.FieldInfo;

@Root(strict=false)
public class Warehouse extends Market {
	@FieldInfo(required = true, IsEffectable=true) @Element(required=false) public int tradeItemCount;

	public Warehouse(){
		contactRange = 6.3f;
		buildTyp = BuildTypes.Single;
		hasHitbox = true;
		canTakeDamage = true;
		canBeUpgraded=true;
		structureRange = 18;
	}
	
	@Override
	public String GetButtonColor() {
		return "#22a6b3";
	}
}
