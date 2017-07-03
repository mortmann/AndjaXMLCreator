package com.mortmann.andja.creator.saveclasses;

import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.unitthings.Unit;

@Root
public class Units {
	public Units(){}
	public Units(Collection<Unit> values) {
		units = new ArrayList<>(values);
	}

	@ElementList(name="Units", inline=true)
	public ArrayList<Unit> units;

}
