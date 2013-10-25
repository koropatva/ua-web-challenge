package com.koropatva.blogic.services.roles;

import org.jsoup.nodes.Element;

import com.koropatva.blogic.exceptions.ParseException;
import com.koropatva.blogic.interfaces.ISelectorRole;
import com.koropatva.blogic.services.IteratorWorker;
import com.koropatva.blogic.services.SelectorRoleFactory;

public class SimpleSelectorCSS3 implements ISelectorRole {

	public static final String	PATTERN	= String.format("^(%s)?((:first-of-type)|(:last-of-type)|(:last-child))",
												SelectorRoleFactory.CLASS_NAME_REGEX);

	private String				currentSeparator;

	private String				selectedSeparator;

	@Override
	public void checkClass(String selectedSeparator, Element element) throws ParseException {
		this.selectedSeparator = selectedSeparator;
		if (selectedSeparator.contains(":")) {
			this.currentSeparator = selectedSeparator.substring(0, selectedSeparator.indexOf(":"));
		} else {
			this.currentSeparator = selectedSeparator;
		}
		event(element);
	}

	public void event(Element element) throws ParseException {
		if (currentSeparator.isEmpty() || element.nodeName().equals(currentSeparator)) {
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
