package com.starov.ranking.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Competition {
	private final int id;
	private final String link;
	private Date date;
	private final String name;
	private String nameFromFile;
	private int runners;
	private int groups;
	private int invalidRunners;
	
	
	public Competition(int id, String name,  String link){
		this.id = id;
		this.name = name;
		this.link = link;
		this.date = null;
	}
	
	public Competition(String name, int id, Date date){
		this.id = id;
		this.nameFromFile = name;
		this.name = name;
		this.link = null;
		this.date = date;
	
	}
	
	public int getId(){
		return id;
	}
	public String getName(){
		return name;
	}
	
	public String getLink(){
		return link;
	}
	
	public Date getDate(){
		return date;
	}
	
	public String toString(){
		if(nameFromFile != null && date != null){
			DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
			String dateString = df.format(date);
			
			return nameFromFile + " " + dateString;
		}
		if(nameFromFile != null) return nameFromFile;
		return name;
	}
	
	public void setRunnersTotal(int runners, int invalidRunners){
		this.runners = runners;
		this.invalidRunners = invalidRunners;
	}
	
	public void setGroupsTotal(int groups){
		this.groups = groups;
	}
	
	public void setDate(Date date){
		this.date = date;
		System.out.println(date + " = compDate");
	}
	public String getNameFromFile(){
		return nameFromFile;
	}
	public void setNameFromFile(String nameFromFile){
		this.nameFromFile = nameFromFile;
	}
	public int getGroupsTotal(){
		return groups;
	}
	public int getRunnersTotal(){
		return runners;
	}
}
