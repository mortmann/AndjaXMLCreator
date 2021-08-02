package com.mortmann.andja.creator.gamesettings;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.other.Fertility.Climate;
import com.mortmann.andja.creator.structures.Structure;
import com.mortmann.andja.creator.structures.Structure.TileType;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Tabable;

@Root(strict=false,name="spawnstructure")
public class SpawnStructure implements Tabable {
	public enum GenerationType { Random, Noise, GroupedNoise };
	public enum StructureType { Natural, Special, Building };

	@Attribute
	@FieldInfo(order = 1, required = true, fixed = true, compareType = Structure.class)
	public String ID;
	@FieldInfo(order = 2, required = false, subType = Climate.class)
	@ElementList(required = false, entry = "Climate")
	public ArrayList<Climate> climate;
	@FieldInfo(order = 2, required = false, subType = TileType.class)
	@ElementList(required = false, entry = "Type")
	public ArrayList<TileType> requiredTile;
	@Element(required = false)
	@FieldInfo(order = 1)
	public float perTileChance = 0.01f;
	@Element(required = false)
	@FieldInfo(order = 1)
	public float cubic2Fractal = 1.0045f;
	@Element(required = false)
	@FieldInfo(order = 1)
	public float valueFractal = .53f;
	@Element(required = false)
	@FieldInfo(order = 1)
	public GenerationType genType = GenerationType.Random;
	@Element(required = false)
	@FieldInfo(order = 2)
	public StructureType structureType = StructureType.Natural;

	@Element(required = false)
	@FieldInfo(order = 1)
	public boolean islandUnique = false;
	@Element(required = false)
	@FieldInfo(order = 2)
	public boolean worldUnique = false;
	
	
	@Override
	public int compareTo(Tabable o) {
		return ID.compareTo(o.GetID());
	}
	@Override
	public String GetName() {
		return "SPS "+ ID!=null? ID : getClass().getSimpleName();
	}
	@Override
	public String GetID() {
		return ID;
	}
	@Override
	public Tabable DependsOnTabable(Tabable t) {
		if(Structure.class.isAssignableFrom(t.getClass())) {
			if(t.GetID().equals(ID)) {
				return this;
			}
		}
		return null;
	}
	@Override
	public void UpdateDependables(Tabable t, String ID) {
		if(t.getClass().isAssignableFrom(Structure.class)) {
			if(ID.equals(ID)) {
				this.ID = t.GetID();
			}
		}
	}
	@Override
	public String GetButtonColor() {
		return null;
	}

}
