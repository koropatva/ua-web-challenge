package com.koropatva.model;

import java.io.Serializable;
import java.util.List;

public class Element implements Serializable {

	private static final long serialVersionUID = 1L;

	public Element(Element parent, String name) {
		this(parent, name, false);		
	}
	
	public Element(Element parent, String name, boolean firstChild) {
		this.parent = parent;
		this.name = name;
		this.firstChild = firstChild;
	}

	private String name;

	private Element parent;

	private List<Element> children;

	private boolean firstChild;

	private List<Attribute> attributes;

	public Element getParent() {
		return parent;
	}

	public void setParent(Element parent) {
		this.parent = parent;
	}

	public List<Element> getChildren() {
		return children;
	}

	public void setChildren(List<Element> children) {
		this.children = children;
	}

	public boolean isFirstChild() {
		return firstChild;
	}

	public void setFirstChild(boolean firstChild) {
		this.firstChild = firstChild;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
