package com.koropatva.blogic.services;

import java.util.ListIterator;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.koropatva.blogic.ParseExcpetion;
import com.koropatva.blogic.interfaces.IEvent;

public class IteratorLogic {
	public static void iteration(Elements elements, IEvent iEvent) throws ParseExcpetion {
		ListIterator<Element> iterator = elements.listIterator();
		while (iterator.hasNext()) {
			iEvent.event(iterator.next());
		}
	}
}
