package com.mortmann.andja.creator.util.convertes;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import com.mortmann.andja.creator.structures.Structure.Direction;

public class DirectionConverter implements Converter<Direction>{
	
	@Override
	public Direction read(InputNode node) throws Exception {
		return Direction.values()[( Integer.parseInt(node.getValue()))];
	}

	@Override
	public void write(OutputNode node, Direction arg1) throws Exception {
		node.setValue(arg1.ordinal()+"");
	}
}
