package com.koropatva.blogic.services.roles;

import org.jsoup.nodes.Element;

import com.koropatva.blogic.exceptions.ParseException;
import com.koropatva.blogic.interfaces.ISelectorRole;
import com.koropatva.blogic.services.IteratorWorker;
import com.koropatva.blogic.services.SelectorRoleFactory;

public class SimpleElement implements ISelectorRole {

	public static final String	PATTERN	= String.format("^%s", SelectorRoleFactory.CLASS_NAME_REGEX);

	private String				currentSeparator;

	@Override
	public void checkClass(String selectedSeparator, Element element) throws ParseException {
		this.currentSeparator = selectedSeparator;
		event(element);
	}

	public void event(Element element) throws ParseException {
		if (element.nodeName().equals(currentSeparator)) {
			// We have found element with current selector
			throw new ParseException(currentSeparator, element);
		}
		if (element.children() != null) {
			IteratorWorker.iteration(element.children(), this);
		}
	}

	@Override
	public String getPattern() {
		return PATTERN;
	}

}
