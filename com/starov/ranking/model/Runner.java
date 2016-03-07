package com.starov.ranking.model;

import java.text.DecimalFormat;
import java.util.Date;

import com.starov.ranking.utils.Converter;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

//The entry from parsed result list
public class Runner {
	
	private String lastName;
	private String firstName;
	private String fullName;
	private String club;
	private int place;
	private String group;
	private String resultString;
	private String numberBib;
	private int controlSequence [];
	private int splitSequence [];
	private int resultSec;
	private int afterSec;
	private String qual;
	private String birthDate;
	private String startTime;
	private String afterString;
	private double points;
	private String latestDate;
	private String competitionName;
	
	
	
	public Runner(String lastName, String firstName, String club,
					String group, String qual, String birthDate,
					String resultString, String afterString, String numberBib,
					int place){
		this.lastName = capitalize(lastName);
		this.firstName = capitalize(firstName);
		this.fullName = this.lastName + " " + this.firstName;
		this.club = club.toUpperCase(); 
		this.group = group;
		this.qual = qual;
		this.birthDate = birthDate;
		this.resultString = resultString;
		this.afterString = afterString;
		this.numberBib = numberBib;
		this.place = place;
		//System.out.println(group + " " + lastName + " " + firstName + " " + club + " " + qual + " " + birthDate + " " + resultString + " " + place);
	}
	//reload constructor for result purposes
	public Runner(String fullName, String club,
			String group, double points, String date, int place){
		this.fullName = fullName;
		this.club = club;
		this.group = group;
		this.points = points;
		this.place = place;
		this.latestDate = date.substring(0, date.indexOf(" "));
		//System.out.println(latestDate);
		//System.out.println(group + " " + lastName + " " + firstName + " " + club + " " + qual + " " + birthDate + " " + resultString + " " + place);
	}
	
	public Runner(String competitionName,
			String group, double points, String date, int place,  String resultString,  String afterString){
		this.competitionName = competitionName;
		System.out.println("comp " + competitionName);
		this.resultString = resultString.indexOf(" ") > 0? resultString.substring(resultString.indexOf(" ") + 1, resultString.length()): "default";
		this.group = group;
		this.points = points;
		this.place = place;
		this.afterString = afterString;
		System.out.println(afterString);
		this.latestDate = date.substring(0, date.indexOf(" "));
		//System.out.println(latestDate);
		//System.out.println(group + " " + lastName + " " + firstName + " " + club + " " + qual + " " + birthDate + " " + resultString + " " + place);
	}
	public String getGroupString(){
		return group;
	}
	public StringProperty getGroup(){
		return new SimpleStringProperty(group);
	}
	public StringProperty getName(){
		return new SimpleStringProperty(fullName);
	}
	public StringProperty getQualification(){
		return new SimpleStringProperty(qual);
	}
	public StringProperty getResult(){
		return new SimpleStringProperty(resultString);
	}
	public String getResultString(){
		if(resultString.indexOf(":") != -1)return resultString;
		return "0:00:00";
	}
	
	
	public double getResultMinutes(){
		return Converter.convertResultToMinutes(resultString);
	}
	public double getTimeAfterMinutes(){
		return Converter.convertResultToMinutes(afterString);
	}
	public StringProperty getTimeAfter(){
		return new SimpleStringProperty(afterString);
	}
	public StringProperty getClub(){
		return new SimpleStringProperty(club);
	}
	public ObservableValue<Integer> getPlace(){
		return new SimpleIntegerProperty(place).asObject();
	}
	public StringProperty getPlaceString(){
		return new SimpleStringProperty(place+ "");
	}
	public StringProperty getLatestDate(){
		return new SimpleStringProperty(latestDate);
	}
	public StringProperty getCompetitionName(){
		return new SimpleStringProperty(competitionName);
	}
	
	
	
	public String getBirthDate(){
		return birthDate;
	}
	
	public void setPoints(double points){
		this.points = points;
	}
	public double getPoints(){
		return points;
	}
	public ObservableValue<Double> getPointsProperty(){
		DecimalFormat df = new DecimalFormat("0.00"); 
		double roundedPoints = Double.parseDouble(df.format(points).replaceAll(",", "."));
		return new SimpleDoubleProperty(roundedPoints).asObject(); 
	}
	
	void setSplits(int  [] codesArray, String [] splitsArrayString){
		controlSequence = new int [codesArray.length];
		controlSequence = codesArray.clone();
		
		splitSequence = new int [splitsArrayString.length];
		for(int i = 0; i < splitSequence.length; i++){
			String after = splitsArrayString[i];
			//System.out.println("after " + after);
			if(after.indexOf(":") != -1){
				int seconds = Integer.valueOf(after.substring(0, after.indexOf(":")))*60 + Integer.valueOf(after.substring(after.indexOf(":") +1, after.indexOf("(")>1?after.indexOf("("):after.length()));
				splitSequence[i] = seconds;
			}
		}		
	}
	
	private String capitalize(String line) {
		line = line.toLowerCase();
		return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}
	
	

}
