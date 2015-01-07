package com.brassorange.eventapp.model;

import java.io.Serializable;
import java.util.Date;

public class ProgramItem implements Serializable {
	public String id;
	public String title;
	public String summary;
	public String content;
	public Person presenter; 
	public Date date;
	public int durationMin;
}
