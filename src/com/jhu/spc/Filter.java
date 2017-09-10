package com.jhu.spc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class Filter {
	
	private String[] keywords = {"tourism", "conference", "business trip", "sightseeing", "happy", "great day",
			"trip", "visiting", "visit", "tour", "touring", "flight", "journey", "great time", "exited", "show", "concert",
			"game", "news" };
	
	private final String labels = "labels.txt";
	
	public void textfilter(String[] status) {
		if(status[2].equals("1")){
			for(String keyword : keywords){
				if(status[1].toLowerCase().contains(keyword)){
					status[2] = "0";
					break;
				}
			}
		}
		
		System.out.println(status[0] + ": " + status[1] + " label: " + status[2]);
		
		try {
			FileWriter fw = new FileWriter(labels, true);
			BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter out = new PrintWriter(bw);
		    out.println(status[0] + ": " + status[1] + " label: " + status[2]);
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}