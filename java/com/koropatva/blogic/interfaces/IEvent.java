package com.koropatva.blogic.interfaces;

import org.jsoup.nodes.Element;

import com.koropatva.blogic.exceptions.ParseException;

public interface IEvent {
	void event(Element element) throws ParseException;
}
