package com.koropatva.blogic.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.koropatva.model.Element;

public class URLParser {

	private static Logger logger = Logger.getLogger(URLParser.class);

	private Element element;

	private String webContent;

	public Element parseURL(String urlAddress) {
		logger.info("name CALLED");
		readWebsite(urlAddress);

		prepareBodyElement();

		parseWebContent(null, webContent);
		return element;
	}

	private void prepareBodyElement() {
		element = new Element(null, "body");
		webContent = webContent.substring(webContent.indexOf("<body"),
				webContent.indexOf("</body>"));
	}

	private void parseWebContent(Element parent, String parseWebContent) {
		logger.info("parseWebContent CALLED");
		if (parseWebContent != null && parseWebContent.length() > 0) {
			Element newChild = createNewElement(parent,
					getElementName(parseWebContent));
			parseWebContent = parseWebContent.substring(0,
					parseWebContent.indexOf("/>"));
			parseWebContent = parseWebContent.substring(parseWebContent
					.lastIndexOf(String.format("</%s", newChild.getName())));
			parseWebContent(newChild, parseWebContent);
		} else {
			return;
		}
	}

	private String getElementName(String parseWebContent) {
		logger.info("getElementName CALLED");
		if (parseWebContent.indexOf(" ") > 0) {
			return parseWebContent.substring(1, parseWebContent.indexOf(" "));
		} else {
			return parseWebContent.substring(1, parseWebContent.length() - 1);
		}

	}

	private Element createNewElement(Element parent, String name) {
		logger.info("creatnew eNewElement CALLED");
		boolean firstChild = false;
		if (element.getChildren() == null) {
			firstChild = true;
			element.setChildren(new ArrayList<Element>());
		}
		Element child = new Element(parent, name, firstChild);
		element.getChildren().add(child);
		return child;
	}

	private void readWebsite(String urlAddress) {
		BufferedReader bufferedReader = null;
		try {
			URL url = new URL(urlAddress);
			URLConnection urlConnection = url.openConnection();

			bufferedReader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));

			String inputString = new String();
			while ((inputString = bufferedReader.readLine()) != null) {
				webContent += inputString;
			}
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
	}

}
