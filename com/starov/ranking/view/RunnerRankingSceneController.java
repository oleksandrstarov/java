package com.starov.ranking.view;

import java.util.Map;

import com.starov.ranking.model.Runner;
import com.starov.ranking.utils.DataBase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class RunnerRankingSceneController {
	private ApplicationScreen applicationScreen;
	@FXML
	private TableView<Runner> fxResults;
	
	@FXML
	private TableColumn<Runner, String> fxNameColumn, fxGroupColumn, fxDateColumn, fxTimeColumn, fxTimeAfterColumn;
	@FXML
	private TableColumn<Runner, Integer> fxPlaceColumn;
	@FXML
	private TableColumn<Runner, Double> fxPointsColumn;
	
	@FXML
	private ChoiceBox<String> fxName;
	
	@FXML
	private Label fxClub, fxBirthDate, fxPoints;
	
	private ObservableList<Runner>runners;
	
	
	
	private void populateValues(){
		System.out.println(runners.size());
		
		fxResults.setItems(runners);
		
		fxGroupColumn.setCellValueFactory(cellData -> cellData.getValue().getGroup());
		fxNameColumn.setCellValueFactory(cellData -> cellData.getValue().getCompetitionName());
		fxTimeColumn.setCellValueFactory(cellData -> cellData.getValue().getResult());
	    fxPlaceColumn.setCellValueFactory(cellData -> cellData.getValue().getPlace());
	    fxPointsColumn.setCellValueFactory(cellData -> cellData.getValue().getPointsProperty());
	    fxDateColumn.setCellValueFactory(cellData -> cellData.getValue().getLatestDate());
	    fxTimeAfterColumn.setCellValueFactory(cellData -> cellData.getValue().getTimeAfter());
	    
			
	}
	
	//populate choice box with runner names
	public void setRunners(){
		
		ObservableList<String>runners = DataBase.getRunners();
		fxName.setItems(runners);
		
	}
	
	@FXML
	public void initialize() {
		
		fxName.setOnAction((event) -> {
		    String name = fxName.getSelectionModel().getSelectedItem();
		   
		    System.out.println("ComboBox Action (selected: " + name + ")");
		    getRunnerInfo(name);
		});

	}
	
	private void getRunnerInfo(String name){
		String [] data = DataBase.getRunnerDetails(name);
		fxBirthDate.setText(data[1]);
		fxClub.setText(data[2]);
		fxPoints.setText(data[3]);
		int reference = Integer.valueOf(data[0]);
		System.out.println(reference);
		this.runners = DataBase.getRunnerResults(reference);
		populateValues();
		
		
		
		
	}
	
	public void setApplicationScreen(ApplicationScreen applicationScreen) {
	      this.applicationScreen = applicationScreen;

	}
}

