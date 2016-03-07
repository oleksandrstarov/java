package com.starov.ranking.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import com.starov.ranking.model.Competition;
import com.starov.ranking.model.Runner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//dedicate to SFR scheme
public class HTMLParser {
	private Competition competition;
	private int totalRunners;
	private ObservableList<Runner> runners = FXCollections.observableArrayList();
	
	
	
	
	public HTMLParser(Competition competition){
		this.competition = competition;
		String url = competition.getLink();
		
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			System.out.println("error");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		parseDocument(doc);
	}
	
	private void parseDocument(Document doc){
		int invalidRunners = 0;
		boolean isDefinitionChecked = false;
		Vector<Integer> columnOrder = new Vector<Integer>();
		final String labelArray [] = {"№ п/п", "Номер", "Фамилия", "Имя", "Г.р.", "Команда", "Разр.", "Результат", "Старт", "Место", "Дельта"};
		
		
		Elements groupsNames = doc.select("a[name]");
		int groupsNamesCounter = 1;
		for(Element groupNames : groupsNames){
			
			String groupsNamesString = groupNames.attr("name");
			if(groupsNamesString.equals("uppoint")) continue;
			//System.out.print(groupsNamesString + " ");
			groupsNamesCounter++;
		}
		competition.setGroupsTotal(groupsNamesCounter);
		//System.out.println(groupsNamesCounter + " from GroupNamesCounter");
		
		
		
		Element name = doc.select("h1").first();
		String compName = name.text();
		//System.out.println(compName);
		parseHeaderLine(compName);
		

		//System.out.println(protocolType);
		
		Elements groups = doc.select("table");
		//System.out.println(groups.size() + " groups amount");
		
		for(Element group: groups){
			String groupName = groupsNames.get(groups.indexOf(group) + 1).attr("name");
			// block with group data
				
			Elements lines = group.select("tr");
			
			for(Element line : lines){
				if(lines.indexOf(line) == 0){
			//this part will check headers once to be sure we will set values into correct places
					if(!isDefinitionChecked){
						Elements headersSet = line.select("th");
						for(int i = 0; i < headersSet.size(); i++){
							
							String valueString = headersSet.get(i).text();
							for(int j =0; j < labelArray.length; j++){
								if(labelArray[j].equals(valueString)){
									//"№ п/п", "Номер", "Фамилия", "Имя", "Г.р.", "Команда", "Разр.", "Результат", "Старт", "Место", "Дельта"
									columnOrder.add(j);
								}
							}
						}
						isDefinitionChecked = true;
					}
					continue;
				}
			
				
				String numberBib = null, 
						lastName = null, 
						firstName = null, 
						birthDate = null, 
						club  = null, 
						qualification = null, 
						result = null, 
						start = null,
						place = null,
						after = null;
				Elements dataSet = line.select("td"); //each nobr element have data
				totalRunners = totalRunners + 1;
				//System.out.println(dataSet.size() + " data set");
				for(int i = 0; i < dataSet.size(); i++){
					String valueString = dataSet.get(i).text();
					//System.out.println(valueString);
					//"№ п/п", "Номер", "Фамилия", "Имя", "Г.р.", "Команда", "Разр.", "Результат", "Старт", "Место", "Дельта"
					if(i < columnOrder.size()){
						switch (columnOrder.get(i)){
						case 0: break;
						case 1: numberBib = valueString;
								break;
						case 2: lastName = valueString;
								break;
						case 3: firstName = valueString;
								break;
						case 4: birthDate = valueString;
								break;
						case 5: club = valueString;
								break;
						case 6: qualification = valueString;
								break;
						case 7: result = valueString;
								break;
						case 8: start = valueString;
								break;
						case 9: place = valueString;
								break;
						case 10: after = valueString;
								break;
						default: break;
					} 	
						
					}
									
				}
				//System.out.println(place + " place");
				if((lastName + firstName).toLowerCase().indexOf("в/к")!= -1 ){
					invalidRunners++;
					continue;
					
				}  // skip invalid results
				
				int placeNumber = 0;
				if(place.length() > 0) placeNumber = Integer.valueOf(place);
				
				Runner runner = new Runner(lastName, firstName, club, groupName, qualification, birthDate, result, after, numberBib, placeNumber);
				runners.add(runner);
				
					
			}
		}
		/*for(int i = 0; i < columnOrder.size(); i++){
			System.out.println(columnOrder.get(i) + " 0rder " + labelArray[columnOrder.get(i)]);
		}*/
		System.out.println(totalRunners + " Total Runners from File" + runners.size() +"/" + invalidRunners);
		competition.setRunnersTotal(runners.size(), invalidRunners);
		
	}
	
