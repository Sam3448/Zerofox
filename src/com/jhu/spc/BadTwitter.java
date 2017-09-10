package com.jhu.spc;

import java.util.ArrayList;
import java.util.List;

public class BadTwitter {
	List<BadTwitter> list2 = new ArrayList<BadTwitter>();
	private int label;
	private String text;
	public int getLabel() {
		return label;
	}
	public String getText() {
		return text;
	}
	public void setLabel(int label) {
		this.label = label;
	}
}
