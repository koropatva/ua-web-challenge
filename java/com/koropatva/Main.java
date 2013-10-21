package com.koropatva;

import com.koropatva.blogic.services.URLParser;

public class Main {

	public static void main(String[] args) {
		URLParser urlParser = new URLParser();
		urlParser.parseURL("https://www.google.com.ua");
	}
}
