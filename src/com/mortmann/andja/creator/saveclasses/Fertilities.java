package com.mortmann.andja.creator.saveclasses;

import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.other.Fertility;

@Root
public class Fertilities {

	public Fertilities(Collection<Fertility> values) {
		fertilities = new ArrayList<>(values);
	}
	public Fertilities(){
	}

	@ElementList(name="Fertilities",entry="Fertility",inline=true)
	public ArrayList<Fertility> fertilities;

}
