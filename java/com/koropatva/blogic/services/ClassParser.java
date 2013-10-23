package com.koropatva.blogic.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.koropatva.blogic.ParseExcpetion;
import com.koropatva.blogic.interfaces.IEvent;

public class ClassParser {

	private HashMap<String, String>	classesMapping	= new HashMap<String, String>();

	private Set<String>				classes			= new HashSet<String>();

	private String					baseUrl;

	private String					url;

	private boolean					localSite;

	public ClassParser(String url, boolean localSite) {
		this.url = url;
		this.localSite = localSite;
		if (!localSite) {
			baseUrl = "http://";
			if (url.startsWith(baseUrl)) {
				url = url.replace(baseUrl, "");
				if (!url.startsWith("www.")) {
					url = "www." + url;
				}
			}
			if (url.indexOf("/") >= 0) {
				this.baseUrl = baseUrl + url.substring(0, url.indexOf("/"));
			} else {
				this.baseUrl = baseUrl + url;
			}
		} else {
			baseUrl = url.substring(0, url.lastIndexOf("/"));
		}
	}

	public void fillClasses(Document doc) throws IOException, ParseExcpetion {

		IteratorLogic.iteration(doc.head().children(), new IEvent() {
			public void event(Element element) {
				try {
					String href = element.attr("href");
					if (element.nodeName().equals("link")
							&& (href.contains(".css") || element.attr("type").equals("text/css"))) {

						readAndFillClassesFromCSSTable(href);
					} else if (element.nodeName().equals("style") && element.attr("type").equals("text/css")) {
						parseClassesFromStyle(element.data());
					}

				} catch (IOException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		});
	}

	private void parseClassesFromStyle(String inputString) {
		while (inputString.contains("{") && inputString.contains("}")) {
			addClasses(inputString.substring(0, inputString.indexOf("{")), url);
			inputString = inputString.substring(inputString.indexOf("}") + 1);
		}
		System.out.println(inputString);
	}

	private void readAndFillClassesFromCSSTable(String href) throws MalformedURLException {
		try {
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
				if (style.isEmpty() && inputString.contains("{") && inputString.contains("}")) {
					addClasses(inputString.substring(0, inputString.indexOf("{")), href);
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

	private void addClasses(String newClasses, String key) {
		newClasses = newClasses.replaceAll("/\\*.*\\*/", "");

		for (String newClass : newClasses.split(",")) {
			String selectedNewClass = newClass.trim();
			if (baseClassesParser(selectedNewClass)) {
				classes.add(selectedNewClass);
				classesMapping.put(selectedNewClass, key);
			}
		}
	}

	private boolean baseClassesParser(String newClass) {
		if (newClass.equals("*")) {
			return false;
		}
		return true;
	}

	public Set<String> getClasses() {
		return classes;
	}

	public HashMap<String, String> getClassesMapping() {
		return classesMapping;
	}
}
