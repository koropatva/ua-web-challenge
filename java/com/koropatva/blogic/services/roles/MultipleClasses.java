package com.koropatva.blogic.services.roles;

import org.jsoup.nodes.Element;

import com.koropatva.blogic.exceptions.ParseException;
import com.koropatva.blogic.interfaces.ISelectorRole;
import com.koropatva.blogic.services.IteratorWorker;
import com.koropatva.blogic.services.SelectorRoleFactory;

public class MultipleClasses implements ISelectorRole {

	public static final String	PATTERN	= String.format("^[\\.]?%1$s[\\.%1$s]{1,}[^\\.]$",
												SelectorRoleFactory.CLASS_NAME_REGEX);

	private String[]			currentSelector;

	private String				selectedSelector;

	@Override
	public void checkClass(String selectedSelector, Element element) throws ParseException {
		currentSelector = selectedSelector.split("\\.");
		this.selectedSelector = selectedSelector;
		event(element);
	}

	public void event(Element element) throws ParseException {
		if (currentSelector[0].isEmpty() || !currentSelector[0].isEmpty()
				&& currentSelector[0].equals(element.nodeName())) {
			// Check if element have all classes
			boolean fl = true;
			for (int i = 1; i < currentSelector.length; i++) {
				if (!element.attr("class").contains(currentSelector[i])) {
					fl = false;
					break;
				}
			}

			if (fl) {
				// We have found element with current selector
				throw new ParseException(selectedSelector, element);
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
