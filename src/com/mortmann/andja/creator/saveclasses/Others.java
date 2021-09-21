package com.mortmann.andja.creator.saveclasses;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import com.mortmann.andja.creator.other.PopulationLevel;

import javafx.collections.ObservableMap;

@Root(strict=false,name="Other")
public class Others extends BaseSave {
	static String FileName = "other.xml";
	
	public Others(Collection<PopulationLevel> values) {
		populationLevels = new ArrayList<>(values);
	}
	public Others(){}
	@ElementList(name="PopulationLevels")
	public ArrayList<PopulationLevel> populationLevels;
	
	public static void Load(ObservableMap<String, PopulationLevel> idToPopulationLevel) {
		Serializer serializer = new Persister(new AnnotationStrategy());
		try {
			Others e = serializer.read(Others.class, Paths.get(saveFilePath, FileName).toFile());
			if(e.populationLevels!=null)
				for (PopulationLevel u : e.populationLevels) {
					idToPopulationLevel.put(""+u.LEVEL, u);
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
