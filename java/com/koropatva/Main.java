package com.koropatva;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.koropatva.blogic.services.ClassParser;
import com.koropatva.blogic.services.SelectorRoleFactory;

public class Main {

	public static void main(String[] args) throws Exception {

		// String url = "http://www.w3schools.com/cssref/css_selectors.asp";
		String url = "C:/Users/user/workspace/TestData/Pattern.htm";

		boolean localSite = true;

		Document doc = createDocument(url, localSite);

		ClassParser classParser = new ClassParser(url, localSite);

		classParser.fillClasses(doc);

		SelectorRoleFactory selectorRoleFactory = new SelectorRoleFactory(classParser);

		System.out.println("List of classes");
		int number = 0;
		for (String newClass : classParser.getClasses()) {
			number++;
			selectorRoleFactory.checkClassIntoElement(newClass, doc);
		}
		System.out.println("____________________________________-");

		number = 0;
		for (String newClass : classParser.getClassesMapping().keySet()) {
			number++;
			System.out.println(number + " " + newClass);
		}
	}

	private static Document createDocument(String url, boolean localSite) throws IOException {
		if (localSite)
			return Jsoup.parse(new File(url), "UTF-8", "");
		else
			return Jsoup.connect(url).get();
	}
}
