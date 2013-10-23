package com.koropatva.blogic.services;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;

import com.koropatva.blogic.ParseExcpetion;
import com.koropatva.blogic.interfaces.ISelectorRole;
import com.koropatva.blogic.services.selectors.ElementMoreElement;
import com.koropatva.blogic.services.selectors.SimpleAttribute;
import com.koropatva.blogic.services.selectors.SimpleAttributeWhereValueContainCSS2;
import com.koropatva.blogic.services.selectors.SimpleAttributeWhereValueContainCSS3;
import com.koropatva.blogic.services.selectors.SimpleAttributeWhereValueEndWithCSS3;
import com.koropatva.blogic.services.selectors.SimpleAttributeWhereValueEqual;
import com.koropatva.blogic.services.selectors.SimpleAttributeWhereValueStartWithCSS2;
import com.koropatva.blogic.services.selectors.SimpleAttributeWhereValueStartWithCSS3;
import com.koropatva.blogic.services.selectors.SimpleClass;
import com.koropatva.blogic.services.selectors.SimpleElement;
import com.koropatva.blogic.services.selectors.SimpleId;

public class SelectorRoleFactory {

	private static Set<ISelectorRole>	iSelectorRoles;
	static {
		iSelectorRoles = new HashSet<ISelectorRole>();
		iSelectorRoles.add(new SimpleClass());
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
	}

	private ClassParser					classParser;

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

	public void checkClassIntoElement(String newClass, Element element) {
		try {
			ISelectorRole iSelectorRole = getSelectorRole(newClass);
			if (iSelectorRole != null)
				iSelectorRole.checkClass(newClass, element);
		} catch (ParseExcpetion e) {
			System.out.println("FIND!!!! class =  " + e.getMessage());
			classParser.getClassesMapping().remove(newClass);
		}

	}
}
