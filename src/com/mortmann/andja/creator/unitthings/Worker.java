package com.mortmann.andja.creator.unitthings;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false)
public class Worker implements Tabable, Comparable<Tabable> {
	
	@FieldInfo(order=0,required=true,id=true) @Attribute(required=true) public String ID;
	@FieldInfo @Element(required=false) public String workSound;
	@FieldInfo @Element(required=false) public String toWorkSprites;
	@FieldInfo @Element(required=false) public String fromWorkSprites;
	@FieldInfo(Minimum = 32) @Element(required=false) public int pixelsPerSprite = 64;
	@FieldInfo(Minimum = 0.1f) @Element(required=false) public float speed = 1;
	@Element(required=false)  public boolean hasToFollowRoads = false;
	@Element(required=false) public boolean hasToEnterWork = false;

	@Override
	public int compareTo(Tabable o) {
		return GetID().compareTo(o.GetID());
	}
	@Override
	public String toString() {
		return GetName();
	}
	@Override
	public String GetName() {
		return ID != null? ID : getClass().getSimpleName();
	}
	@Override
	public String GetID() {
		return ID;
	}
	@Override
	public Tabable DependsOnTabable(Tabable t) {
		return null;
	}
	@Override
	public void UpdateDependables(Tabable t, String ID) {
		
	}
	@Override
	public String GetButtonColor() {
		return null;
	}
	
}
