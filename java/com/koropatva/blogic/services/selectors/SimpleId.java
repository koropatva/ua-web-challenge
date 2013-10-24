package com.koropatva.blogic.services.selectors;

import org.jsoup.nodes.Element;

import com.koropatva.blogic.ParseExcpetion;
import com.koropatva.blogic.interfaces.IEvent;
import com.koropatva.blogic.interfaces.ISelectorRole;
import com.koropatva.blogic.services.IteratorWorker;
import com.koropatva.blogic.services.SelectorRoleFactory;

public class SimpleId implements ISelectorRole {

	private static final String	PATTERN	= String.format("^\\#%s", SelectorRoleFactory.CLASS_NAME_REGEX);

	@Override
	public void checkClass(final String selectedClass, Element element) throws ParseExcpetion {
		final String currentClass = selectedClass.replace("#", "");

		IteratorWorker.iteration(element.children(), new IEvent() {
			public void event(Element element) throws ParseExcpetion {
				if (element.attr("id").contains(currentClass)) {
					// We have found element with current selector
					throw new ParseExcpetion(selectedClass, element);
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
