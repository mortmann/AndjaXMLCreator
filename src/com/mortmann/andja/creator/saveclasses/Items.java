package com.mortmann.andja.creator.saveclasses;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import com.mortmann.andja.creator.other.ItemXML;

import javafx.collections.ObservableMap;

@Root
public class Items extends BaseSave {
	static String FileName = "items.xml";

	@ElementList(name="Items", inline=true)
	public ArrayList<ItemXML> items;
	
	public Items(Collection<ItemXML> values) {
		this.items = new ArrayList<>(values);
		Collections.sort(items);
	}
	public Items(){}

	public static void Load(ObservableMap<String, ItemXML> idToItem) {
		Serializer serializer = new Persister(new AnnotationStrategy());
		try {
			Items e = serializer.read(Items.class, Paths.get(saveFilePath, FileName).toFile());
			for (ItemXML i : e.items) {
				idToItem.put(i.GetID(), i);
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
