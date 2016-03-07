package com.starov.ranking.view;

import java.sql.SQLException;
import java.util.Date;

import com.starov.ranking.model.Competition;
import com.starov.ranking.model.Runner;
import com.starov.ranking.utils.Converter;
import com.starov.ranking.utils.DataBase;
import com.starov.ranking.utils.RankCount;

import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ImportStageOneController {

	private ApplicationScreen applicationScreen;
	private Competition competition;
	
	@FXML
	private TextField fxCompetitionName;
	
	@FXML
	private Label fxGroups, fxRunners;
	
	@FXML
	private TableView<Runner> fxRunnersTable;
	
	@FXML
	private TableColumn<Runner, String> fxGroup, fxName, fxClub, fxQualification, fxTime, fxTimeAfter, fxPlace;

	private ObservableList<Runner>runners;
	
	@FXML
	public ProgressBar fxProgress;
	
	

	//Import button on home scene
	@FXML
	private void doImport() {
		Converter.trace("start doImport()");
		//save name
		competition.setNameFromFile(fxCompetitionName.getText());
		//set up ranking points
		System.out.println(runners.size() + " from do import");
		applicationScreen.showHomeStage(RankCount.rankCount(runners), competition);
		Converter.trace("end resultsSaved()");
		
		
		
		
		/*int selectedIndex = personTable.getSelectionModel().getSelectedIndex();
	    personTable.getItems().remove(selectedIndex);*/
		
		
		
		//show home
		//applicationScreen.showHomeStage(competition);
	}
	
	@FXML
	private void cancel() {
		applicationScreen.showHomeStage();
	}
 
	 public void setApplicationScreen(ApplicationScreen applicationScreen) {
	      this.applicationScreen = applicationScreen;

	 }
	 
	 public void setCompetition(Competition competition, ObservableList<Runner>runners){
		 System.out.println(runners.size() + " from import load");
		 this.runners = runners;
		 this.competition = competition;
		 //actually name should be parsed from file
		 fxCompetitionName.setText(competition.toString());
		 fxGroups.setText(Integer.toString(competition.getGroupsTotal()));
		 System.out.println(competition.getRunnersTotal());
		 fxRunners.setText(Integer.toString(competition.getRunnersTotal()));
		 
		 fxRunnersTable.setItems(runners);
		// Initialize the person table with the two columns.
		 fxGroup.setCellValueFactory(cellData -> cellData.getValue().getGroup());
	     fxName.setCellValueFactory(cellData -> cellData.getValue().getName());
	     fxClub.setCellValueFactory(cellData -> cellData.getValue().getClub());
	     fxQualification.setCellValueFactory(cellData -> cellData.getValue().getQualification());
	     fxTime.setCellValueFactory(cellData -> cellData.getValue().getResult());
	     fxTimeAfter.setCellValueFactory(cellData -> cellData.getValue().getTimeAfter());
	     fxPlace.setCellValueFactory(cellData -> cellData.getValue().getPlaceString());
	 }
	 
	 @FXML
	 private void initialize() {
		 
	 }
	 //group, name, club, qualification, time, after, place;

	 
}
