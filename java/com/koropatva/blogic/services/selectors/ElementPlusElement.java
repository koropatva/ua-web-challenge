package com.koropatva.blogic.services.selectors;

import org.jsoup.nodes.Element;

import com.koropatva.blogic.ParseExcpetion;
import com.koropatva.blogic.interfaces.IEvent;
import com.koropatva.blogic.interfaces.ISelectorRole;
import com.koropatva.blogic.services.IteratorWorker;
import com.koropatva.blogic.services.SelectorRoleFactory;

public class ElementPlusElement implements ISelectorRole {

	public static final String	PATTERN	= String.format("^[\\.?\\#?]?%1$s%2$s[\\+]{1}%2$s[\\.?\\#?]?%1$s",
												SelectorRoleFactory.CLASS_NAME_REGEX, SelectorRoleFactory.BLANK_REGEX);

	@Override
	public void checkClass(final String selectedSelector, Element element) throws ParseExcpetion {
		final String currentSelector = selectedSelector;
		final String firstPart = currentSelector.substring(0, currentSelector.indexOf("+")).trim();
		final String secondPart = currentSelector.substring(currentSelector.indexOf("+") + 1).trim();

		IteratorWorker.iteration(element.children(), new IEvent() {

			public void event(Element element) throws ParseExcpetion {
				Element nextElementSibling = element.nextElementSibling();
				if (nextElementSibling != null && checkPart(firstPart, element)
						&& checkPart(secondPart, nextElementSibling)) {
					throw new ParseExcpetion(selectedSelector, element);
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
		});
	}

	@Override
	public String getPattern() {
		return PATTERN;
	}

}
