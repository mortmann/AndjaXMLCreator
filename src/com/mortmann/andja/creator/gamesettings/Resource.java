package com.mortmann.andja.creator.gamesettings;

import java.util.ArrayList;
import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.other.Fertility.Climate;
import com.mortmann.andja.creator.structures.Structure.TileType;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Range;
import com.mortmann.andja.creator.util.Size;

@Root(strict=false,name="resource")
public class Resource {
	@Attribute
	@FieldInfo(order = 0, required = true, fixed = true, ignore = true)
	public String ID;
	@Element(required = false)
	@FieldInfo(order = 1)
	public float percentageOfIslands = 0.2f;
	@FieldInfo(order = 2, required = false, subType = Climate.class)
	@ElementList(required = false, entry = "Climate")
	public ArrayList<Climate> climate;
	@FieldInfo(order = 2, required = false, subType = TileType.class)
	@ElementList(required = false, entry = "Type")
	public ArrayList<TileType> requiredTile;
	@FieldInfo(order = 3, required = true, subType = Range.class, fixed = true)
	@ElementMap(key = "islandSize", attribute = true, required = false)
	public HashMap<Size, Range> distributionMap;
	public Resource(String ID) {
		this.ID = ID;
		distributionMap = new HashMap<>();
		for(Size s : Size.values()) {
			distributionMap.put(s, new Range());
		}
	}
	
	public Resource() {}
}
