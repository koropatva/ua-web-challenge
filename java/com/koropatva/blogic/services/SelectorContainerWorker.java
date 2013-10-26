package com.koropatva.blogic.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.koropatva.blogic.exceptions.ParseException;
import com.koropatva.blogic.interfaces.IEvent;
import com.koropatva.model.SelectorsContainer;

public class SelectorContainerWorker {

	private SelectorsContainer selectorsContainer = new SelectorsContainer();

	private String baseUrl;

	private String url;

	private Document document;

	// Flag for show from where system will get info about site
	private boolean localSite;

	public SelectorContainerWorker(String url, boolean localSite) {
		this.url = url;
		this.localSite = localSite;
		if (!localSite) {
			baseUrl = "http://";
			if (!this.url.startsWith(baseUrl)) {
				if (!this.url.startsWith("www.")) {
					this.url = "www." + this.url;
				}
				this.url = baseUrl + this.url;
			}
			if (this.url.indexOf("/") >= 0) {
				this.baseUrl = baseUrl + this.url.substring(0, this.url.indexOf("/"));
			} else {
				this.baseUrl = baseUrl + this.url;
			}
		} else {
			baseUrl = this.url.substring(0, this.url.lastIndexOf("/"));
		}

		fillDocument();
	}

	private void fillDocument() {
		try {
			if (localSite)
				this.document = Jsoup.parse(new File(url), "UTF-8", "");
			else
				this.document = Jsoup.connect(url).get();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public SelectorsContainer fillSeparatorsContainer() throws IOException,
			ParseException {
		// Find all link elements on the page and try to get classes from it
		IteratorWorker.iteration(document.head().children(), new IEvent() {
			public void event(Element element) {
				try {
					String href = element.attr("href");
					if (element.nodeName().equals("link")
							&& (href.contains(".css") || element.attr("type")
									.equals("text/css"))) {

						fillClassesFromCSSTable(href);
					} else if (element.nodeName().equals("style")
							&& element.attr("type").equals("text/css")) {
						fillClassesFromStyleElement(element.data());
					}

				} catch (IOException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		});
		return selectorsContainer;
	}

	private void fillClassesFromStyleElement(String inputString) {
		// Cut style element and take all classes(separators)
		while (inputString.contains("{") && inputString.contains("}")) {
			addClasses(inputString.substring(0, inputString.indexOf("{")), url);
			inputString = inputString.substring(inputString.indexOf("}") + 1);
		}
		System.out.println(inputString);
	}

	private void fillClassesFromCSSTable(String href)
			throws MalformedURLException {
		try {
			href = generateUrlForCSSTable(href);

			Reader inputStream;
			if (localSite) {
				inputStream = new FileReader(new File(href));
			} else {
				URL hrefUrl = new URL(href);
				inputStream = new InputStreamReader(hrefUrl.openStream());
			}
			BufferedReader bufferedReader = new BufferedReader(inputStream);

			String inputString;
			String style = "";
			while ((inputString = bufferedReader.readLine()) != null) {
				if (style.isEmpty() && inputString.contains("{")
						&& inputString.contains("}")) {
					addClasses(
							inputString.substring(0, inputString.indexOf("{")),
							href);
				} else {
					style += inputString;
					if (style.contains("{") && style.contains("}")) {
						addClasses(style.substring(0, style.indexOf("{")), href);
						style = style.substring(style.indexOf("}") + 1);
					}
				}
			}
		} catch (IOException e) {
		}
	}

	private String generateUrlForCSSTable(String href) {
		if (href.startsWith("/")) {
			href = baseUrl + href;
		}
		if (href.startsWith("./")) {
			href = baseUrl + href.replaceFirst(".", "");
		}
		if (href.startsWith("../")) {
			String newUrl = new String(url);
			newUrl = newUrl.substring(0, newUrl.lastIndexOf("/"));
			while (href.startsWith("../")) {
				href = href.substring(3);
				newUrl = newUrl.substring(0, newUrl.lastIndexOf("/"));
			}
			href = newUrl + "/" + href;
		}
		return href;
	}

	private void addClasses(String newClasses, String cssTable) {
		// Remove all annotations from class
		newClasses = newClasses.replaceAll("/\\*.*\\*/", "");

		for (String newClass : newClasses.split(",")) {
			String selectedNewClass = newClass.trim();
			// If true class used in any case, we can miss it
			if (baseClassesParser(selectedNewClass)) {
				// Add class for list of checking
				selectorsContainer.getCssTables().add(cssTable);
				selectorsContainer.getSelectors().add(selectedNewClass);
				selectorsContainer.getSelectorMapping().put(selectedNewClass,
						cssTable);
			}
		}
	}

	private boolean baseClassesParser(String newClass) {
		if (newClass.equals("*")) {
			return false;
		}
		if (newClass.equals(":root")) {
			return false;
		}
		return true;
	}

	public Document getDocument() {
		return document;
	}

}
