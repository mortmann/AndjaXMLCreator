package com.mortmann.andja.creator.saveclasses;

import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.ElementList;

import com.mortmann.andja.creator.other.Need;
import com.mortmann.andja.creator.other.NeedGroup;

public class Needs {
	public Needs(Collection<Need> values) {
		this.needs = new ArrayList<>(values);
	}
	public Needs(){}
	@ElementList(name="Needs", inline=true)
	public ArrayList<Need> needs;
	@ElementList(name="GroupNeeds", inline=true)
	public ArrayList<NeedGroup> groupNeeds;
	
}
