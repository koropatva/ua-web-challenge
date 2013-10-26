package com.koropatva.blogic.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;

import org.jsoup.nodes.Element;

import com.koropatva.blogic.exceptions.ParseException;
import com.koropatva.blogic.interfaces.IEvent;
import com.koropatva.model.SelectorsContainer;

public class SelectorContainerWorker {

	private SelectorsContainer selectorsContainer = new SelectorsContainer();

	private DocumentWorker documentWorker;

	public SelectorContainerWorker(DocumentWorker documentWorker) {
		this.documentWorker = documentWorker;
	}

	public SelectorsContainer fillSeparatorsContainer() throws IOException,
			ParseException {
		// Find all link elements on the page and try to get classes from it
		IteratorWorker.iteration(
				documentWorker.getDocument().head().children(), new IEvent() {
					public void event(Element element) {
						try {
							String href = element.attr("href");
							if (element.nodeName().equals("link")
									&& (href.contains(".css") || element.attr(
											"type").equals("text/css"))) {

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
			addClasses(inputString.substring(0, inputString.indexOf("{")),
					documentWorker.getUrl());
			inputString = inputString.substring(inputString.indexOf("}") + 1);
		}
		System.out.println(inputString);
	}

	private void fillClassesFromCSSTable(String href)
			throws MalformedURLException {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = documentWorker.getBufferedReader(href);

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
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
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

}
