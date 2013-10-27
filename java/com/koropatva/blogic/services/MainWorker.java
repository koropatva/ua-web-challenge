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

	private static final String L = "l";
	private static final String W = "w";
	private static final String S = "s";
	private static final String D = "d";
	private static final String N = "n";
	private static final String SEPARATOR = ">";

	private Map<String, String> commands = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put(W, "global(WWW) location of site(s) for parsing");
			put(L, "local location of file(s) for parsing");
			put(S,
					String.format(
							"parse site. Can use one of next inner attributes : %s - depth; %s - page number. "
									+ "Attribute '%s' without attributes will parse all pages on the site.",
							D, N, S));
		}
	};

	private Set<String> urlsForParsing = new HashSet<String>();

	private boolean localSite;

	private boolean parseSite;

	private Integer depth;

	private Integer pageNumber;

	public void parsing(String[] args) {
		if (parseEnteredArgs(args)) {
			printHelp();
			return;
		}

		if (parseSite) {
			SiteParser siteParser = new SiteParser();
			for (String url : urlsForParsing) {
				System.out
						.println(String.format("Scanning site '%s' ...", url));
				Set<String> pages = siteParser.fillSeparatorsContainer(url);
				int count = 0;
				for (String page : pages) {
					count++;
					if (pageNumber != null && pageNumber < count)
						break;
					if (depth != null && depth < getDepthOfPage(page))
						continue;
					System.out.println(String.format("Parsing page '%s' ...",
							page));
					DocumentWorker documentWorker = new DocumentWorker(page,
							localSite);
					startParsing(documentWorker);
					System.out.println();
					System.out.println();
				}
			}
		} else {
			for (String url : urlsForParsing) {
				DocumentWorker documentWorker = new DocumentWorker(url, localSite);
				startParsing(documentWorker);
			}
		}
	}

	private int getDepthOfPage(String url) {
		url = url.replace("http://", "");
		int count = 0;
		for (String symbol : url.split("")) {
			if (symbol.equals("/")) {
				count++;
			}
		}
		return count;
	}

	private void startParsing(DocumentWorker documentWorker) {
		try {
			SelectorContainerWorker selectorContainerWorker = new SelectorContainerWorker(
					documentWorker);

			SelectorsContainer selectorsContainer = selectorContainerWorker
					.fillSeparatorsContainer();

			SelectorRoleFactory selectorRoleFactory = new SelectorRoleFactory(
					selectorsContainer);

			for (String selector : selectorsContainer.getSelectors()) {
				selectorRoleFactory.checkClassIntoElement(selector,
						documentWorker.getDocument());
			}

			printResult(selectorsContainer, documentWorker.getUrl());
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private boolean parseEnteredArgs(String[] args) {
		boolean urls = false;
		boolean locations = false;
		for (String arg : args) {
			if (arg.equalsIgnoreCase(W)) {
				localSite = false;
				locations = true;
			} else if (arg.equalsIgnoreCase(L)) {
				localSite = true;
				locations = true;
			} else if (arg.startsWith(S)) {
				parseSite = true;
				if (arg.contains("("))
					if (arg.substring(2, 3).equalsIgnoreCase(N)) {
						depth = null;
						pageNumber = Integer.parseInt(arg.substring(3,
								arg.length() - 1));
					} else if (arg.substring(2, 3).equalsIgnoreCase(D)) {
						pageNumber = null;
						depth = Integer.parseInt(arg.substring(3,
								arg.length() - 1));
					}
			} else if (arg.equalsIgnoreCase(SEPARATOR)) {
				urls = true;
			} else if (urls) {
				if (localSite) {
					if (!new File(arg).canRead()) {
						System.out
								.println(String
										.format("ATTENTION!!!\n Can't find file. \n Path '%s' is not correct.",
												arg));
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

	private void printResult(SelectorsContainer selectorsContainer,
			String pageUrl) {
		System.out
				.println("_____________________________________________PAGE_URL_FOR_PARSE___________________________________________");
		System.out.println("                          " + pageUrl);
		System.out.println();
		System.out
				.println("__________________________________________________RESULT__________________________________________________");
		System.out.println(">>>>");
		for (String cssTable : selectorsContainer.getCssTables()) {
			System.out.println();
			System.out.println("=====================");
			System.out.println(cssTable);
			System.out.println();
			for (String selector : selectorsContainer.getSelectorMapping()
					.keySet()) {
				if (selectorsContainer.getSelectorMapping().get(selector)
						.equals(cssTable)) {
					System.out.println(selector);
				}
			}
			System.out.println("=====================");
		}
		System.out.println("<<<<");
	}

	private void printHelp() {
		System.out.println("MANUAL");
		System.out
				.println(String
						.format("Main structure: list of attributes %s list of url(local path) for sites",
								SEPARATOR));
		System.out.println(String.format(
				"One attribute of '%s' or '%s' is required!", L, W));
		System.out.println(String.format(
				"It is not possible to use '%s', '%s' attributes together!", L,
				W));
		System.out.println("For example : ");
		System.out
				.println(String
						.format("\t%s %s uawebchallenge.com http://www.oracle.com/index.html ---> will parse list of sites",
								W, SEPARATOR));
		System.out.println(String.format(
				"\t%s %s uawebchallenge.com ---> will parse current page", W,
				SEPARATOR));
		System.out
				.println(String
						.format("\t%s %s file:///C:/Users/user/workspace/TestData/Pattern.htm ---> will parse current page on local machine",
								L, SEPARATOR));
		System.out
				.println(String
						.format("\t%s %s(%s4) %s uawebchallenge.com  ---> will parse first 4 pages(what will find) on current site",
								W, S, N, SEPARATOR));
		System.out
				.println(String
						.format("\t%s %s(%s2) %s uawebchallenge.com ---> will parse all pages in the site, where depth for scanning equals 2. ",
								W, S, D, SEPARATOR));
		System.out
				.println(String
						.format("\t%s %s %s uawebchallenge.com ---> will parse all pages on the site",
								W, S, SEPARATOR));
		System.out.println();
		System.out.println("List of Attributes: ");
		for (String iterable : commands.keySet()) {
			System.out.println(String.format("%1$-10s%2$s", iterable,
					commands.get(iterable)));
		}

	}

}
