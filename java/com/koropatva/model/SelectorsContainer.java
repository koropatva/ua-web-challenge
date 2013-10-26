package com.koropatva.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SelectorsContainer {
	private Set<String> cssTables = new HashSet<String>();

	private Set<String> selectors = new HashSet<String>();

	private Map<String, String> selectorMapping = new HashMap<String, String>();

	public Set<String> getCssTables() {
		return cssTables;
	}

	public void setCssTables(Set<String> cssTables) {
		this.cssTables = cssTables;
	}

	public Set<String> getSelectors() {
		return selectors;
	}

	public void setSelectors(Set<String> selectors) {
		this.selectors = selectors;
	}

	public Map<String, String> getSelectorMapping() {
		return selectorMapping;
	}

	public void setSelectorMapping(Map<String, String> selectorMapping) {
		this.selectorMapping = selectorMapping;
	}
}
