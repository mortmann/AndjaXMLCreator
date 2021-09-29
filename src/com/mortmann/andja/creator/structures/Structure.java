package com.mortmann.andja.creator.structures;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.other.Fertility.Climate;
import com.mortmann.andja.creator.other.Item;
import com.mortmann.andja.creator.other.ItemXML;
import com.mortmann.andja.creator.other.PopulationLevel;
import com.mortmann.andja.creator.other.Item.ItemType;
import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.MethodInfo;
import com.mortmann.andja.creator.util.Settings;
import com.mortmann.andja.creator.util.Tabable;
import com.mortmann.andja.creator.util.Vector2;

@Root(strict=false)
public abstract class Structure implements Tabable, Comparable<Tabable>  {
	public enum BuildTypes {Drag, Path, Single};
	public enum StructureTyp {Pathfinding, Blocking,Free};
	public enum Direction {None, N, E, S, W};
	public enum ExtraUI { None, Range, Upgrade, Efficiency };
	public enum ExtraBuildUI { None, Range, Efficiency };
	public enum BuildRestriktions { Land, Shore, Mountain };
	public enum TileType { NoRestriction, BuildLand, /*Ocean,*/ Shore, Cliff, Water, Dirt, Grass, Stone, Desert, Steppe, Jungle, Mountain };

	@Attribute
	@FieldInfo(order=-1,required=true,id=true)
	public String ID;	

	@FieldInfo(order=1,required=true,subType=String.class)@ElementMap(key = "lang",attribute=true) public HashMap<String,String> Name;
	@FieldInfo(required=true,subType=String.class,longtext=true)@ElementMap(key = "lang",attribute=true) public HashMap<String,String> Description;
	@FieldInfo(required=false,subType=String.class)@ElementMap(key = "lang",attribute=true,required=false) public HashMap<String,String> HoverOver;
	@FieldInfo(subType=String.class) @ElementMap(key = "lang",required=false) public HashMap<String,String> Short;
	
	@Element(required=false) public boolean canBeBuild = true;
	@Element(required=false) public boolean isWalkable;
	@Element(required=false) public boolean hasHitbox;
	
	@FieldInfo(required=true, IsEffectable=true) @Element public float maxHealth;
	@FieldInfo(required=true, ignore=true, IsEffectable=true) public float CurrentHealth; //only for effects here!

	@FieldInfo(IsEffectable=true) @Element(required=false) public int structureRange = 0;
	
	@FieldInfo(required=true,subType=PopulationLevel.class)@Element public int populationLevel = -1;
	@FieldInfo(required=true)@Element public int populationCount = -1;
	@FieldInfo(IsEffectable=false) @Element(required=false) public int structureLevel = 0;

	@FieldInfo(required=true) @Element public int tileWidth;
	@FieldInfo(required=true) @Element public int tileHeight;
	@FieldInfo(required=false, First2DName="tileWidth",Second2DName="tileHeight") @ElementArray(required=false) public TileType[][] buildTileTypes;
	
	@Element(required=false) public boolean canRotate = true;
	@Element(required=false) public boolean canBeBuildOver = false;
	@Element(required=false) public boolean canBeUpgraded = false;
	@Element(required=false) public boolean canTakeDamage = false;

	@Element(required=false) public boolean canStartBurning = false;
 
	@FieldInfo(IsEffectable=true) @Element(required=false) public int upkeepCost;
	@FieldInfo(IsEffectable=true) @Element(required=false) public int buildCost;

	@Element(required=false) public BuildTypes buildTyp =  BuildTypes.Single;
	@Element(required=false) public StructureTyp structureTyp = StructureTyp.Blocking;
	@Element(required=false) public ExtraUI extraUITyp = ExtraUI.None;
	@Element(required=false) public ExtraBuildUI extraBuildUITyp = ExtraBuildUI.None;
	
	@ElementArray(entry="Item",required=false) @FieldInfo(ComperatorMethod = "SortBuildItem") public Item[] buildingItems;

	@FieldInfo(order=0,required=true) @Element(required=false) public String spriteBaseName;
	
	@FieldInfo(order=1,required=false, subType = String.class, mainType = Climate.class) @ElementMap(required=false, attribute = true) public HashMap<Climate, String> climateSpriteModifier;
	
	public int compareTo(Tabable str) {
		if(Structure.class.isAssignableFrom(str.getClass())) {
			return Comparator.comparing(Structure::getClassName).thenComparing(Structure::getPopulationLevel).thenComparing(Structure::getPopulationCount).compare(this,(Structure) str);
		}
		return GetID().compareTo(str.GetID());
	}	
	@Override
	public String toString() {
		return GetName();
	}
	@Override
	public String GetID() {
		return ID;
	}
	protected Tabable StructureDependsOnTabable(Tabable t) {
		return null;
	}
	@Override
	public Tabable DependsOnTabable(Tabable t) {
		if(t.getClass()==ItemXML.class){
			if(buildingItems!=null) {
				for (Item item : buildingItems) {
					if(item.GetID().equals(t.GetID())){
						return this;
					}
				}
			}
		}
		return StructureDependsOnTabable(t);
	}
	public int getPopulationLevel() {
		return populationLevel;
	}
	public int getPopulationCount() {
		return populationCount;
	}
	public String getClassName() {
		return getClass().getName();
	}
	@Override
	public void UpdateDependables(Tabable t, String ID) {
		if(t.getClass()==ItemXML.class && buildingItems!=null){
			for(int i = 0; i < buildingItems.length;i++ ) {
				if(ID.equals(buildingItems[i].GetID())){
					buildingItems[i].ID = t.GetID();
				}
			}
		}
		StructureUpdateDependables(t,ID);
	}
	public void StructureUpdateDependables(Tabable t, String ID) {

	}
	@Override
	public String GetName() {
		if(Name==null||Name.isEmpty()){
			return getClass().getSimpleName();
		}
		return Name.get(Settings.CurrentLanguage.toString());
	}
	
