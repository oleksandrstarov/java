package com.starov.ranking.view;

import java.sql.SQLException;
import java.util.ArrayList;

import com.starov.ranking.model.Competition;
import com.starov.ranking.model.Runner;
import com.starov.ranking.utils.DataBase;
import com.starov.ranking.utils.HTMLParser;
import com.starov.ranking.utils.RankCount;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;

public class HomeSceneController {

	private ApplicationScreen applicationScreen;
	 	
	//Fields from screens
	@FXML
	private CheckBox fxQuickImport;
	
	@FXML
	private ListView<Competition> fxAvailableCompetitions;
	
	@FXML
	private ListView<Competition> fxLatestImported;
	
	@FXML
	private Label fxCompetitionsInDB, fxRunnersInDB, fxGroupsInDB;
	
	private ObservableList<Competition> competitions = FXCollections.observableArrayList();
	
	@FXML
	public ProgressBar fxProgress;

	
	private Competition selectedValue;

	//Import button on home scene
	@FXML
	private void doImportStageOne() {
		// if Quick import - do not show any screens just import and show home screen
		if(selectedValue == null){
			
	        Alert alert = new Alert(AlertType.WARNING);
	        alert.initOwner(applicationScreen.getMainApp().getPrimaryStage());
	        alert.setTitle("No Selection");
	        alert.setHeaderText("No Competition Selected");
	        alert.setContentText("Please select a competition to import.");

	        alert.showAndWait();
		}else{
			HTMLParser htmlParser = new HTMLParser(selectedValue);
			ObservableList<Runner> runners  = htmlParser.getRunnersData();
			
			if(fxQuickImport.isSelected()) {
				
				System.out.println("Quick Import");
				importData(RankCount.rankCount(runners));

				//DataBase.addResults(RankCount.rankCount(runners), selectedValue);
			    
				
				
				
				
			}else{
				applicationScreen.showImportStageOneStage(selectedValue, runners);
			}

		}
	}
	
	public void importData(ArrayList<Runner> runners){
		DataBase.addResults(runners, selectedValue, this);
		applicationScreen.showHomeStage(selectedValue);
	}
	
	public void updateCompetitionsList(Competition competition){
		competitions.remove(competition);
		System.out.println("REMOVED");
		
	}
	
	public void populateAvailableCompetitions(ObservableList<Competition> competitions){
		this.competitions = competitions;
		fxAvailableCompetitions.setItems(this.competitions);
		fxAvailableCompetitions.getSelectionModel().selectedItemProperty().addListener(
				 (observable, oldValue, selectedValue) -> {					 
					 //System.out.println(selectedValue.getId() + " " + selectedValue.getName());
					 this.selectedValue = selectedValue;
				 });
		//fxAvailableCompetitions.getSelectionModel().select(null);
		
	}
 
	 public void setApplicationScreen(ApplicationScreen applicationScreen) {
	      this.applicationScreen = applicationScreen;

	 }
	 
	 
	 @FXML
	 public void initialize() {
		 try {
			fxRunnersInDB.setText(DataBase.ReadStats("SELECT COUNT(*) AS total FROM RUNNERS"));
			fxCompetitionsInDB.setText(DataBase.ReadStats("SELECT COUNT(*) AS total FROM COMPETITIONS"));
			fxGroupsInDB.setText(DataBase.ReadStats("SELECT COUNT(DISTINCT CLUB) AS total FROM RUNNERS"));
			
			ObservableList<Competition> importedCompetitions = DataBase.ReadImportedCompetitions();
			fxLatestImported.setItems(importedCompetitions);
			
			
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	 }
	 
	 
	 
	 
	 
	
}
