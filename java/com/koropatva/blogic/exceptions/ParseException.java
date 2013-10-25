package com.koropatva.blogic.exceptions;

import org.jsoup.nodes.Element;

public class ParseException extends Exception {

	private static final long	serialVersionUID	= 1L;

	private Element				element;

	public ParseException() {
	}

	public ParseException(String e, Element element) {
		super(e);
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

}
