package com.mortmann.andja.creator.saveclasses;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.mortmann.andja.creator.structures.*;

public class Structures {

	public ArrayList<Road> roads;
	public ArrayList<Farm> farms;
	public ArrayList<Growable> growables;
	public ArrayList<Market> markets;
	public ArrayList<Mine> mines;
	public ArrayList<NeedStructure> needstructures;
	public ArrayList<Production> productions;
	public ArrayList<Warehouse> warehouses;
	public ArrayList<Home> homes;
	public ArrayList<MilitaryStructure> militarystructures;
	public ArrayList<ServiceStructure> servicestructures;

	public Structures(){
		
	}
	public Structures(List<Structure> Structures){
		roads = new ArrayList<>();
		farms = new ArrayList<>();
		growables = new ArrayList<>();
		markets = new ArrayList<>();
		mines = new ArrayList<>();
		needstructures = new ArrayList<>();
		productions = new ArrayList<>();
		warehouses = new ArrayList<>();
		homes = new ArrayList<>();
		militarystructures = new ArrayList<>();
		servicestructures = new ArrayList<>();
		Collections.sort(Structures);
		for (Structure s : Structures) {
			if(s instanceof Road){
				roads.add((Road)s);
			}
			else if(s instanceof Farm){
				farms.add((Farm)s);
			}
			else if(s instanceof Growable){
				growables.add((Growable)s);
			}
			else if(s instanceof Mine){
				mines.add((Mine)s);
			}
			else if(s instanceof NeedStructure){
				needstructures.add((NeedStructure)s);
			}
			else if(s instanceof Production){
				productions.add((Production)s);
			}
			else if(s instanceof Warehouse){
				warehouses.add((Warehouse)s);
			}
			else if(s instanceof Market){
				markets.add((Market)s);			
			}
			else if(s instanceof Home){
				homes.add((Home)s);			
			}
			else if(s instanceof MilitaryStructure){
				militarystructures.add((MilitaryStructure)s);			
			}
			else if(s instanceof ServiceStructure){
				servicestructures.add((ServiceStructure)s);			
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Structure> GetAllStructures(){
		ArrayList<Structure> strs = new ArrayList<>();
		for (Field f : this.getClass().getFields()) {
			try {
				if(f.get(this) != null)
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
