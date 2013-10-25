package com.koropatva.blogic.interfaces;

import org.jsoup.nodes.Element;

import com.koropatva.blogic.exceptions.ParseException;

public interface ISelectorRole extends IEvent {
	void checkClass(final String selectedClass, Element element) throws ParseException;

	String getPattern();
}
