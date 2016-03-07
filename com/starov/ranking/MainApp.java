package com.starov.ranking;

import java.sql.SQLException;
import java.util.Date;

import com.starov.ranking.utils.Converter;
import com.starov.ranking.utils.DataBase;
import com.starov.ranking.utils.URLParser;
import com.starov.ranking.view.ApplicationScreen;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainApp extends Application {

    private Stage primaryStage;
    public ApplicationScreen applicationScreen;
  
    @Override
    public void start(Stage primaryStage) {
		Converter.trace("start handleDB()");
		handleDB();
		Converter.trace("application screen");
    	
        //Window creation
        applicationScreen = new ApplicationScreen(this, primaryStage);
        Converter.trace("url parser");
        URLParser urlParser = new URLParser("http://orienteering.kh.ua/Result/Index/tag/1/");
        Converter.trace("end url parser");
       
        applicationScreen.showHomeStage(urlParser.getAllResults());
    	primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
            	try {
					DataBase.CloseDB();
				} catch (ClassNotFoundException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                System.out.println("Stage is closing");
            }
        });  
        
        
    }
    
    public void handleDB(){
    	try{
    		DataBase.Conn();
    		//DataBase.dropDB();
    		DataBase.CreateDB();
    		
    	} catch (ClassNotFoundException | SQLException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	
    }
/*
    
    *//**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
      
    public static void main(String[] args) {
        launch(args);
    }
    
    
}