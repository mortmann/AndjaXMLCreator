package com.mortmann.andja.creator.other;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import com.mortmann.andja.creator.structures.*;

public class Structures {

	public ArrayList<Road> Roads;
	public ArrayList<Farm> Farms;
	public ArrayList<Growable> Growables;
	public ArrayList<Market> Markets;
	public ArrayList<Mine> Mines;
	public ArrayList<NeedsBuilding> NeedsBuildings;
	public ArrayList<Production> Productions;
	public ArrayList<Warehouse> Warehouses;
	public Structures(){
		
	}
	public Structures(Iterable<Structure> Structures){
		Roads = new ArrayList<>();
		Farms = new ArrayList<>();
		Growables = new ArrayList<>();
		Markets = new ArrayList<>();
		Mines = new ArrayList<>();
		NeedsBuildings = new ArrayList<>();
		Productions = new ArrayList<>();
		Warehouses = new ArrayList<>();
		for (Structure s : Structures) {
			if(s instanceof Road){
				Roads.add((Road)s);
			}
			else if(s instanceof Farm){
				Farms.add((Farm)s);
			}
			else if(s instanceof Growable){
				Growables.add((Growable)s);
			}
			else if(s instanceof Market){
				Markets.add((Market)s);			
			}
			else if(s instanceof Mine){
				Mines.add((Mine)s);
			}
			else if(s instanceof NeedsBuilding){
				NeedsBuildings.add((NeedsBuilding)s);
			}
			else if(s instanceof Production){
				Productions.add((Production)s);
			}
			else if(s instanceof Warehouse){
				Warehouses.add((Warehouse)s);
			}
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Structure> GetAllStructures(){
		ArrayList<Structure> strs = new ArrayList<>();
		for (Field f : this.getClass().getFields()) {
			try {
				strs.addAll((Collection<? extends Structure>) f.get(this));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return strs;
	}
	
}
