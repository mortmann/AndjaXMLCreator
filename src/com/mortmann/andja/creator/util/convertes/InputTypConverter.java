package com.mortmann.andja.creator.util.convertes;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import com.mortmann.andja.creator.structures.Production.InputTyp;


public class InputTypConverter implements Converter<InputTyp> {

	@Override
	public InputTyp read(InputNode node) throws Exception {
		return InputTyp.values()[(Integer.parseInt(node.getValue()))];
	}

	@Override
	public void write(OutputNode node, InputTyp arg1) throws Exception {
		node.setValue(arg1.toString());
	}
}
