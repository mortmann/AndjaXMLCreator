package com.mortmann.andja.creator.util;

public interface Tabable {

	int GetID();
	Tabable DependsOnTabable(Tabable t);
}
