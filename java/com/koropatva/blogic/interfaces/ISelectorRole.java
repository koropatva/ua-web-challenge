package com.koropatva.blogic.interfaces;

import org.jsoup.nodes.Element;

import com.koropatva.blogic.ParseExcpetion;

public interface ISelectorRole {
	void checkClass(final String selectedClass, Element element) throws ParseExcpetion;

	String getPattern();
}
