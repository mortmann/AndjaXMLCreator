package com.mortmann.andja.creator.util;

public interface Tabable extends Comparable<Tabable> {
	String GetName();
	String GetID();
	Tabable DependsOnTabable(Tabable t);
	void UpdateDependables(Tabable t, String ID);
	String GetButtonColor();
}
