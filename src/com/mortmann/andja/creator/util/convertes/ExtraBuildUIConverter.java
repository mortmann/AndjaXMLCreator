package com.mortmann.andja.creator.util.convertes;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import com.mortmann.andja.creator.structures.Structure.ExtraBuildUI;

public class ExtraBuildUIConverter implements Converter<ExtraBuildUI>{
		
		@Override
		public ExtraBuildUI read(InputNode node) throws Exception {
			return ExtraBuildUI.values()[( Integer.parseInt(node.getValue()))];
		}
	
		@Override
		public void write(OutputNode node, ExtraBuildUI arg1) throws Exception {
			node.setValue(arg1.toString());
		}
}
