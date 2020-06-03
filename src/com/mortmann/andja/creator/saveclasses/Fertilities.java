package com.mortmann.andja.creator.saveclasses;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import com.mortmann.andja.creator.other.Fertility;

import javafx.collections.ObservableMap;

@Root
public class Fertilities extends BaseSave {
	static String FileName = "fertilities.xml";

	public Fertilities(Collection<Fertility> values) {
		fertilities = new ArrayList<>(values);
	}
	public Fertilities(){
	}

	@ElementList(name="Fertilities",entry="Fertility",inline=true)
	public ArrayList<Fertility> fertilities;
	public static void Load(ObservableMap<String, Fertility> idToFertility) {
		Serializer serializer = new Persister(new AnnotationStrategy());
		try {
			Fertilities e = serializer.read(Fertilities.class, Paths.get(saveFilePath, FileName).toFile());
			for (Fertility i : e.fertilities) {
				idToFertility.put(i.GetID(), i);
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
