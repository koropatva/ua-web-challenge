package com.koropatva.blogic.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Element;

import com.koropatva.blogic.exceptions.ParseException;
import com.koropatva.blogic.interfaces.IEvent;

public class SiteParser implements IEvent {
	public Map<String, Boolean> urls = new HashMap<String, Boolean>();

	private DocumentWorker documentWorker;

	public Set<String> fillSeparatorsContainer(String url) {
		DocumentWorker dW = new DocumentWorker(url);
		if (dW.getUrl().endsWith("/")) {
			urls.put(dW.getUrl().substring(0, dW.getUrl().length() - 1), false);
		} else {
			urls.put(dW.getUrl(), false);
		}

		while (urls.containsValue(false)) {
			for (String currentUrl : urls.keySet()) {
				try {
					if (!urls.get(currentUrl)) {
						urls.put(currentUrl, true);
						documentWorker = new DocumentWorker(currentUrl);

						event(documentWorker.getDocument().body());

						break;
					}
				} catch (ParseException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}

		return urls.keySet();
	}

	public void event(Element element) throws ParseException {

		String newPage = null;
		if (element.nodeName().equals("a")) {
			String href = element.attr("href");
			if (href.startsWith("/")) {
				newPage = documentWorker.getBaseUrl() + href;
			} else if (href.startsWith(documentWorker.getBaseUrl())) {
				newPage = href;
			}
		}
		if (newPage != null) {
			if (newPage.endsWith("/"))
				newPage = newPage.substring(0, newPage.length() - 1);
			if (!urls.containsKey(newPage))
				urls.put(newPage, false);
		}

		if (element.children() != null) {
			IteratorWorker.iteration(element.children(), this);
		}
	}

}
