package com.koropatva.blogic.services;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.koropatva.blogic.exceptions.ParseException;
import com.koropatva.model.SelectorsContainer;

public class MainWorker {

	private static final String	L				= "l";
	private static final String	W				= "w";
	private static final String	S				= "s";
	private static final String	SEPARATOR		= ">";

	private Map<String, String>	commands		= new HashMap<String, String>() {
													private static final long	serialVersionUID	= 1L;
													{
														put(W, "global(WWW) location of file for parsing");
														put(L, "local location of file for parsing");
														put(S, "parse site");
													}
												};

	private Set<String>			urlsForParsing	= new HashSet<String>();

	private boolean				localSite;

	private boolean				parseSite;

	public void parsing(String[] args) {
		if (notValidEnteredArgs(args)) {
			printHelp();
			return;
		}

		for (String url : urlsForParsing) {
			startParsing(url, localSite);
		}
	}

	private void startParsing(String url, boolean localSite) {

		try {
			SelectorContainerWorker selectorContainerWorker = new SelectorContainerWorker(url, localSite);

			SelectorsContainer selectorsContainer = selectorContainerWorker.fillSeparatorsContainer();

			SelectorRoleFactory selectorRoleFactory = new SelectorRoleFactory(selectorsContainer);

			for (String selector : selectorsContainer.getSelectors()) {
				selectorRoleFactory.checkClassIntoElement(selector, selectorContainerWorker.getDocument());
			}

			printResult(selectorsContainer, url);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private boolean notValidEnteredArgs(String[] args) {
		boolean urls = false;
		boolean locations = false;
		for (String arg : args) {
			if (arg.equalsIgnoreCase(W)) {
				localSite = false;
				locations = true;
			} else if (arg.equalsIgnoreCase(L)) {
				localSite = true;
				locations = true;
			} else if (arg.equalsIgnoreCase(S)) {
				parseSite = true;
			} else if (arg.equalsIgnoreCase(SEPARATOR)) {
				urls = true;
			} else if (urls) {
				if (localSite) {
					if (!new File(arg).canRead()) {
						System.out.println(String.format(
								"ATTENTION!!!\n Can't find file. \n Path '%s' is not correct.", arg));
						System.out.println();
						return true;
					}
				}
				urlsForParsing.add(arg);
			}
		}
		if (locations && urls)
			return false;
		return true;
	}

	private void printResult(SelectorsContainer selectorsContainer, String pageUrl) {
		System.out.println("____________PAGE_URL_FOR_PARSE______________");
		System.out.println(pageUrl);
		System.out.println();
		System.out.println("__________________RESULT____________________");
		System.out.println(">>>>");
		for (String cssTable : selectorsContainer.getCssTables()) {
			System.out.println();
			System.out.println("=====================");
			System.out.println(cssTable);
			System.out.println();
			for (String selector : selectorsContainer.getSelectorMapping().keySet()) {
				if (selectorsContainer.getSelectorMapping().get(selector).equals(cssTable)) {
					System.out.println(selector);
				}
			}
			System.out.println("=====================");
		}
		System.out.println("<<<<");
	}

	private void printHelp() {
		System.out.println("MANUAL");
		System.out.println(String.format("Main structure: list of attributes %s list of url(local path) for sites",
				SEPARATOR));
		System.out.println(String.format("One attribute of '%s' or '%s' is required!", L, W));
		System.out.println(String.format("It is not possible to use '%s', '%s' attributes together!", L, W));
		System.out.println("For example : ");
		System.out
				.println(String.format(
						"\t%s %s uawebchallenge.com http://docs.oracle.com/javase/6/docs/api/java/util/Map.html", W,
						SEPARATOR));
		System.out.println(String.format("\t%s %s file:///C:/Users/user/workspace/TestData/Pattern.htm", L, SEPARATOR));
		System.out.println();
		System.out.println("List of Attributes: ");
		for (String iterable : commands.keySet()) {
			System.out.println(String.format("%1$-10s%2$s", iterable, commands.get(iterable)));
		}

	}

}
