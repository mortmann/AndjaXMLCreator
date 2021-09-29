package com.mortmann.andja.creator.ui;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;


@Root(strict=false,name = "translationData")
public class TranslationData {
	@Attribute(name = "id") public String id;
	@Element(required=false) public String translation;
    @Element(required=false) public String toolTipTranslation;
    @ElementArray(required=false, entry = "string") public String[] values;
    @Element(required=false) public Boolean valueIsMainTranslation;

    //For now not needed
//    @ElementList(required=false, name="uiElements", entry = "string") public ArrayList<String> uiElements;
    @Element(required=false) public Boolean onlyToolTip = null; // only null can be ignored in deserializing 
    @Element(required=false) public Integer valueCount = null; // only null can be ignored in deserializing 
    @Override
    public boolean equals(Object obj) {
    	if(obj instanceof TranslationData == false)
    		return false;
    	return id.contentEquals(((TranslationData)obj).id);
    }
    @Override
    public int hashCode() {
    	return id.hashCode();
    }
}