	public static int CalculateMidPointCircleTileCount(int radius, int centerWidth, int centerHeight) {
        HashSet<Vector2> vecs = new HashSet<Vector2>();
		int center_x = radius;
        int center_y = radius;
        int P = (5 - radius * 4) / 4;
        int circle_x = 0;
        int circle_y = radius;
        do {
            //Fill the circle 
            for (int actual_x = center_x - circle_x; actual_x <= center_x + circle_x; actual_x++) {
                //-----
                int actual_y = center_y + circle_y;
                if (CircleCheck(radius, centerWidth, actual_x) || CircleCheck(radius, centerHeight, actual_y))
                	vecs.add(new Vector2(actual_x, actual_y));
                //-----
                actual_y = center_y - circle_y;
                if (CircleCheck(radius, centerWidth, actual_x) || CircleCheck(radius, centerHeight, actual_y))
                	vecs.add(new Vector2(actual_x,actual_y));
            }
            for (int actual_x = center_x - circle_y; actual_x <= center_x + circle_y; actual_x++) {
                //-----
                int actual_y = center_y + circle_x;
                if (CircleCheck(radius, centerWidth, actual_x) || CircleCheck(radius, centerHeight, actual_y))
                	vecs.add(new Vector2(actual_x,actual_y));
                //-----
                actual_y = center_y - circle_x;
                if (CircleCheck(radius, centerWidth, actual_x) || CircleCheck(radius, centerHeight, actual_y))
                	vecs.add(new Vector2(actual_x,actual_y));
            }
            if (P < 0) {
                P += 2 * circle_x + 1;
            }
            else {
                P += 2 * (circle_x - circle_y) + 1;
                circle_y--;
            }
            circle_x++;
        } while (circle_x <= circle_y);
        return vecs.size();
    }
	
	public int CalculateEvcenCircle(float radius, float centerWidth, float centerHeight) {
		HashSet<Vector2> vecs = new HashSet<Vector2>();
        for (float x = -radius + 0.5f; x <= radius - 0.5f; x++) {
            for (float y = -radius + 0.5f; y <= radius - 0.5f; y++) {
                if (-centerWidth / 2 <= x && centerWidth / 2 >= x && -centerHeight / 2 <= y && centerHeight / 2 >= y) {
                    continue;
                }
                if (CircleDistance(x, y, 1) <= radius-1f) {
                	vecs.add(new Vector2(x, y));
                }
            }
        }
        return vecs.size();
	}

	public int CalculateEllipse(float radius_x, float radius_y, float centerWidth, float centerHeight) {
        float r_x = centerWidth % 2 == 0 ? 0.5f : 0;
        float r_y = centerHeight % 2 == 0 ? 0.5f : 0;

        float ratio = radius_x / radius_y;
		HashSet<Vector2> vecs = new HashSet<Vector2>();
        for (float x = -radius_x + r_x; x <= radius_x - r_x; x++) {
            for (float y = -radius_y + r_y; y <= radius_y - r_y; y++) {
                if (-centerWidth / 2 <= x && centerWidth / 2 >= x && -centerHeight / 2 <= y && centerHeight / 2 >= y) {
                    continue;
                }
                if (CircleDistance(x, y, ratio) <= radius_x - (r_x+r_y)) {
                	vecs.add(new Vector2(x, y));
                }
            }
        }
        return vecs.size();
	}
	static float CircleDistance(float x, float y, float ratio) {
		return (float) Math.floor(Math.sqrt((Math.pow(y* ratio, 2)) + Math.pow(x, 2)));
	}
    private static boolean CircleCheck(int radius, int centerHeight, int actual_y) {
        return (centerHeight > 0 && actual_y >= radius && actual_y < radius + centerHeight) == false;
    }
    @MethodInfo(BelongingVariable = "structureRange",Title="Range Tile Count")
	public int InRangeTiles() {
    	if(tileWidth%2==0&&tileHeight%2==0) {
    		return CalculateEvcenCircle(structureRange, tileWidth, tileHeight);
    	}
		return CalculateMidPointCircleTileCount(structureRange, tileWidth, tileHeight);
	}
    
    public Comparator<Item> SortBuildItem() {
		return new Comparator<Item>(){
			@Override
			public int compare(Item o1, Item o2) {
				if(o1.getType() == ItemType.Build && o2.getType() == ItemType.Build)
					return -o1.ID.compareTo(o2.ID);
				if(o1.getType() == ItemType.Build)
					return -1;
				if(o2.getType() == ItemType.Build)
					return 1;
				return 0;
			}
		};
	}
}
