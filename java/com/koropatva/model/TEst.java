package com.koropatva.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.koropatva.blogic.services.SelectorRoleFactory;
import com.koropatva.blogic.services.selectors.ElementPlusElement;
import com.koropatva.blogic.services.selectors.MultipleClasses;
import com.koropatva.blogic.services.selectors.SimpleAttributeWhereValueStartWithCSS2;

public class TEst {

	public static void main(String[] args) {

		String result = "> asdfasdfasd";

		String parse = "    asdfaasdf    #asdfasd  >  asdfsd | sdfsdf".trim();
		while (parse.contains("  ")) {
			parse = parse.replace("  ", " ");
		}
		System.out.println(parse);

		for (String elem : parse.split("\\p{Blank}", 3)) {

			System.out.println(elem.matches("^[\\>|\\|\\+\\~][^\\>]*"));
			System.out.println(elem);

		}

		//
		// System.out.println(parse);
		// System.out.println(patt);
		// for (String cl : parse.split("\\.")) {
		// System.out.println(cl);
		// }
		//
		// System.out.println(matcher.matches());
	}
}
