package com.starov.ranking.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javafx.beans.property.SimpleStringProperty;

public class Converter {
	private static Date date = new Date();
	
	public static double convertResultToMinutes(String result){
		if(result.indexOf(":") != -1){
			Date time = null;
			DateFormat df = new SimpleDateFormat("hh:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));
			try {
				time = df.parse(result);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return (double) time.getTime()/60000; // minutes
		}else if(result == "") return 0; // if empty
		else return 0; // if disq
	}
	
	public static String convertDateToString(Date date){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//df.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		
		return df.format(date);
		
	}
	
	
	
	public static Date addMonthsToDate(Date date, int months){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, months);
		date = cal.getTime();
		return date;
	}
	
	public static void trace(String from){
		Date newDate = new Date();
		double duration = (newDate.getTime() - date.getTime()) / 1000;
		System.out.println(convertDateToString(newDate) + " ("+duration+") " + from);
		date = newDate;
	}
	
	
	
}
