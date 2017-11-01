package com.mortmann.andja.creator.util;

public interface Tabable {
	String GetName();
	int GetID();
	Tabable DependsOnTabable(Tabable t);
}
