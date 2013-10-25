package com.koropatva.blogic.services;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;

import com.koropatva.blogic.exceptions.ParseException;
import com.koropatva.blogic.interfaces.IEvent;
import com.koropatva.blogic.interfaces.ISelectorRole;
import com.koropatva.blogic.services.roles.MultipleClasses;
import com.koropatva.blogic.services.roles.SimpleAttribute;
import com.koropatva.blogic.services.roles.SimpleAttributeWhereValueContainCSS2;
import com.koropatva.blogic.services.roles.SimpleAttributeWhereValueContainCSS3;
import com.koropatva.blogic.services.roles.SimpleAttributeWhereValueEndWithCSS3;
import com.koropatva.blogic.services.roles.SimpleAttributeWhereValueEqual;
import com.koropatva.blogic.services.roles.SimpleAttributeWhereValueEqualCSS2;
import com.koropatva.blogic.services.roles.SimpleAttributeWhereValueStartWithCSS2;
import com.koropatva.blogic.services.roles.SimpleAttributeWhereValueStartWithCSS3;
import com.koropatva.blogic.services.roles.SimpleElement;
import com.koropatva.blogic.services.roles.SimpleId;
import com.koropatva.blogic.services.roles.SimpleSelector;
import com.koropatva.blogic.services.roles.SimpleSelectorCSS3;
import com.koropatva.model.SelectorsContainer;

public class SelectorRoleFactory implements IEvent {

	public static final String			CLASS_NAME_REGEX	= "[\\w\\_\\-]*";
	public static final String			VALUE_REGEX			= "[\\w\\s]*";
	public static final String			BLANK_REGEX			= "[[\\p{Blank}]{1}]?";

	// List of Roles for checking separators
	private static Set<ISelectorRole>	iSelectorRoles;
	static {
		iSelectorRoles = new HashSet<ISelectorRole>();
		iSelectorRoles.add(new SimpleId());
		iSelectorRoles.add(new SimpleElement());
		iSelectorRoles.add(new SimpleAttribute());
		iSelectorRoles.add(new SimpleAttributeWhereValueEqual());
		iSelectorRoles.add(new SimpleAttributeWhereValueContainCSS2());
		iSelectorRoles.add(new SimpleAttributeWhereValueContainCSS3());
		iSelectorRoles.add(new SimpleAttributeWhereValueStartWithCSS2());
		iSelectorRoles.add(new SimpleAttributeWhereValueStartWithCSS3());
		iSelectorRoles.add(new SimpleAttributeWhereValueEndWithCSS3());
		iSelectorRoles.add(new MultipleClasses());
		iSelectorRoles.add(new SimpleSelector());
		iSelectorRoles.add(new SimpleAttributeWhereValueEqualCSS2());
		iSelectorRoles.add(new SimpleSelectorCSS3());
	}

	private SelectorsContainer			selectorsContainer;

	private String						enteredSelector;

	private String						selector;

	private String						restOfCheckingSelector;

	public SelectorRoleFactory(SelectorsContainer separatorsContainer) {
		this.selectorsContainer = separatorsContainer;
	}

	public void checkClassIntoElement(String selector, Element element) {
		try {
			enteredSelector = new String(selector);

			selector = prepareSelector(selector);

			ISelectorRole iSelectorRole = getSelectorRole(selector);
			if (iSelectorRole != null) {
				iSelectorRole.checkClass(selector, element);
			} else {
				cutFirstSelector(selector);
				IteratorWorker.iteration(element.children(), this);
			}
		} catch (ParseException e) {
			selectorsContainer.getSelectorMapping().remove(e.getMessage());
		}
	}

	public void event(Element element) throws ParseException {
		try {
			ISelectorRole iSelectorRole = getSelectorRole(selector);
			if (iSelectorRole != null)
				iSelectorRole.checkClass(selector, element);
		} catch (ParseException e) {
			if (restOfCheckingSelector != null && !restOfCheckingSelector.isEmpty()) {
				cutFirstSelector(restOfCheckingSelector);
				if (restOfCheckingSelector != null && selector.matches("^((>)|(\\+)|(~))")) {
					if (selector.equals(">")) {
						cutFirstSelector(restOfCheckingSelector.trim());
						IteratorWorker.iteration(e.getElement().children(), this);
					} else if (selector.equals("+")) {
						cutFirstSelector(restOfCheckingSelector.trim());
						Element nextElementSibling = e.getElement().nextElementSibling();
						if (nextElementSibling != null) {
							event(nextElementSibling);
						}
					} else if (selector.equals("~")) {
						cutFirstSelector(restOfCheckingSelector.trim());
						Element firstElement = e.getElement();
						Element nextElementSibling;
						while ((nextElementSibling = firstElement.nextElementSibling()) != null) {
							event(nextElementSibling);
							firstElement = nextElementSibling;
						}
					}
				} else {
					event(e.getElement());
				}
			} else {
				throw new ParseException(enteredSelector, e.getElement());
			}
		}
	}

	private void cutFirstSelector(String currentSelector) {

		String[] selectors = currentSelector.split("\\p{Blank}", 2);
		this.selector = selectors[0];
		if (selectors.length > 1) {
			this.restOfCheckingSelector = selectors[1];
		} else {
			this.restOfCheckingSelector = null;
		}

	}

	private String prepareSelector(String selector) {
		selector = selector.trim();
		selector = selector.replaceAll(">", " > ");
		selector = selector.replaceAll("\\+", " + ");
		selector = selector.replaceAll("~[^\\=]", " ~ ");

		while (selector.contains("  ")) {
			selector = selector.replace("  ", " ");
		}
		return selector;
	}

	private ISelectorRole getSelectorRole(String selectedClass) {

		for (ISelectorRole iSelectorRole : iSelectorRoles) {
			Pattern pattern = Pattern.compile(iSelectorRole.getPattern());
			Matcher matcher = pattern.matcher(selectedClass);
			if (matcher.matches()) {
				return iSelectorRole;
			}
		}
		return null;
	}

}
