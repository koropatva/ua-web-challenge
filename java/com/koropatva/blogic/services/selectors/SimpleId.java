package com.koropatva.blogic.services.selectors;

import org.jsoup.nodes.Element;

import com.koropatva.blogic.ParseExcpetion;
import com.koropatva.blogic.interfaces.IEvent;
import com.koropatva.blogic.interfaces.ISelectorRole;
import com.koropatva.blogic.services.IteratorLogic;

public class SimpleId implements ISelectorRole {

	@Override
	public void checkClass(final String selectedClass, Element element) throws ParseExcpetion {
		final String currentClass = selectedClass.replace("#", "");

		IteratorLogic.iteration(element.children(), new IEvent() {
			public void event(Element element) throws ParseExcpetion {
				if (element.attr("id").contains(currentClass)) {
					throw new ParseExcpetion(selectedClass);
				}
				if (element.children() != null) {
					IteratorLogic.iteration(element.children(), this);
				}
			}
		});
	}

	@Override
	public String getPattern() {
		return "^\\#[^\\p{Blank}]*";
	}

}
