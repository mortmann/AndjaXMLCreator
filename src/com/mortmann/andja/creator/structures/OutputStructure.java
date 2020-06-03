package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.*;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.other.ItemXML;
import com.mortmann.andja.creator.other.Need;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.MethodInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false)
public abstract class OutputStructure extends Structure {
	@Element public float contactRange=0;
	@Element public boolean forMarketplace=true;
	@FieldInfo(required = true, IsEffectable=true) @Element public int maxNumberOfWorker = 1;
	@FieldInfo(required = true, IsEffectable=true) @Element public float produceTime = 0;
	@FieldInfo(required = true, IsEffectable=true) @Element(required=false) public int maxOutputStorage;
	@FieldInfo(required = true, IsEffectable=true,Minimum = 0) @Element(required=false) public float efficiency = 1f;
	@FieldInfo @Element(required=false) public String workerWorkSound;
	@FieldInfo @Element(required=false) public String workSound;

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
	@MethodInfo(Title = "Number of Structures per 1000")
	public String CalculateNeededPerNeedPer1000() {
		String calculated = "";
		if(output==null)
			return calculated;
		for(Item out : output) {
			calculated += out.GetID() + ": ";
			Need[] needs = GUI.Instance.GetNeedsRequiringOutput(out);
			if(needs == null || needs.length == 0) {
				calculated += "Is not a Need.\n";
				continue;
			}
			
			for(Need need : needs) {
				String singleNeed = "";
				if(need.UsageAmounts==null) {
					singleNeed +="MISSING USAGEAMOUNTS " + need.GetName();
					continue;
				}
				for(String s : need.UsageAmounts.keySet()) {
					singleNeed += "\t"+ GUI.Instance.idToPopulationLevel.get(s) + ":";
					if(out.count*produceTime*efficiency != 0) {
						double value = ((double)need.UsageAmounts.get(s)) * 1000d * (double)out.count * (double)produceTime * (double)efficiency;
						value = Math.round(value * 10000d) / 10000d;
						value /= 60d;
						singleNeed += "\t"+ value;//(need.UsageAmounts.get(s) * 1000 * 60) / ((out.count*produceTime*efficiency));
					}
					else 
						singleNeed += "\t UNDEFINED";
					singleNeed += "\n";
				}			
				calculated += "\n";
				calculated += singleNeed;
			}
		}
		return calculated;
	}
}
