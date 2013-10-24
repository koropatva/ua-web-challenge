package com.koropatva.blogic.services.selectors;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.koropatva.blogic.ParseExcpetion;
import com.koropatva.blogic.interfaces.IEvent;
import com.koropatva.blogic.interfaces.ISelectorRole;
import com.koropatva.blogic.services.IteratorWorker;
import com.koropatva.blogic.services.SelectorRoleFactory;

public class SimpleAttribute implements ISelectorRole {

	public static final String	PATTERN	= String.format("^%1$s\\[%2$s%1$s%2$s\\]$",
												SelectorRoleFactory.CLASS_NAME_REGEX, SelectorRoleFactory.BLANK_REGEX);

	@Override
	public void checkClass(final String selectedSelector, Element element) throws ParseExcpetion {
		// Split Attribute
		final String currentAttribute = selectedSelector.substring(selectedSelector.indexOf("[") + 1,
				selectedSelector.indexOf("]")).trim();
		// Split element
		final String currentElement = selectedSelector.substring(0, selectedSelector.indexOf("[")).trim();

		IteratorWorker.iteration(element.children(), new IEvent() {
			public void event(Element element) throws ParseExcpetion {
				// Check if we have element with name(if he is present in the
				// selector)
				if (currentElement != null && !currentElement.isEmpty() && element.nodeName().equals(currentElement)
						|| (currentElement == null || currentElement.isEmpty())) {
					Attributes attributes = element.attributes();
					for (Attribute attribute : attributes.asList()) {
						// Check if we have current attribute
						if (attribute.getKey().equals(currentAttribute)) {
							// We have found element with current selector
							throw new ParseExcpetion(selectedSelector, element);
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
