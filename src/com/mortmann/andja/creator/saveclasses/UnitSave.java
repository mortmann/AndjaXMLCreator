package com.mortmann.andja.creator.saveclasses;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import com.mortmann.andja.creator.unitthings.*;

import javafx.collections.ObservableMap;

@Root(name = "units")
public class UnitSave extends BaseSave {
	static String FileName = "units.xml";
	@ElementList(name = "units", inline = true)
	public ArrayList<Unit> units;
	@ElementList(name = "ships", inline = true)
	public ArrayList<Ship> ships;
	@ElementList(name = "workers", inline = true, required = false)
	public ArrayList<Worker> workers;

	public UnitSave() {
	}

	public UnitSave(Collection<Unit> values, Collection<Worker> workers) {
		this.workers = new ArrayList<Worker>(workers);
		units = new ArrayList<>();
		ships = new ArrayList<>();
		for (Unit u : values) {
			if (u instanceof Ship) {
				ships.add((Ship) u);
			} else if (u instanceof Unit) {
				units.add(u);
			}

		}
	}

	public ArrayList<Unit> getAllUnits() {
		ArrayList<Unit> sunits = new ArrayList<>();
		sunits.addAll(ships);
		sunits.addAll(units);
		return sunits;
	}

	public static void Load(ObservableMap<String, Unit> idToUnit, ObservableMap<String, Worker> idToWorker) {
		Serializer serializer = new Persister(new AnnotationStrategy());
		try {
			UnitSave e = serializer.read(UnitSave.class, Paths.get(saveFilePath, FileName).toFile());
			for (Unit u : e.getAllUnits()) {
				idToUnit.put(u.GetID(), u);
			}
			if (e.workers != null)
				for (Worker u : e.workers) {
					idToWorker.put(u.GetID(), u);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String GetSaveFileName() {
		return FileName;
	}
}
