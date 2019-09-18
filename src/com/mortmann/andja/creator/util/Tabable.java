package com.mortmann.andja.creator.util;

public interface Tabable {
	String GetName();
	String GetID();
	Tabable DependsOnTabable(Tabable t);
}
