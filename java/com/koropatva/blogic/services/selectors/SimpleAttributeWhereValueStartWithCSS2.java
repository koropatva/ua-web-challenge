package com.koropatva.blogic.services.selectors;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.koropatva.blogic.ParseExcpetion;
import com.koropatva.blogic.interfaces.IEvent;
import com.koropatva.blogic.interfaces.ISelectorRole;
import com.koropatva.blogic.services.IteratorWorker;
import com.koropatva.blogic.services.SelectorRoleFactory;

public class SimpleAttributeWhereValueStartWithCSS2 implements ISelectorRole {

	public static final String	PATTERN	= String.format("^%1$s\\[%2$s%1$s%2$s\\|\\=%2$s[\"]?%3$s[\"]?%2$s\\]$",
												SelectorRoleFactory.CLASS_NAME_REGEX, SelectorRoleFactory.BLANK_REGEX,
												SelectorRoleFactory.VALUE_REGEX);

	@Override
	public void checkClass(final String selectedClass, Element element) throws ParseExcpetion {
		final String currentAttribute = selectedClass.substring(selectedClass.indexOf("[") + 1,
				selectedClass.indexOf("|")).trim();
		final String currentValue;
		if (selectedClass.indexOf("\"") > 0) {
			currentValue = selectedClass.substring(selectedClass.indexOf("\"") + 1, selectedClass.lastIndexOf("\""))
					.trim();
		} else {
			currentValue = selectedClass.substring(selectedClass.indexOf("=") + 1, selectedClass.indexOf("]")).trim();
		}
		final String currentElement = selectedClass.substring(0, selectedClass.indexOf("[")).trim();

		IteratorWorker.iteration(element.children(), new IEvent() {
			public void event(Element element) throws ParseExcpetion {
				if (currentElement != null && !currentElement.isEmpty() && element.nodeName().equals(currentElement)
						|| (currentElement == null || currentElement.isEmpty())) {
					Attributes attributes = element.attributes();
					for (Attribute attribute : attributes.asList()) {
						if (attribute.getKey().equals(currentAttribute)
								&& attribute.getValue().startsWith(currentValue)) {
							// We have found element with current selector
							throw new ParseExcpetion(selectedClass, element);
						}
					}
				}
				if (element.children() != null) {
					IteratorWorker.iteration(element.children(), this);
				}
			}
		});
	}

	@Override
	public String getPattern() {
		return PATTERN;
	}

}
