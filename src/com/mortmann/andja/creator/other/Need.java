package com.mortmann.andja.creator.other;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.other.Item.ItemType;
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
	@FieldInfo(order = 0, required=true, subType=PopulationLevel.class, PresetDefaultForHashMapTabable = true)
	public HashMap<String,Float> UsageAmounts;

	@Attribute
	@FieldInfo(order=0,required=true,id=true)
	public String ID;

	@FieldInfo(required=true,subType=String.class)
	@ElementMap(key = "lang",attribute=true,required=false) 
	public HashMap<String,String> Name;

	@FieldInfo(required=false, ComperatorMethod = "SortNeedItem")
	@Element(required=false)
	public Item item;
	@FieldInfo(required=false,compareType=NeedStructure[].class, ComperatorMethod = "SortNeedStructure")
	@ElementArray(required=false, entry = "id")
	public String[] structures;
	@FieldInfo(required=true,subType=PopulationLevel.class)
	@Element(required=false)
	public int startLevel;
	@FieldInfo(required=true)
	@Element(required=false)
	public int startPopulationCount;
	
	@FieldInfo(required=true,compareType=NeedGroup.class)
	@Element(required=false)
	public String group;
	@FieldInfo(required=true)
	@Element(required=false)
	public boolean hasToReachPerRoad;
	
	
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
		if(item != null) {
			return Name.get(Settings.CurrentLanguage.toString()) + " " + item.ID;
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
		if(UsageAmounts == null)
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
		if(UsageAmounts == null)
			return "NaN";
		String used = "";
		for(OutputStructure structure : GUI.Instance.GetOutputStructures(item)) {
			used += structure.GetName() + "\n";
			for(Item i : structure.output) {
				if(i.ID.contentEquals(item.ID) == false)
					continue;
				used += structure.PerNeedNeeded(this, i);
			}
		}

		return used; 
	}
	
	public Comparator<Structure> SortNeedStructure() {
		return new Comparator<Structure>(){
			@Override
			public int compare(Structure o1, Structure o2) {
				if(o1 instanceof NeedStructure == false && o2 instanceof NeedStructure == false)
					return o1.ID.compareTo(o2.ID);
				if(o1 instanceof NeedStructure && o2 instanceof NeedStructure) {
					if(o1.populationLevel == o2.populationLevel) {
						if(o1.populationCount == o2.populationCount) {
							return -10 + o1.ID.compareTo(o2.ID);
						} else {
							return ((Integer)o1.populationCount).compareTo(o2.populationCount);
						}
					} else {
						return ((Integer)o1.populationLevel).compareTo(o2.populationLevel);
					}
				}
				if(o1 instanceof NeedStructure)
					return -1;
				if(o2 instanceof NeedStructure)
					return 1;
				return 1;
			};
		};
	}
	public Comparator<Item> SortNeedItem() {
		return new Comparator<Item>(){
			@Override
			public int compare(Item o1, Item o2) {
				if(o1.getType() != ItemType.Luxury && o2.getType() != ItemType.Luxury)
					return o1.ID.compareTo(o2.ID);
				if(o1.getType() == ItemType.Luxury && o2.getType() == ItemType.Luxury)
					return -10 + o1.ID.compareTo(o2.ID);
				if(o1.getType() == ItemType.Luxury)
					return -1;
				if(o2.getType() == ItemType.Luxury)
					return 1;
				return 1;
			}
		};
	}
}
