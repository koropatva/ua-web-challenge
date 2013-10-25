package com.koropatva.blogic.services.roles;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.koropatva.blogic.exceptions.ParseException;
import com.koropatva.blogic.interfaces.ISelectorRole;
import com.koropatva.blogic.services.IteratorWorker;
import com.koropatva.blogic.services.SelectorRoleFactory;

public class SimpleAttribute implements ISelectorRole {

	public static final String	PATTERN	= String.format("^%1$s\\[%2$s%1$s%2$s\\]$",
												SelectorRoleFactory.CLASS_NAME_REGEX, SelectorRoleFactory.BLANK_REGEX);

	private String				currentAttribute;

	private String				currentElement;

	private String				selectedSelector;

	@Override
	public void checkClass(String selectedSelector, Element element) throws ParseException {
		// Split Attribute
		currentAttribute = selectedSelector.substring(selectedSelector.indexOf("[") + 1, selectedSelector.indexOf("]"))
				.trim();
		// Split element
		currentElement = selectedSelector.substring(0, selectedSelector.indexOf("[")).trim();
		this.selectedSelector = selectedSelector;
		event(element);
	}

	public void event(Element element) throws ParseException {
		// Check if we have element with name(if he is present in the
		// selector)
		if (currentElement != null && !currentElement.isEmpty() && element.nodeName().equals(currentElement)
				|| (currentElement == null || currentElement.isEmpty())) {
			Attributes attributes = element.attributes();
			for (Attribute attribute : attributes.asList()) {
				// Check if we have current attribute
				if (attribute.getKey().equals(currentAttribute)) {
					// We have found element with current selector
					throw new ParseException(selectedSelector, element);
				}
			}

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
