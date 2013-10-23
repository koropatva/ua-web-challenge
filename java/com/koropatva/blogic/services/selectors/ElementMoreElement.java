package com.koropatva.blogic.services.selectors;

import org.jsoup.nodes.Element;

import com.koropatva.blogic.ParseExcpetion;
import com.koropatva.blogic.interfaces.IEvent;
import com.koropatva.blogic.interfaces.ISelectorRole;
import com.koropatva.blogic.services.IteratorLogic;

public class ElementMoreElement implements ISelectorRole {

	@Override
	public void checkClass(final String selectedClass, Element element) throws ParseExcpetion {
		final String currentClass = selectedClass;
		final String firstPart = currentClass.substring(0, currentClass.indexOf(">")).trim();
		final String secondPart = currentClass.substring(currentClass.indexOf(">") + 1).trim();

		IteratorLogic.iteration(element.children(), new IEvent() {

			public void event(Element element) throws ParseExcpetion {
				boolean firstPartValid = checkPart(firstPart, element);

				if (firstPartValid) {
					IteratorLogic.iteration(element.children(), new IEvent() {

						@Override
						public void event(Element element) throws ParseExcpetion {
							if (checkPart(secondPart, element)) {
								throw new ParseExcpetion(selectedClass);
							}
						}
					});
				}
				if (element.children() != null) {
					IteratorLogic.iteration(element.children(), this);
				}
			}

			private boolean checkPart(final String firstPart, Element element) {
				if (firstPart.startsWith(".") && element.attr("class").contains(firstPart)) {
					return true;
				} else if (firstPart.startsWith("#") && element.attr("id").contains(firstPart)) {
					return true;
				} else if (element.nodeName().equals(firstPart)) {
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public String getPattern() {
		return "^[\\.?\\#?]?[^\\p{Blank}\\.\\#\\:]*[[\\p{Blank}]{1}]?[\\>]{1}[[\\p{Blank}]{1}]?[\\.?\\#?]?[^\\p{Blank}]{1}[^\\p{Blank}\\.\\#\\:]*";
	}

}
