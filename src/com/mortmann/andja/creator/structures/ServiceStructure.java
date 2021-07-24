package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.other.Effect;
import com.mortmann.andja.creator.unitthings.Worker;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;
@Root(strict=false,name="servicestructure")
public class ServiceStructure extends Structure {
	public enum ServiceTarget { All, Damageable, Military, Homes, Production, Service, NeedStructure, SpecificRange, City, None }
	public enum ServiceFunction { None, Repair, AddEffect, RemoveEffect, PreventEffect }

	@Element public ServiceTarget targets = ServiceTarget.All;
    @Element public ServiceFunction function;
    @FieldInfo(required=false,compareType=Structure[].class) 
	@ElementArray(entry="Structure",required=false) 
    public String[] specificRange = null;
    @FieldInfo(required=false,compareType=Effect[].class) 
	@ElementArray(entry="Effect",required=false) 
	public String[] effectsOnTargets;
    @Element public int maxNumberOfWorker = 1;
    @Element public float workSpeed = 0.01f;
	@FieldInfo(order = 0, compareType=Worker.class) @Element(required=false) public String workerID;

    public ServiceStructure() {
		hasHitbox = true;
	}
    @Override
	public Tabable StructureDependsOnTabable(Tabable t) {
		if(t.getClass().isAssignableFrom(Structure.class)&& specificRange!=null){
			for(int i = 0; i < specificRange.length;i++ ) {
				if(t.GetID().equals(specificRange[i])){
					return this;
				}
			}
		}
		if(t.getClass() == Effect.class&& effectsOnTargets!=null){
			for(int i = 0; i < effectsOnTargets.length;i++ ) {
				if(t.GetID().equals(effectsOnTargets[i])){
					return this;
				}
			}
		}
		return null;
	}
    @Override
    public void StructureUpdateDependables(Tabable t, String ID) {
    	if(t.getClass().isAssignableFrom(Structure.class)&& specificRange!=null){
			for(int i = 0; i < specificRange.length;i++ ) {
				if(ID.equals(specificRange[i])){
					specificRange[i] = t.GetID();
				}
			}
		}
		if(t.getClass() == Effect.class&& effectsOnTargets!=null){
			for(int i = 0; i < effectsOnTargets.length;i++ ) {
				if(ID.equals(effectsOnTargets[i])){
					effectsOnTargets[i] = t.GetID();
				}
			}
		}
	}
	@Override
	public String GetButtonColor() {
		return "#c7ecee";
	}
	
}
