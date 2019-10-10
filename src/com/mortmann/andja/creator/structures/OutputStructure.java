package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.*;

import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.other.ItemXML;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false)
public abstract class OutputStructure extends Structure {
	@Element public float contactRange=0;
	@Element public boolean forMarketplace=true;
	@FieldInfo(required = true, IsEffectable=true) @Element public int maxNumberOfWorker = 1;
	@FieldInfo(required = true, IsEffectable=true) @Element public float produceTime = 0;
	@FieldInfo(required = true, IsEffectable=true) @Element(required=false) public int maxOutputStorage;
	@FieldInfo(required = true, IsEffectable=true) @Element(required=false) public float efficiency = 1f;

	@ElementArray(entry="Item",required=false) public Item[] output;

	@Override
	protected Tabable StructureDependsOnTabable(Tabable t) {
		if(t.getClass()==ItemXML.class&& output!=null){
			for (Item item : output) {
				if(item.GetID().equals(t.GetID())){
					return this;
				}
			}
		}
		return OutputStructureDependsOnTabable(t);
	}
	protected Tabable OutputStructureDependsOnTabable(Tabable t) {
		return null;
	}
	@Override
	public void StructureUpdateDependables(Tabable t, String ID) {
		if(t.getClass()==ItemXML.class && output!=null){
			for(int i = 0; i < output.length;i++ ) {
				if(ID.equals( output[i].GetID() )){
					output[i].ID = t.GetID();
				}
			}
		}
		OutputStructureUpdateDependables(t,ID);
	}
	public void OutputStructureUpdateDependables(Tabable t, String ID) {

	}
}
