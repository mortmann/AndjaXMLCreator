package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;

import com.mortmann.andja.creator.other.Effect;
import com.mortmann.andja.creator.util.FieldInfo;

public class ServiceStructure extends Structure {
	public enum ServiceTarget { All, Damageable, Military, Homes, Production, Service, NeedStructure, SpecificRange, City, None }
	public enum ServiceFunction { None, Repair, AddEffect, RemoveEffect, PreventEffect }

	@Element public ServiceTarget targets = ServiceTarget.All;
    @Element public ServiceFunction function;
    @FieldInfo(required=false,compareType=Structure[].class) 
	@ElementArray(entry="Structure",required=false) 
    public int[] specificRange = null;
    @FieldInfo(required=false,compareType=Effect[].class) 
	@ElementArray(entry="Effect",required=false) 
	public int[] effectsOnTargets;
    @Element public int maxNumberOfWorker = 1;
    @Element public float workSpeed = 0.01f;
	
	
}
