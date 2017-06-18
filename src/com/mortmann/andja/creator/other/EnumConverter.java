package com.mortmann.andja.creator.other;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import com.mortmann.andja.creator.other.Item.ItemType;

public class EnumConverter implements Converter<ItemType>
{

	@Override
	public ItemType read(InputNode node) throws Exception {
		return ItemType.values()[( Integer.parseInt(node.getValue()))];
	}

	@Override
	public void write(OutputNode node, ItemType arg1) throws Exception {
		node.setAttribute("Type", arg1.ordinal()+"");
	}
}