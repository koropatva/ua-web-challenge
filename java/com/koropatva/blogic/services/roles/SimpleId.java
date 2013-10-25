package com.koropatva.blogic.services.roles;

import org.jsoup.nodes.Element;

import com.koropatva.blogic.exceptions.ParseException;
import com.koropatva.blogic.interfaces.ISelectorRole;
import com.koropatva.blogic.services.IteratorWorker;
import com.koropatva.blogic.services.SelectorRoleFactory;

public class SimpleId implements ISelectorRole {

	public static final String	PATTERN	= String.format("^\\#%s", SelectorRoleFactory.CLASS_NAME_REGEX);

	private String				selectedClass;

	private String				selectedSeparator;

	@Override
	public void checkClass(String selectedSeparator, Element element) throws ParseException {
		this.selectedClass = selectedSeparator.replace("#", "");
		this.selectedSeparator = selectedSeparator;
		event(element);
	}

	public void event(Element element) throws ParseException {
		if (element.attr("id").contains(selectedClass)) {
			// We have found element with current selector
			throw new ParseException(selectedSeparator, element);
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
