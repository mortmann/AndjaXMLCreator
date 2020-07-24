package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.MethodInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false)
public class Farm extends OutputStructure {
	
	@FieldInfo(order = 0 ,compareType=Growable.class , required = true) @Element(required=false) public String growable;

	@FieldInfo(order = 0, required = true, IsEffectable=true) @Element(required=false) public int neededHarvestToProduce = 1;
	
	public Farm(){
		structureTyp = StructureTyp.Blocking;
		buildTyp = BuildTypes.Single;
		hasHitbox = true;
		this.canTakeDamage = true;
	}
	
	@Override
	public Tabable OutputStructureDependsOnTabable(Tabable t) {
		if(t.GetID().equals(growable)&&t.getClass()==Growable.class){
			return this;
		}
		return null;
	}

	@Override
	public void OutputStructureUpdateDependables(Tabable t, String ID) {
		if(ID.equals(growable)&&t.getClass()==Growable.class){
			growable = t.GetID();
		}		
	}
	@Override
	public String GetButtonColor() {
		return "#3EB650";
	}
	
	
	@MethodInfo(Title = "Produce per Minute")
	public float CalculatePPM() {
		float tileCount = InRangeTiles();
		Growable grow = (Growable)GUI.Instance.idToStructures.get(growable);
        float neededWorkerRatio = maxNumberOfWorker / neededHarvestToProduce; 
		if(grow==null||produceTime * efficiency <= 0|| grow.produceTime<=0 || output==null || output.length==0)
			return 0;
		if(maxNumberOfWorker*produceTime*efficiency>=grow.produceTime)
			return neededWorkerRatio*(60f/produceTime);
		float ppm = Math.min(60f/(neededHarvestToProduce*produceTime*efficiency),((tileCount / (float)neededHarvestToProduce)*(60f/grow.produceTime)));
		return ppm / output[0].count;
	}
}