	private void parseHeaderLine(String header){
		if(header.indexOf("промежуточные времена") != -1){
			System.out.println("SPLITS");
		} else {
			System.out.println("RESULTS");
		}
		if(header.indexOf(". ") != -1){
			String competitionName = header.substring(0, header.indexOf(". "));
			System.out.println(competitionName);
			competition.setNameFromFile(competitionName);
		}else{
			competition.setNameFromFile(header);
		}
		

		
		String tempString = header.substring(0, header.indexOf(", "));
		String competitionDistace = header.substring((tempString.lastIndexOf(". ")) +2, header.indexOf(", "));
		System.out.println(competitionDistace);
		String competitionDate = header.substring((header.indexOf(", ")) + 2, header.indexOf(". ", header.indexOf(", ")) );
		System.out.println(competitionDate);
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		try {
			Date date = df.parse(competitionDate);
			competition.setDate(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public ObservableList<Runner> getRunnersData(){
		return runners;
	}
	
	/*static void analizeHTML(Document doc){
		Elements groupsNames = doc.select("a[name]");
		int groupsNamesCounter = 1;
		for(Element groupNames : groupsNames){
			
			String groupsNamesString = groupNames.attr("name");
			if(groupsNamesString.equals("uppoint")) continue;
			System.out.print(groupsNamesString + " ");
			groupsNamesCounter++;
		}
		System.out.println();
		System.out.println(groupsNamesCounter + " from GroupNamesCounter");
		
		runnersArray = new Vector<Runner>();
		
		Element name = doc.select("h1").first();
		String compName = name.text();
		System.out.println(compName);
		parseHeaderLine(compName);
		

		System.out.println(protocolType);
		
		Elements groups = doc.select("table");
		System.out.println(groups.size() + " groups amount");
		for(Element group: groups){
		// block with group data
			
			Elements lines = group.select("tr");
			for(Element line : lines){
				
				if(lines.indexOf(line) == 0){
					getDefinitions(line);
					continue;
				}
				Elements dataSet = line.select("td"); //each nobr element have data 
				Runner runner = new Runner();
				for(int i = 0; i < dataSet.size(); i++){
					String valueString = dataSet.get(i).text();
					for(int j =0; j < indexArray.length; j++){
						if(indexArray[j] == i){
							//number, bibNumber, lastName, firstName, team, birthDate, qualification, result, start, place, delta
							valueArray[j] = valueString;
						}
					}
					if(controlsAmount != -1 && i >= splitIndex){
						splitsArray[i-splitIndex] = valueString;
						
					}
				}
				
				runner.setFullName(valueArray[3], valueArray[2]);
				runner.setClub(valueArray[4]);
				runner.setGroup(groupsNames.get(groups.indexOf(group) + 1).attr("name"));
				runner.setResult(valueArray[7]);
				runner.setNumber(valueArray[1]);
				runner.setAfter(valueArray[10]);
				runner.setPlace(valueArray[9]);
				runner.setBirthDate(valueArray[5]);
				runner.setQualification(valueArray[6]);
				runner.setStartTime(valueArray[8]);
				runner.output();
				
				
				if(controlCodesArray != null){
					runner.setSplits(controlCodesArray, splitsArray);
				}
				
				runnersArray.add(runner);
			}
			
			
			
		}
	}
	*/
}
