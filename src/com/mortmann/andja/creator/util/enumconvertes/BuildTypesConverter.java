package com.mortmann.andja.creator.util.enumconvertes;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import com.mortmann.andja.creator.structures.Structure.BuildTypes;

public class BuildTypesConverter implements Converter<BuildTypes>{
	
	@Override
	public BuildTypes read(InputNode node) throws Exception {
		return BuildTypes.values()[( Integer.parseInt(node.getValue()))];
	}

	@Override
	public void write(OutputNode node, BuildTypes arg1) throws Exception {
		node.setValue(arg1.ordinal()+"");
	}
}
