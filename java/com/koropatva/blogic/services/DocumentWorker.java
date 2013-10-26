package com.koropatva.blogic.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DocumentWorker {

	private String baseUrl;

	private String url;

	// Flag for show from where system will get info about site
	private boolean localSite;

	private Document document;

	public DocumentWorker(String url) {
		this(url, false);
	}

	public DocumentWorker(String url, boolean localSite) {
		this.url = url;
		this.localSite = localSite;
		if (!localSite) {
			baseUrl = "http://";
			this.url = this.url.replace(baseUrl, "");
			this.url = this.url.replace("www.", "");
			if (this.url.indexOf("/") >= 0) {
				this.baseUrl = baseUrl + "www."
						+ this.url.substring(0, this.url.indexOf("/"));
			} else {
				this.baseUrl = baseUrl + "www." + this.url;
			}
			this.url = "http://" + "www." + this.url;
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

	public BufferedReader getBufferedReader(String href) {
		try {
			href = generateUrlForCSSTable(href);

			Reader inputStream;
			if (localSite) {
				inputStream = new FileReader(new File(href));
			} else {
				URL hrefUrl = new URL(href);
				HttpURLConnection connection = (HttpURLConnection) hrefUrl
						.openConnection();
				connection.setConnectTimeout(7000);
				inputStream = new InputStreamReader(connection.getInputStream());
			}

			return new BufferedReader(inputStream);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
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

	public Document getDocument() {
		return document;
	}

	public String getUrl() {
		return url;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

}
