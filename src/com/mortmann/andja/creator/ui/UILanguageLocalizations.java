package com.mortmann.andja.creator.ui;

import java.util.ArrayList;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(strict=false)
public class UILanguageLocalizations {
	@ElementList public ArrayList<String> missingLocalization;
}
