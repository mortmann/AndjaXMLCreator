package com.mortmann.andja.creator.other;

import java.util.Comparator;
import java.util.HashMap;

import org.simpleframework.xml.*;

import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Settings;
import com.mortmann.andja.creator.util.Tabable;

@Root(name = "Item", strict = false)
public class ItemXML extends Item implements Tabable, Comparator<Tabable> {

	@FieldInfo(required = true, subType = String.class)
	@ElementMap(key = "lang", attribute = true, required = false)
	public HashMap<String, String> Name;
	@Element(required = false)
	public ItemType type;
	@Element
	public int decays;

	@Override
	public String toString() {
		return GetName();
	}

	public ItemXML(ItemXML i) {
		super(i);
	}

	public ItemXML() {
	}

	@Override
	public Tabable DependsOnTabable(Tabable t) {
		return null;
	}

	@Override
	public String GetName() {
		if (Name == null || Name.isEmpty()) {
			return getClass().getSimpleName();
		}
		return Name.get(Settings.CurrentLanguage.toString());
	}

	@Override
	public void UpdateDependables(Tabable t, String ID) {

	}

	@Override
	public String GetButtonColor() {
		switch (type) {
		case Build:
			return "#3EB650";
		case Intermediate:
			return "#95afc0";
		case Luxury:
			return "#FCC133";
		case Military:
			return "#E12B38";
		default:
			return null;
		}
	}

	@Override
	public int compareTo(Tabable i) {
		if (i instanceof ItemXML)
			return Comparator.comparing(ItemXML::getType).thenComparing(ItemXML::GetID).compare(this, (ItemXML) i);
		;
		return GetID().compareTo(i.GetID());
	}

	@Override
	public int compare(Tabable o1, Tabable o2) {
		if (o1 instanceof ItemXML && o2 instanceof ItemXML)
			return ((ItemXML) o1).type.compareTo(((ItemXML) o2).type);
		return o1.GetID().compareTo(o2.GetID());
	}

	@Override
	public ItemType getType() {
		return type;
	}
}
