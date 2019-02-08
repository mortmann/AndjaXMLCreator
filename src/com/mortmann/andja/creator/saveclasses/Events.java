package com.mortmann.andja.creator.saveclasses;

import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.other.Effect;
import com.mortmann.andja.creator.other.GameEvent;

@Root
public class Events {
	@ElementList(name="Effects", inline=true)
	public ArrayList<Effect> effects;
	public ArrayList<GameEvent> gameEvents;
	
	public Events() {}
	public Events(Collection<Effect> effects,Collection<GameEvent> gameEvents) {
		this.effects = new ArrayList<>(effects);
		this.gameEvents = new ArrayList<>(gameEvents);

	}

}
