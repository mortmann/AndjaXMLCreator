package com.mortmann.andja.creator.other;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.structures.Home;
import com.mortmann.andja.creator.structures.NeedStructure;
import com.mortmann.andja.creator.structures.OutputStructure;
import com.mortmann.andja.creator.structures.Structure;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.MethodInfo;
import com.mortmann.andja.creator.util.Settings;
import com.mortmann.andja.creator.util.Tabable;
import com.mortmann.andja.creator.util.Vector2;

@Root(strict=false,name="Need")
public class Need implements Tabable {
	public final float UseTickTime = 60f;
	
	@ElementMap(key = "Level",attribute=true,required=false) 
	@FieldInfo(order = 0, required=true, subType=PopulationLevel.class)
	public HashMap<String,Float> UsageAmounts;

	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public String ID;

	@FieldInfo(required=true,subType=String.class)
	@ElementMap(key = "lang",attribute=true,required=false) 
	public HashMap<String,String> Name;

	@FieldInfo(required=false)
	@Element(required=false)
	public Item item;
	@FieldInfo(required=false,compareType=NeedStructure[].class)
	@Element(required=false)
	public String[] structures;
	
	@FieldInfo(required=true,subType=PopulationLevel.class)
	@Element(required=false)
	public int startLevel;
	@FieldInfo(required=true)
	@Element(required=false)
	public int popCount;
	
	@FieldInfo(required=true,compareType=NeedGroup.class)
	@Element(required=false)
	public String group;
	
	@Override
	public String GetID() {
		return ID;
	}

	@Override
	public Tabable DependsOnTabable(Tabable t) {
		if(t instanceof Item){
			if(item!=null && t.GetID().equals(item.GetID())){
				return this;
			}
		}
		if(t instanceof NeedStructure){
			if(structures!=null) {
				for (String id : structures) {
					if(t.GetID().equals(id)){
						return this;
					}
				}
			}
		}
		if(t instanceof NeedGroup){
			if(t.GetID().equals(group)){
				return this;
			}
		}
		if(t instanceof PopulationLevel) {
			for(String id : UsageAmounts.keySet()){
				if(t.GetID().equals(id)){
					return this;
				}
			}
		}
		return null;
	}
	@Override
	public String toString() {
		return GetName();
	}
	@Override
	public String GetName() {
		if(Name==null||Name.isEmpty()){
			return getClass().getSimpleName();
		}
		return Name.get(Settings.CurrentLanguage.toString());
	}

	@Override
	public void UpdateDependables(Tabable t, String ID) {
		if(t instanceof Item && item!=null){
			if(ID.equals(item.GetID())){
				item.ID = t.GetID();
			}
		}
		if(t instanceof NeedStructure && structures!=null){
			for(int i = 0; i<structures.length;i++ ) {
				if(ID.equals(structures[i])){
					 structures[i] = t.GetID();
				}
			}
		}
		if(t instanceof NeedGroup){
			if(ID.equals(group)){
				group = t.GetID();
			}
		}
		if(t instanceof PopulationLevel) {
			UsageAmounts.put(t.GetID(), UsageAmounts.remove(ID));
		}
	}

	@Override
	public String GetButtonColor() {
		if(item == null && structures != null)
			return "#f0932b";
		if(item != null && structures == null)
			return "#7ed6df";
		return null;
	}

	@Override
	public int compareTo(Tabable o) {
		return ID.compareTo(o.GetID());
	}
	@MethodInfo(Title = "Ton used per 1000")
	public String TonUsedPer1000() {
		if(structures!=null&&structures.length>=1)
			return "NaN";
		if(item==null)
			return "NaN";
		String used = "";
		for(String s : UsageAmounts.keySet()) {
			double value = UsageAmounts.get(s) * 1000d;
			value = Math.round(value * 10000d) / 10000d;
			used += GUI.Instance.idToPopulationLevel.get(s) + ":" + value;
			used += "\n";
		}
		return used; 
	}
	@MethodInfo(Title = "Structure needed per 1000")
	public String StructuresPer1000() {
		if(structures!=null&&structures.length>=1) {
			ArrayList<Structure> homes = GUI.Instance.getStructureList(Home.class);
			System.out.println(homes.size());
			if(homes.size()==0) {
				return "No Homes.";
			}
			ArrayList<Structure> needS = GUI.Instance.getStructureList(NeedStructure.class);
			needS.removeIf(x->Arrays.asList(structures).contains(x.GetID())==false);
			String counts="";
			for(Structure nS : needS) {
				for(int i = 0; i <= GUI.Instance.idToPopulationLevel.size(); i++) {
					int level = i;
					Optional<Structure> home = homes.stream().filter(x->x.populationLevel == level).findFirst();
					if(home.isEmpty())
						continue;
					counts = nS.GetName()+":\n";
					Vector2 size = new Vector2(home.get().tileWidth,home.get().tileHeight);
					int tiles = (int) (size.x * size.y);
					float rangeTiles = nS.InRangeTiles();
					counts += GUI.Instance.idToPopulationLevel.get(i+"").GetName() + ": ";
					counts += size + " " + rangeTiles/(tiles+1.5f) + " \n";

				}
			}
			return counts;
		}
		if(item == null)
			return "Undefined";
		String used = "";
		for(OutputStructure structure : GUI.Instance.GetOutputStructures(item)) {
			used += structure.GetName() + "\n";
			used += structure.CalculateNeededPerNeedPer1000();
		}
		for(String s : UsageAmounts.keySet()) {
			double value = UsageAmounts.get(s) * 1000d;
			value = Math.round(value * 10000d) / 10000d;
			used += GUI.Instance.idToPopulationLevel.get(s) + ":" + value;
			used += "\n";
		}
		return used; 
	}
}
