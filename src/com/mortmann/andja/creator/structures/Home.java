package com.mortmann.andja.creator.structures;

import org.simpleframework.xml.Element;

public class Home extends Structure {
	@Element public int maxLivingSpaces;
	@Element public float increaseSpeed;
	@Element public float decreaseSpeed;

}
