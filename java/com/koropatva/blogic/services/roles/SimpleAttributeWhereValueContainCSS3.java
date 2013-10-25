package com.koropatva.blogic.services.roles;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.koropatva.blogic.exceptions.ParseException;
import com.koropatva.blogic.interfaces.ISelectorRole;
import com.koropatva.blogic.services.IteratorWorker;
import com.koropatva.blogic.services.SelectorRoleFactory;

public class SimpleAttributeWhereValueContainCSS3 implements ISelectorRole {

	public static final String	PATTERN	= String.format("^%1$s\\[%2$s%1$s%2$s\\*\\=%2$s[\"]?%3$s[\"]?%2$s\\]$",
												SelectorRoleFactory.CLASS_NAME_REGEX, SelectorRoleFactory.BLANK_REGEX,
												SelectorRoleFactory.VALUE_REGEX);

	private String				currentAttribute;

	private String				currentValue;

	private String				currentElement;

	private String				selectedSelector;

	@Override
	public void checkClass(String selectedSelector, Element element) throws ParseException {
		currentAttribute = selectedSelector.substring(selectedSelector.indexOf("[") + 1, selectedSelector.indexOf("*"))
				.trim();
		if (selectedSelector.indexOf("\"") > 0) {
			currentValue = selectedSelector.substring(selectedSelector.indexOf("\"") + 1,
					selectedSelector.lastIndexOf("\"")).trim();
		} else {
			currentValue = selectedSelector.substring(selectedSelector.indexOf("=") + 1, selectedSelector.indexOf("]"))
					.trim();
		}
		currentElement = selectedSelector.substring(0, selectedSelector.indexOf("[")).trim();
		this.selectedSelector = selectedSelector;
		event(element);
	}

	public void event(Element element) throws ParseException {
		if (currentElement != null && !currentElement.isEmpty() && element.nodeName().equals(currentElement)
				|| (currentElement == null || currentElement.isEmpty())) {
			Attributes attributes = element.attributes();
			for (Attribute attribute : attributes.asList()) {
				if (attribute.getKey().equals(currentAttribute) && attribute.getValue().contains(currentValue)) {
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
