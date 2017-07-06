package com.mortmann.andja.creator.util.convertes;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import com.mortmann.andja.creator.structures.Structure.BuildingTyp;

public class BuildingTypConverter implements Converter<BuildingTyp> {

	@Override
	public BuildingTyp read(InputNode node) throws Exception {
		return BuildingTyp.values()[(Integer.parseInt(node.getValue()))];
	}

	@Override
	public void write(OutputNode node, BuildingTyp arg1) throws Exception {
		node.setValue(arg1.ordinal() + "");
	}
}
