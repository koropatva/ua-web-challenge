package com.koropatva.blogic.services;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;

import com.koropatva.blogic.ParseExcpetion;
import com.koropatva.blogic.interfaces.IEvent;
import com.koropatva.blogic.interfaces.ISelectorRole;
import com.koropatva.blogic.services.selectors.ElementMoreElement;
import com.koropatva.blogic.services.selectors.ElementPlusElement;
import com.koropatva.blogic.services.selectors.MultipleClasses;
import com.koropatva.blogic.services.selectors.SimpleAttribute;
import com.koropatva.blogic.services.selectors.SimpleAttributeWhereValueContainCSS2;
import com.koropatva.blogic.services.selectors.SimpleAttributeWhereValueContainCSS3;
import com.koropatva.blogic.services.selectors.SimpleAttributeWhereValueEndWithCSS3;
import com.koropatva.blogic.services.selectors.SimpleAttributeWhereValueEqual;
import com.koropatva.blogic.services.selectors.SimpleAttributeWhereValueStartWithCSS2;
import com.koropatva.blogic.services.selectors.SimpleAttributeWhereValueStartWithCSS3;
import com.koropatva.blogic.services.selectors.SimpleElement;
import com.koropatva.blogic.services.selectors.SimpleId;

public class SelectorRoleFactory {

	public static final String			CLASS_NAME_REGEX	= "[\\w\\_\\-]*";
	public static final String			VALUE_REGEX			= "[\\w\\s]*";
	public static final String			BLANK_REGEX			= "[[\\p{Blank}]{1}]?";

	private static Set<ISelectorRole>	iSelectorRoles;
	static {
		iSelectorRoles = new HashSet<ISelectorRole>();
		iSelectorRoles.add(new SimpleId());
		iSelectorRoles.add(new SimpleElement());
		iSelectorRoles.add(new ElementMoreElement());
		iSelectorRoles.add(new SimpleAttribute());
		iSelectorRoles.add(new SimpleAttributeWhereValueEqual());
		iSelectorRoles.add(new SimpleAttributeWhereValueContainCSS2());
		iSelectorRoles.add(new SimpleAttributeWhereValueContainCSS3());
		iSelectorRoles.add(new SimpleAttributeWhereValueStartWithCSS2());
		iSelectorRoles.add(new SimpleAttributeWhereValueStartWithCSS3());
		iSelectorRoles.add(new SimpleAttributeWhereValueEndWithCSS3());
		iSelectorRoles.add(new MultipleClasses());
		iSelectorRoles.add(new ElementPlusElement());
	}

	private ClassParser					classParser;

	private String						enteredSelector;

	private String						selector;

	private String						restOfCheckingSelector;

	public SelectorRoleFactory(ClassParser classParser) {
		this.classParser = classParser;
	}

	public ISelectorRole getSelectorRole(String selectedClass) {

		for (ISelectorRole iSelectorRole : iSelectorRoles) {
			Pattern pattern = Pattern.compile(iSelectorRole.getPattern());
			Matcher matcher = pattern.matcher(selectedClass);
			if (matcher.matches()) {
				return iSelectorRole;
			}
		}
		return null;
	}

	public void checkClassIntoElement(String selector, Element element) {
		try {
			if (selector.equals(".contentContainer table")) {
				System.out.println();
			}

			enteredSelector = selector;

			selector = prepareSelector(selector);

			ISelectorRole iSelectorRole = getSelectorRole(selector);
			if (iSelectorRole != null) {
				iSelectorRole.checkClass(selector, element);
			} else {
				cutFirstSelector(selector);
				IteratorWorker.iteration(element.children(), new IEvent() {
					@Override
					public void event(Element element) throws ParseExcpetion {
						checkClassIntoElement(element);
					}
				});
			}
		} catch (ParseExcpetion e) {
			System.out.println("FIND!!!! class =  " + e.getMessage());
			classParser.getClassesMapping().remove(e.getMessage());
		}
	}

	private void checkClassIntoElement(Element element) throws ParseExcpetion {
		try {
			ISelectorRole iSelectorRole = getSelectorRole(selector);
			if (iSelectorRole != null)
				iSelectorRole.checkClass(selector, element);
		} catch (ParseExcpetion e) {
			if (restOfCheckingSelector != null && !restOfCheckingSelector.isEmpty()) {
				cutFirstSelector(restOfCheckingSelector);
				checkClassIntoElement(e.getElement());
			} else {
				throw new ParseExcpetion(enteredSelector, e.getElement());
			}
		}
	}

	private void cutFirstSelector(String currentSelector) {

		String[] selectors = currentSelector.split("\\p{Blank}", 2);
		this.selector = selectors[0];
		if (selectors.length > 1) {
			this.restOfCheckingSelector = selectors[1];
			if (selectors[1].matches("^[\\>|\\|\\+\\~][^\\>]*")) {
				selectors = selectors[1].split("\\p{Blank}", 2);
				this.selector += selectors[0];
				if (selectors.length > 1)
					this.restOfCheckingSelector = selectors[1];
				else {
					this.restOfCheckingSelector = null;
				}
			}
		} else {
			this.restOfCheckingSelector = null;
		}

	}

	private String prepareSelector(String selector) {
		selector = selector.trim();
		while (selector.contains("  ")) {
			selector = selector.replace("  ", " ");
		}
		return selector;
	}
}
