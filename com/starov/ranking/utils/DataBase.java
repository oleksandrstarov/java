package com.starov.ranking.utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.starov.ranking.model.Competition;
import com.starov.ranking.model.Runner;
import com.starov.ranking.view.HomeSceneController;
import com.starov.ranking.view.ImportStageOneController;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;



public class DataBase {
	public DataBase(){
		
	}
	
	public static Connection conn;
	public static Statement statmt;
	public static ResultSet resSet;
	public static String importedIDs;
	private static Date competitionDate;
	private static final double DEFAULT_POINTS = 10;
	public static double progress = 0.0;

	
	//connect
	public static void Conn() throws ClassNotFoundException, SQLException {
		   conn = null;
		   //Statement stmt = null;
		   Class.forName("org.sqlite.JDBC");
		   File f = new File("RankingSystemDB.s3db");
		   if(!f.exists()){
			   try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   }
		   conn = DriverManager.getConnection("jdbc:sqlite:RankingSystemDB.s3db");
		   //conn = DriverManager.getConnection("jdbc:sqlite:DB.db");
		 
			   
			   
		   System.out.println("Connected");
	 }
	
	public static void dropDB(){
		try {
			statmt = conn.createStatement();
			statmt.execute("DROP TABLE IF EXISTS RUNNERS");
			statmt.execute("DROP TABLE IF EXISTS RESULTS");
			statmt.execute("DROP TABLE IF EXISTS COMPETITIONS");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//RUNNERS
		
		
	}
	
	
	// create properties - always called to check if all the props are exists
	public static void CreateDB() throws ClassNotFoundException, SQLException{
	
		statmt = conn.createStatement();
		
		//RUNNERS
		ResultSet property = statmt.executeQuery("SELECT COUNT(*) AS total FROM sqlite_master WHERE type='table' AND name='RUNNERS'");
		if(property.getInt("total") == 0){
			//if empty == create propery and write/crear - to set the first value for REFERENCE coumn
			statmt.execute("CREATE TABLE if not exists 'RUNNERS' "
					+ "('REFERENCE' INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "'FULL_NAME' text NOT NULL, " //last name + " " + first name 
					+ "'DOB' text, "
					+ "'QUALIFICATION' text, "
					+ "'CLUB' text);");
			statmt.execute("INSERT INTO 'RUNNERS' ('REFERENCE', 'FULL_NAME') VALUES (1000, 'F'); ");
			statmt.execute("DELETE FROM 'RUNNERS'; ");
		}
		
		//COMPETITIONS
		property = statmt.executeQuery("SELECT COUNT(*) AS total FROM sqlite_master WHERE type='table' AND name='COMPETITIONS'");
		if(property.getInt("total") == 0){
			//if empty == create propery and write/crear - to set the first value for REFERENCE coumn
			statmt.execute("CREATE TABLE if not exists 'COMPETITIONS' "
					+ "('REFERENCE' INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "'DATE' text, "
					+ "'PLACE' text, "
					+ "'NAME' text, "
					+ "'FILE_NAME' text, "
					+ "'ID' INTEGER, "
					+ "'ORGANISER' text, "
					+ "'STATUS' text) ;");
			statmt.execute("INSERT INTO 'COMPETITIONS' ('REFERENCE') VALUES (1000); ");
			statmt.execute("DELETE FROM 'COMPETITIONS'; ");
		}
		
		//RESULTS
		property = statmt.executeQuery("SELECT COUNT(*) AS total FROM sqlite_master WHERE type='table' AND name='RESULTS'");
		if(property.getInt("total") == 0){
			//if empty == create propery and write/crear - to set the first value for REFERENCE coumn
			statmt.execute("CREATE TABLE if not exists 'RESULTS' "
					+ "('REFERENCE' INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "'RUNNER' INTEGER, "
					+ "'COMPETITION' INTEGER, "
					+ "'GROUP_NAME' text, "
					+ "'PLACE' INTEGER, "
					+ "'TIME' text, "
					+ "'NOTE' text, "
					+ "'TIME_AFTER' text, "
					+ "'GROUP_POINTS' REAL, "
					+ "'POINTS_ABS' REAL, "
					+ "'DATE' text) ;");
			statmt.execute("INSERT INTO 'RESULTS' ('REFERENCE') VALUES (1000); ");
			statmt.execute("DELETE FROM 'RESULTS'; ");
		}
		
		//System.out.println("Property created or exists");
	}
	
	public static String ReadStats(String dbProp) throws ClassNotFoundException, SQLException{
		resSet = statmt.executeQuery(dbProp + ";");
		//System.out.println("Value from db " + resSet.getInt("total"));
		return resSet.getInt("total") + "";
	}
	
	public static ObservableList<Competition> ReadImportedCompetitions()throws ClassNotFoundException, SQLException{
		ObservableList<Competition> competitions = FXCollections.observableArrayList();
		resSet = statmt.executeQuery("SELECT NAME, ID, DATE  FROM COMPETITIONS ORDER BY DATE DESC;");
		importedIDs = "|";
		while(resSet.next()){
			int id = resSet.getInt("ID");
			String  name = resSet.getString("NAME");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date = null;
			try {
				date = df.parse(resSet.getString("DATE"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Competition competition = new Competition(name, id, date);
			competitions.add(competition);
			//System.out.println( "ID = " + id + ", name = " + name );
	        importedIDs += id + "|";
		}
		System.out.println(importedIDs);
		return competitions;
	}
	
	
	public static double[] getTopBestPoints(String paramString){
		double[] rankings = new double[3];
		System.out.println(paramString);
		String todayMinusYear = Converter.convertDateToString(Converter.addMonthsToDate(new Date(), -12));
		try {
			resSet = statmt.executeQuery("SELECT REFERENCE FROM RUNNERS WHERE FULL_NAME IN(" + paramString + ");");
			int index = 0;
			String request = "";
			while(resSet.next()){
				if(request != "") request+= " UNION ALL ";
				request += "SELECT CASE WHEN COUNT(POINTS_ABS) = 0 THEN 300 ELSE ((SUM(POINTS_ABS) + (6 - COUNT(POINTS_ABS))*300)/6) END "
						+ "FROM RESULTS "
						+ "INNER JOIN RUNNERS on RUNNERS.REFERENCE = RUNNER "
						+ "WHERE RESULTS.REFERENCE IN( "
						+ "SELECT REFERENCE FROM RESULTS "
						+ "WHERE DATE > '"+todayMinusYear+"' AND RUNNER = "+resSet.getInt(1)+" "
						+ "ORDER BY POINTS_ABS ASC LIMIT 6) ";
			}
			if(request!=""){
				request += " ORDER BY 1 ASC LIMIT 3";
				resSet = statmt.executeQuery(request);
				while(resSet.next()){
					System.out.println(resSet.getDouble(1));
					rankings[index] = resSet.getDouble(1);
					index++;
				}
			}
			//IF NOT ENOUGHT SET DEFAULT
			for(;index < 3; index++){
				rankings[index]= DEFAULT_POINTS; // default value
			}
				
				
			
			
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return rankings;
	}
	
	private static int addCompetition (Competition competition){
		String date = Converter.convertDateToString(competition.getDate());
		competitionDate = competition.getDate();
		//System.out.println(competitionDate + " date!!!!");
		int reference = 0;
		try{
			statmt.execute("INSERT INTO 'COMPETITIONS' "
				+ "('DATE', 'PLACE', 'NAME', 'FILE_NAME', 'ID', 'ORGANISER', 'STATUS') "
				+ "VALUES ('"+ date +"', '', '"+ competition.getNameFromFile()+"', '" + competition.getName() + "', '"+ competition.getId()+"', '',''); ");
			
			resSet = statmt.executeQuery("SELECT REFERENCE FROM COMPETITIONS ORDER BY REFERENCE DESC LIMIT 1;");
			if(resSet.next()){
				reference = resSet.getInt(1);
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reference;
	}
	
	//add Runner and return new reference
	private static int addRunner(Runner runner){
		
		try {
			statmt.execute("INSERT INTO RUNNERS "
					+ "('FULL_NAME', 'DOB', 'QUALIFICATION', 'CLUB') "
					+ "VALUES ('"+ runner.getName().getValue()+"', '"+ runner.getBirthDate()+"', '" + runner.getQualification().getValue() + "', '"+runner.getClub().getValue()+"'); ");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int runnerReference = getRunnerReference(runner.getName().getValue());
		//create dummy results (6) for the date of competition and backward with step == one month
		if(competitionDate == null) competitionDate = new Date();
		//Date validTill = Converter.addMonthsToDate(competitionDate, 12);
		try {
			String request = "INSERT INTO RESULTS "
					+ "('GROUP_NAME', 'PLACE', 'TIME', 'NOTE', 'TIME_AFTER', 'GROUP_POINTS', 'POINTS_ABS', 'DATE', 'RUNNER', 'COMPETITION') "
					+ "VALUES ";
			for(int i = 0; i<6; i++){
				request += " ('', '0', '', 'Dummy rank', '', '', "
						+ "'"+DEFAULT_POINTS+"', '"+Converter.convertDateToString(Converter.addMonthsToDate(competitionDate, -1*i))+"', '"+runnerReference+"', '0')";
				
				if(i< 5)request += ", ";
				else request += ";";
				
			}
			statmt.execute(request);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return runnerReference;
	}
	
	private static int getRunnerReference(String fullName){
		int reference = 0;
		try {
			resSet = statmt.executeQuery("SELECT REFERENCE FROM RUNNERS WHERE FULL_NAME = '" +fullName+ "';");
			if(resSet.next()){
				reference = resSet.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return reference;
	}
	
	public static void addResults(ArrayList<Runner> runners, Competition competition, HomeSceneController controller){
		//save competition
		Converter.trace("start addResults()");
		int competitionReference =	addCompetition(competition);
	
		
		String date = Converter.convertDateToString(competition.getDate());
		int total = runners.size();
		
		progress = 0.0;
		Task<Void> task = new Task<Void>() {
			
	         @Override protected Void call() throws Exception {
	        	
	        	 for(int i = 0; i < runners.size(); i++){
	     			Runner runner = runners.get(i);
	     			int runnerReference = getRunnerReference(runner.getName().getValue());
	     			if(runnerReference == 0) runnerReference = addRunner(runner);
	     			addResult(runner, date, competitionReference, runnerReference);
	     			//System.out.println(total-- + " runners left...");
	     			progress = (i+1)/(double)total;	        		
	        		updateProgress(progress, 1.0);
	        		//fxProgress.setProgress(DataBase.progress);
	        		System.out.println(DataBase.progress + " from Home Scene " + controller.fxProgress.getProgress());
	        	} 
	        	 updateProgress(0, 1.0);
	         return null;
	         }
		};
		controller.fxProgress.progressProperty().bind(task.progressProperty());
		new Thread(task).start();
		
		
		
		
				Converter.trace("start addResults()");
	}
	
	private static void addResult(Runner runner, String date, int competitionReference, int runnerReference){
		try {
			statmt.execute("INSERT INTO RESULTS "
				+ "('GROUP_NAME', 'PLACE', 'TIME', 'NOTE', 'TIME_AFTER', 'GROUP_POINTS', 'POINTS_ABS', 'DATE', 'RUNNER', 'COMPETITION') "
				+ "VALUES ('"+ runner.getGroup().getValue()+"', "
							+ "'"+ runner.getPlace().getValue()+"', "
							+ "'1970-01-01 " + runner.getResultString() +"', "
							+ "'',"
							+ "'"+runner.getTimeAfter().getValue()+"', "
							+ "'0', "
							+ "'"+runner.getPoints()+"', "
							+ "'"+date+"', "
							+ "'"+runnerReference+"', "
							+ "'"+competitionReference+"' ); ");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	
	
	
	public static ObservableList<String> getGroups(){
		ObservableList<String>groups = FXCollections.observableArrayList();
		groups.add("All Groups");
		try {
			resSet = statmt.executeQuery("SELECT DISTINCT GROUP_NAME FROM RESULTS ORDER BY GROUP_NAME DESC;");
			
			while(resSet.next()){
				String group = resSet.getString(1);
				groups.add(group);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return groups;
		
	}
	
	public static ObservableList<Runner> getGroupRanking(String group){
		Converter.trace("start get group ranking");
		String todayMinusYear = Converter.convertDateToString(Converter.addMonthsToDate(new Date(), -12));
		String groupParam = "";
		ObservableList<Runner>runners = FXCollections.observableArrayList();
		try {
			resSet = statmt.executeQuery("SELECT REFERENCE FROM RUNNERS;");
			
			String request = "";
			if(group!= "") groupParam = "AND GROUP_NAME = '"+group+"' ";
			while(resSet.next()){
				if(request != "")request +=" UNION ALL ";
				
				request += "SELECT FULL_NAME, CLUB, "
						+ "CASE WHEN COUNT(POINTS_ABS) = 0 THEN 300 ELSE ((SUM(POINTS_ABS) + (6 - COUNT(POINTS_ABS))*300)/6) END, "
						+ "(SELECT DATE FROM RESULTS WHERE RUNNER = "+resSet.getInt(1)+" ORDER BY DATE DESC LIMIT 1) AS DATE "
						+ "FROM RESULTS "
						+ "INNER JOIN RUNNERS on RUNNERS.REFERENCE = RUNNER "
						+ "WHERE RESULTS.REFERENCE IN( "
						+ "SELECT REFERENCE FROM RESULTS "
						+ "WHERE DATE > '"+todayMinusYear+"' AND RUNNER = "+resSet.getInt(1)+" " +groupParam+ " "
						+ "ORDER BY POINTS_ABS ASC LIMIT 6)";
				
			}
			
			if(request!= ""){
				request += " ORDER BY 3 ASC ";
				resSet = statmt.executeQuery(request);
			}
			
			while(resSet.next()){
				if(resSet.getDouble(3)== 300 && group !="") continue;
				Runner runner = new Runner(resSet.getString(1), resSet.getString(2), group, resSet.getDouble(3), resSet.getString(4), resSet.getRow());
				runners.add(runner);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Converter.trace("end get group ranking");
		return runners;
		
	}
	
	public static ObservableList<String> getRunners(){
		ObservableList<String>runners = FXCollections.observableArrayList();
		try {
			resSet = statmt.executeQuery("SELECT FULL_NAME FROM RUNNERS ORDER BY FULL_NAME;");
			while(resSet.next()){
				runners.add(resSet.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return runners;
		
	}
	
	public static String[] getRunnerDetails(String name){
		String[] data = new String[4];
		try {
			resSet = statmt.executeQuery("SELECT REFERENCE, DOB, CLUB FROM RUNNERS WHERE FULL_NAME = '"+name+"';");
			if(resSet.next()){
				data[0] = resSet.getInt(1)+ "";
				data[1] = resSet.getString(2);
				data[2] = resSet.getString(3);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double[] points = getTopBestPoints("'" + name +"'");
		data[3] = points[0] + "";
		return data;
	}
	
	public static ObservableList<Runner> getRunnerResults(int reference){
		ObservableList<Runner>runners = FXCollections.observableArrayList();
		/*String competitionName,
		String group, double points, String date, int place,  String resultString,  String afterString*/
		try {
			resSet = statmt.executeQuery("SELECT NAME, TIME, TIME_AFTER, RESULTS.PLACE, POINTS_ABS, GROUP_NAME, RESULTS.DATE "
					+ "FROM RESULTS "
					+ "LEFT JOIN COMPETITIONS ON COMPETITIONS.REFERENCE = COMPETITION "
					+ "WHERE RUNNER = '" +reference+ "' ORDER BY RESULTS.DATE DESC;");
			while(resSet.next()){
				Runner runner = new Runner(resSet.getString("NAME"), 
						resSet.getString("GROUP_NAME"), 
						resSet.getDouble("POINTS_ABS"), 
						resSet.getString("DATE"),  
						resSet.getInt("PLACE"),
						resSet.getString("TIME"),  
						resSet.getString("TIME_AFTER"));
				runners.add(runner);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return runners;
	}
	
		// --------Закрытие--------
	public static void CloseDB() throws ClassNotFoundException, SQLException{
			
			
		resSet.close();
		statmt.close();
		conn.close();
			
		System.out.println("Connection closed");
	}
}
