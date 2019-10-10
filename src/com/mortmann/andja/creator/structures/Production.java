package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.*;

import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.other.ItemXML;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false)
public class Production extends OutputStructure {
	public enum InputTyp { AND, OR };
	
	@FieldInfo(required=false)@ElementArray(entry="Item",required=false) public Item[] intake;
	@FieldInfo(required=false)@Element(required=false) public InputTyp myInputTyp;
	
	public Production(){
		maxOutputStorage = 5; // hardcoded 5 ? need this to change?
		hasHitbox = true;
		myStructureTyp = StructureTyp.Blocking;
		buildTyp = BuildTypes.Single;
		myInputTyp = InputTyp.AND;
		canTakeDamage = true;
		maxNumberOfWorker = 1;
	}
	@Override
	protected Tabable OutputStructureDependsOnTabable(Tabable t) {
		if(t.getClass()==ItemXML.class&& intake!=null){
			for (Item item : intake) {
				if(item.GetID().equals(t.GetID())){
					return this;
				}
			}
		}
		return null;
	}
	@Override
	public void OutputStructureUpdateDependables(Tabable t, String ID) {
		if(t.getClass()==ItemXML.class&& intake!=null){
			for(int i = 0; i < intake.length;i++ ) {
				if(ID.equals(intake[i].GetID())){
					intake[i].ID = t.GetID();
				}
			}
		}
	}
}
