package com.mortmann.andja.creator.saveclasses;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import com.mortmann.andja.creator.other.Effect;
import com.mortmann.andja.creator.other.GameEvent;

import javafx.collections.ObservableMap;

@Root
public class Events extends BaseSave {
	static String FileName = "events.xml";
	
	@ElementList(required=false, name="Effects", inline=true)
	public ArrayList<Effect> effects;
	@ElementList(required=false, name="Effects", inline=true)
	public ArrayList<GameEvent> gameEvents;
	
	public Events() {}
	public Events(Collection<Effect> effects,Collection<GameEvent> gameEvents) {
		this.effects = new ArrayList<>(effects);
		this.gameEvents = new ArrayList<>(gameEvents);
	}
	public static void Load(ObservableMap<String, Effect> idToEffect, ObservableMap<String, GameEvent> idToGameEvent) {
		Serializer serializer = new Persister(new AnnotationStrategy());
		try {
			Events e = serializer.read(Events.class, Paths.get(saveFilePath, FileName).toFile());
			if(e.effects!=null)
				for (Effect u : e.effects) {
					idToEffect.put(u.GetID(), u);
				}
			if(e.gameEvents!=null)
				for (GameEvent u : e.gameEvents) {
					idToGameEvent.put(u.GetID(), u);
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
