package com.koropatva.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.koropatva.blogic.services.selectors.SimpleAttribute;
import com.koropatva.blogic.services.selectors.SimpleAttributeWhereValueEndWithCSS3;
import com.koropatva.blogic.services.selectors.SimpleAttributeWhereValueStartWithCSS2;
import com.koropatva.blogic.services.selectors.SimpleAttributeWhereValueStartWithCSS3;

public class TEst {

	public static void main(String[] args) {

		String parse = "a[ name $= \"navbar\" ]";

		String patt = SimpleAttributeWhereValueEndWithCSS3.PATTERN;

		Pattern pattern = Pattern.compile(patt);
		Matcher matcher = pattern.matcher(parse);

		System.out.println(parse);
		System.out.println(patt);
		System.out.println(matcher.matches());
	}
}
