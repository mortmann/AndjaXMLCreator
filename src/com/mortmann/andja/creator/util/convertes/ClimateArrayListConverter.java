package com.mortmann.andja.creator.util.convertes;

import java.util.ArrayList;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import com.mortmann.andja.creator.other.Fertility.Climate;

public class ClimateArrayListConverter implements Converter<ArrayList<Climate>>{
	
	@Override
	public ArrayList<Climate> read(InputNode node) throws Exception {
		InputNode n = node.getNext("Climate");
		ArrayList<Climate> cs = new ArrayList<>();
		while(n!=null){
			String val = n.getValue();//fixes random nullpointer exception, why it happens idk
			if(val==null||val.isEmpty()){
				break;
			}
			Climate c = (Climate.values())[( Integer.parseInt(val))];
			cs.add(c);
			n.getNext("Climate");
		}
		return cs;
	}

	@Override
	public void write(OutputNode node, ArrayList<Climate> arg1) throws Exception {
		for(Climate c : arg1){
			node.setValue(c.toString());
		}
	}
}