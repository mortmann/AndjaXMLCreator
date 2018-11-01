package com.mortmann.andja.creator.util.convertes;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import com.mortmann.andja.creator.structures.Structure.ExtraUI;


public class ExtraUIConverter implements Converter<ExtraUI>{
		
		@Override
		public ExtraUI read(InputNode node) throws Exception {
			return ExtraUI.values()[( Integer.parseInt(node.getValue()))];
		}
	
		@Override
		public void write(OutputNode node, ExtraUI arg1) throws Exception {
			node.setValue(arg1.toString());
		}
	}
