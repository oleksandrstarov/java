package com.starov.ranking.view;

import java.sql.SQLException;

import com.starov.ranking.model.Competition;
import com.starov.ranking.model.Runner;
import com.starov.ranking.utils.DataBase;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class RankingSceneController {
	private ApplicationScreen applicationScreen;
	@FXML
	private TableView<Runner> fxRunnersRanking;
	
	@FXML
	private TableColumn<Runner, String> fxName, fxClub, fxGroup, fxLastDate;
	@FXML
	private TableColumn<Runner, Integer> fxPlace;
	@FXML
	private TableColumn<Runner, Double> fxPoints;
	
	@FXML
	private ChoiceBox<String> fxSelectGroup;
	
	private ObservableList<Runner>runners;
	private ObservableList<String>groups;
	
	public void setRanking(){
		groups = DataBase.getGroups();
		fxSelectGroup.setItems(groups);
		fxSelectGroup.setValue(groups.get(0)); // set default value
		fxSelectGroup.setOnAction((event) -> {
		    String group = fxSelectGroup.getSelectionModel().getSelectedItem();
		   
		    System.out.println("ComboBox Action (selected: " + group + ")");
		    if(group == "All Groups") group = "";
		    populateValues(DataBase.getGroupRanking(group));
		    
		});
		runners = DataBase.getGroupRanking("");
		populateValues(runners);
		
	}
	private void populateValues(ObservableList<Runner>runners){
		fxRunnersRanking.setItems(runners);
		// Initialize the person table with the two columns.
		fxGroup.setCellValueFactory(cellData -> cellData.getValue().getGroup());
	    fxName.setCellValueFactory(cellData -> cellData.getValue().getName());
	    fxClub.setCellValueFactory(cellData -> cellData.getValue().getClub());
	    fxPlace.setCellValueFactory(cellData -> cellData.getValue().getPlace());
	    fxPoints.setCellValueFactory(cellData -> cellData.getValue().getPointsProperty());
	    fxLastDate.setCellValueFactory(cellData -> cellData.getValue().getLatestDate());
	    
			
	}
	
	@FXML
	private void selectGroup() {
		System.out.println("Selected value: " + fxSelectGroup.getValue());
	}
	
	
	@FXML
	public void initialize() {

	}
	
	public void setApplicationScreen(ApplicationScreen applicationScreen) {
	      this.applicationScreen = applicationScreen;

	}
}
