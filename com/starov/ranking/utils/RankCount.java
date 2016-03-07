package com.starov.ranking.utils;

import java.util.ArrayList;
import com.starov.ranking.model.Competition;
import com.starov.ranking.model.Runner;
import javafx.collections.ObservableList;

public class RankCount {

	public static ArrayList<Runner> rankCount(ObservableList<Runner>runnersGeneral){
		//remove disq
		ArrayList <Runner> runners = new ArrayList<Runner>();
		System.out.println(runnersGeneral.size() + " before" );
		for(Runner runner: runnersGeneral){
			if(runner.getResultMinutes() < 1){
				continue;
			}
			runners.add(runner);
			
		}
		System.out.println(runners.size() + " after" );
		
		//split into 2 dimentional array		
		System.out.println(runners.size() + " from rank count");
		ArrayList<ArrayList<Runner>> runnersArray = new ArrayList<ArrayList<Runner>>();
		ArrayList<Runner> arrayByGroups = new ArrayList<Runner>();
		ArrayList<Runner> rankedRunners = new ArrayList<Runner>();
		String previousGroup = runners.get(0).getGroupString(); //first group
		for(int i = 0; i < runners.size(); i++){
			System.out.println(i + " " +runners.get(i).getGroupString());
			//split to groups to count ranking
			String currentGroup = runners.get(i).getGroupString();
			if(i==0){
				arrayByGroups = new ArrayList<Runner>();	
			}
			if(currentGroup != previousGroup){
				runnersArray.add(arrayByGroups);
				arrayByGroups = new ArrayList<Runner>();	
			}
			arrayByGroups.add(runners.get(i));
			previousGroup = currentGroup;
			//last group adding
			if(i+1 == runners.size()) runnersArray.add(arrayByGroups);
		}
		int afterGrouping = 0;
		for(int i = 0; i< runnersArray.size(); i++){
			System.out.println(runnersArray.get(i).size() +" " + runnersArray.get(i).get(0).getGroupString());
			afterGrouping += runnersArray.get(i).size();
		
		}
		System.out.println(afterGrouping + " size");
		
		System.out.println(runnersArray.size() + " size groups");
		int ignoredPersons = 0;
		int invalidTime = 0;
		int count = 0;
		for(int i = 0; i< runnersArray.size(); i++){
			if(runnersArray.get(i).size() < 3){
				//exclude groups where less than 3 runners
				ignoredPersons += runnersArray.get(i).size();
				continue;
			}			
			
			double averageTime = (runnersArray.get(i).get(0).getResultMinutes() +
					runnersArray.get(i).get(1).getResultMinutes() +
					runnersArray.get(i).get(2).getResultMinutes())/3;
			
			double averageTimeLimit = runnersArray.get(i).get(0).getResultMinutes()*1.1;
			if(averageTimeLimit < averageTime){
				averageTime = averageTimeLimit;
			}
				
			//System.out.println("Avarage time " + avarageTime);
			double averagePoints = 0;
			//for each runner - set points and add to List
			for(int j = 0; j < runnersArray.get(i).size(); j++){
				if(j == 0) averagePoints = getAvaragePoints(runnersArray.get(i));
				double result = runnersArray.get(i).get(j).getResultMinutes();
				double points = countPoints(result,averageTime, averagePoints,75);
				
				runnersArray.get(i).get(j).setPoints(points);
				rankedRunners.add(runnersArray.get(i).get(j));
				System.out.println(count++  +" " + runnersArray.get(i).get(j).getName().getValue() + " " + points + " " + runnersArray.get(i).get(j).getResultMinutes());
			}
			
		}
		System.out.println(ignoredPersons + " small groups," + invalidTime + " no result;");
		return rankedRunners;
		
		
	}
	
	private static double getAvaragePoints(ArrayList<Runner> groupSet){
		String requestParams ="";
		for(int i = 0; i < groupSet.size(); i++){
			requestParams += "'" + groupSet.get(i).getName().getValue()+ "'";
			if(i != groupSet.size()-1) requestParams += ", ";
		}
		//System.out.println(requestParams);
		double[] pointsArray = DataBase.getTopBestPoints(requestParams);
		double points = 0;
		for(int i = 0; i < pointsArray.length; i++){
			 points += pointsArray[i];
			
		}		
		return points/3;
	}
	
	private static double countPoints(double runnerTime, double totalTime, double totalPoints, int correlation){
		//Points (P) = (Time - (TM - PM /  KK)) õ KK
		//KK  =  (75 + PM) / TM)
		//limit is 300
		double correlationValue = (correlation + totalPoints)/totalTime;
		double points = (runnerTime - (totalTime - totalPoints/correlationValue))*correlationValue;
		if(points > 300)points = 300;
		return points;
	}
	
}
