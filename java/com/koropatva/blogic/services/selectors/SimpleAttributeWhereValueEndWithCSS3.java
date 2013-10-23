package com.koropatva.blogic.services.selectors;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.koropatva.blogic.ParseExcpetion;
import com.koropatva.blogic.interfaces.IEvent;
import com.koropatva.blogic.interfaces.ISelectorRole;
import com.koropatva.blogic.services.IteratorLogic;

public class SimpleAttributeWhereValueEndWithCSS3 implements ISelectorRole {

	public static final String	PATTERN	= String.format(
												"%s\\[[[\\p{Blank}]{1}]?[^\\p{Blank}\\=\\^\\|]*[[\\p{Blank}]{1}]?\\$\\=[[\\p{Blank}]{1}]?[\"]?[^\\p{Blank}\\=\\~\\|]*[\"]?[[\\p{Blank}]{1}]?\\]$",
												SimpleElement.PATTERN);

	@Override
	public void checkClass(final String selectedClass, Element element) throws ParseExcpetion {
		final String currentAttribute = selectedClass.substring(selectedClass.indexOf("[") + 1,
				selectedClass.indexOf("$")).trim();
		final String currentValue;
		if (selectedClass.indexOf("\"") > 0)
			currentValue = selectedClass.substring(selectedClass.indexOf("\"") + 1, selectedClass.lastIndexOf("\""))
					.trim();
		else
			currentValue = selectedClass.substring(selectedClass.indexOf("=") + 1, selectedClass.lastIndexOf("]"))
					.trim();
		final String currentElement = selectedClass.substring(0, selectedClass.indexOf("[")).trim();

		IteratorLogic.iteration(element.children(), new IEvent() {
			public void event(Element element) throws ParseExcpetion {
				if (currentElement != null && !currentElement.isEmpty() && element.nodeName().equals(currentElement)
						|| (currentElement == null || currentElement.isEmpty())) {
					Attributes attributes = element.attributes();
					for (Attribute attribute : attributes.asList()) {
						if (attribute.getKey().equals(currentAttribute)
								&& attribute.getValue().endsWith(currentValue)) {
							throw new ParseExcpetion(selectedClass);
						}
					}
				}
				if (element.children() != null) {
					IteratorLogic.iteration(element.children(), this);
				}
			}
		});
	}

	@Override
	public String getPattern() {
		return PATTERN;
	}

}
