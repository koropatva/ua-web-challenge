package com.koropatva.blogic;

import org.jsoup.nodes.Element;

public class ParseExcpetion extends Exception {

	private static final long	serialVersionUID	= 1L;

	private Element				element;

	public ParseExcpetion() {
	}

	public ParseExcpetion(String e, Element element) {
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
