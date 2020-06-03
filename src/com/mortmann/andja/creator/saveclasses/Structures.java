package com.mortmann.andja.creator.saveclasses;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import com.mortmann.andja.creator.structures.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class Structures extends BaseSave {
	static String FileName = "structures.xml";

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
	public Structures(Collection<Structure> Structures){
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
		ArrayList<Structure> list = new ArrayList<Structure>(Structures);
		Collections.sort(list);
		for (Structure s : list) {
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
			if(Modifier.isStatic(f.getModifiers()))
				continue;
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
	public static void Load(ObservableMap<String, Structure> idToStructures) {
		Serializer serializer = new Persister(new AnnotationStrategy());
		Structures s = new Structures();
		try {
			serializer.read(s, Paths.get(saveFilePath, FileName).toFile());
			for (Structure i : s.GetAllStructures()) {
				idToStructures.put(i.GetID(), i);
			} 
		} catch (Exception e1) {
			e1.printStackTrace();
			idToStructures = FXCollections.observableHashMap();
		}
	}
	@Override
	public String GetSaveFileName() {
		return FileName;
	}
}
