package com.mortmann.andja.creator.saveclasses;

import java.util.ArrayList;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.other.PopulationLevel;

@Root(strict=false,name="Other")
public class Others {
	@ElementList(name="PopulationLevels", inline=true)
	public ArrayList<PopulationLevel> populationLevels;
}
