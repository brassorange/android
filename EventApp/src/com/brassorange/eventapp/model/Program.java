package com.brassorange.eventapp.model;

import java.util.ArrayList;

public class Program {
	public String splashUrl;
	public String id;
	public String name;
	public ArrayList<ProgramItem> programItems;
	
	public Program() {
		programItems = new ArrayList<ProgramItem>();
	}

}
