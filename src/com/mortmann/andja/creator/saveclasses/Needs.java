package com.mortmann.andja.creator.saveclasses;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import com.mortmann.andja.creator.GUI;
import com.mortmann.andja.creator.other.Need;
import com.mortmann.andja.creator.other.NeedGroup;

import javafx.collections.ObservableMap;

public class Needs extends BaseSave {	
	static String FileName = "needs.xml";

	public Needs(Collection<Need> ns,Collection<NeedGroup> ngs) {
		this.needs = new ArrayList<>(ns);
		this.groupNeeds = new ArrayList<>(ngs);
	}
	public Needs(){}
	@ElementList(name="Needs", inline=true)
	public ArrayList<Need> needs;
	@ElementList(name="GroupNeeds", inline=true, required=false)
	public ArrayList<NeedGroup> groupNeeds;
	
	public static void Load(ObservableMap<String, Need> idToNeed,ObservableMap<String, NeedGroup> idToNeedGroup) {
		Serializer serializer = new Persister(new AnnotationStrategy());
		try {
			Needs e = serializer.read(Needs.class, Paths.get(saveFilePath, FileName).toFile());
			if(e.needs!=null)
				for (Need u : e.needs) {
					idToNeed.put(u.GetID(), u);
				}
			if(e.groupNeeds!=null)
				for (NeedGroup u : e.groupNeeds) {
					idToNeedGroup.put(u.GetID(), u);
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
