package com.mortmann.andja.creator.gamesettings;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.util.FieldInfo;
import com.mortmann.andja.creator.util.Range;
import com.mortmann.andja.creator.util.Size;

@Root(strict=false,name="islandSize")
public class IslandSize {
	@Attribute
	@FieldInfo(order = 0, required = true, fixed = true, ignore=true)
	public Size size;
	@Element(required = false)
	@FieldInfo(order = 0, required = true)
	public Range fertilityRange = new Range();
	@Element(required = false)
	@FieldInfo(order = 1, required = true)
	public Range resourceRange = new Range();
	public IslandSize(Size s) {
		size = s;
		fertilityRange = new Range();
	}
	public IslandSize() {}
}

