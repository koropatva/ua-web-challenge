package com.koropatva.blogic.services.roles;

import org.jsoup.nodes.Element;

import com.koropatva.blogic.exceptions.ParseException;
import com.koropatva.blogic.interfaces.ISelectorRole;
import com.koropatva.blogic.services.IteratorWorker;
import com.koropatva.blogic.services.SelectorRoleFactory;

public class SimpleSelector implements ISelectorRole {

	public static final String	PATTERN	= String.format(
												"^(\\#|\\.)?%1$s:((link)|(visited)|(active)|(hover)|(focus)|(first-letter)|(first-line)|(last-child)|(before)|(after))$",
												SelectorRoleFactory.CLASS_NAME_REGEX);

	private String				selectedSelector;

	private String				mainPart;

	@Override
	public void checkClass(String selectedSelector, Element element) throws ParseException {
		mainPart = selectedSelector.substring(0, selectedSelector.indexOf(":")).trim();
		this.selectedSelector = selectedSelector;
		event(element);
	}

	public void event(Element element) throws ParseException {
		if (checkPart(mainPart, element)) {
			// We have found element
			throw new ParseException(selectedSelector, element);
		}
		if (element.children() != null) {
			IteratorWorker.iteration(element.children(), this);
		}
	}

	private boolean checkPart(final String part, Element element) {
		if (part.startsWith(".") && element.attr("class").contains(part.substring(1))) {
			return true;
		} else if (part.startsWith("#") && element.attr("id").contains(part.substring(1))) {
			return true;
		} else if (element.nodeName().equals(part)) {
			return true;
		}
		return false;
	}

	@Override
	public String getPattern() {
		return PATTERN;
	}

}
