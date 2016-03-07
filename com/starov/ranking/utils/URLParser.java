package com.starov.ranking.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.starov.ranking.model.Competition;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class URLParser {
	

	private ObservableList<Competition> competitions = FXCollections.observableArrayList();
	private int id;
	private String name;
	private String date;
	private String link;
	
	
	public URLParser(String string){
		Document doc = null;
		try {
			doc = Jsoup.connect(string).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		parseDocument(doc);
	
	}
	
	private void parseDocument(Document doc){
		//get part for results
		String importedIDs = DataBase.importedIDs;
		
		Element resultSection = doc.getElementsByClass("main-bg").first();
		resultSection = resultSection.select("table").first();
		Elements resultsBlocks = resultSection.select("a[target]");
		System.out.println("Total found: " + resultsBlocks.size());
		int counterTotal = 0;
		int counterNew = 0;
		for(Element part : resultsBlocks){
			
			link = part.attr("href");
			
			//check extension
			if(!checkExtension(link)) continue;
			counterTotal++;
			//if passed - continue
			name = part.text();
			//System.out.println(name);
			name = parseName(name);
			id = parseCompetitionId(link);
			if(importedIDs.indexOf("|" + id + "|") == -1){
				Competition competition = new Competition(id, name, link);
				competitions.add(competition);
				counterNew++;
			}
			
			
			
		}
		System.out.println("Total : " + resultsBlocks.size()+"/"+ counterTotal + "/" + counterNew);
		
		
	}
	

	public ObservableList<Competition> getAllResults(){
		return competitions;
	}
	
	private int parseCompetitionId(String link){
		//<a href="http://orienteering.kh.ua/images/events/416_Rezultati-trenirovochnogo-starta-KSO-Kompas..htm" 
		//target="_blank">
		//Результаты тренировочного старта КСО Компас..htm</a>
		
		int underlineIndex = link.indexOf("_");
		String linkSubstring = link.substring(0, underlineIndex);
		int lastSlashIndex = linkSubstring.lastIndexOf("/");
		String stringId = link.substring(lastSlashIndex + 1, underlineIndex);
		int parsedId = 0;
		try{
			parsedId = Integer.parseInt(stringId);
		}catch(NumberFormatException e){
			return 0;
		}
		return parsedId;
	}
	
	private Boolean checkExtension(String link){
		Boolean result = link.endsWith(".htm");
		return result;
	}
	
	private String parseName(String name){
		if(name.endsWith(".htm")){ 
			int extensionIndex = name.lastIndexOf(".");
			name = name.substring(0, extensionIndex - 1);
		}
		if(name.endsWith(".")) name = name.substring(0, name.length() - 1);
		return name;
	}
}
