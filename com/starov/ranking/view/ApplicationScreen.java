package com.starov.ranking.view;

import java.io.IOException;
import java.util.ArrayList;

import com.starov.ranking.MainApp;
import com.starov.ranking.model.Competition;
import com.starov.ranking.model.Runner;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ApplicationScreen {
	private MainApp mainApp;
	private BorderPane rootLayout;
	private Stage primaryStage;
	
	 //controller classes
    private TopMenuNavigationController menuController;
    private HomeSceneController homeSceneController;
    private ImportStageOneController importStageOneController;
    private RankingSceneController rankingSceneController;
    private RunnerRankingSceneController runnerRankingSceneController;
   // private ImportStageTwoController importStageTwoController;
    
    private SplitPane homeOverview;
    private Pane settingsOverview;
    private Pane clubRankingOverview;
    private AnchorPane importStageOneOverview, runnerRankingOverview, runnerOverview;
    
	
	
	public ApplicationScreen(MainApp mainApp, Stage primaryStage){
		this.mainApp = mainApp;
		this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Ranking Program");
        initRootLayout();
        initStages();
	}
	

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootWindow.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            
            // Give the menu controller access to the main app.
            menuController = loader.getController();
            menuController.setApplicationScreen(this);
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void initStages(){
    
     // Load Home scene.
    	try {
    		FXMLLoader loader = new FXMLLoader();
    		
    		//home Scene
    		loader.setLocation(MainApp.class.getResource("view/HomeScene.fxml"));
    		homeOverview = (SplitPane) loader.load();
    		homeSceneController = loader.getController();
    		homeSceneController.setApplicationScreen(this);
    		
    		loader = new FXMLLoader();
    		loader.setLocation(MainApp.class.getResource("view/Step1Scene.fxml"));
    		importStageOneOverview = (AnchorPane) loader.load();
	        importStageOneController = loader.getController();
	        importStageOneController.setApplicationScreen(this);
	        
	     /*   loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/Step2Scene.fxml"));
	        importStageTwoOverview = (AnchorPane) loader.load();
	        importStageTwoController = loader.getController();
	        importStageTwoController.setApplicationScreen(this);*/
    		
	        loader = new FXMLLoader();
    		loader.setLocation(MainApp.class.getResource("view/RunnerScene.fxml"));
            runnerOverview = (AnchorPane) loader.load();
            runnerRankingSceneController = loader.getController();
            runnerRankingSceneController.setApplicationScreen(this);
    		
            loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/SettingsScene.fxml"));
            settingsOverview = (Pane) loader.load();
    		
            loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RankingClubScene.fxml"));
            clubRankingOverview = (Pane) loader.load();
    		
            loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RankingRunnerScene.fxml"));
            runnerRankingOverview = (AnchorPane) loader.load();
            rankingSceneController = loader.getController();
            rankingSceneController.setApplicationScreen(this);
            
    		
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    }
    
    // Set person overview into the center of root layout.
    public void showHomeStage(){
    	homeSceneController.initialize();
    	rootLayout.setCenter(homeOverview);
    }
    
    public void showHomeStage(Competition competition){
    	
    	homeSceneController.updateCompetitionsList(competition);
    	homeSceneController.initialize();
    	rootLayout.setCenter(homeOverview);
    }
    
    public void showHomeStage(ObservableList<Competition> competitions){
    	homeSceneController.initialize();
    	homeSceneController.populateAvailableCompetitions(competitions);
    	rootLayout.setCenter(homeOverview);
    }
    
    public void showHomeStage(ArrayList<Runner> runners, Competition competition){
    	homeSceneController.initialize();
    	homeSceneController.importData(runners);
    	homeSceneController.updateCompetitionsList(competition);
    	
    	rootLayout.setCenter(homeOverview);
    }
    
    public void showRunnerStage(){
    	runnerRankingSceneController.setRunners();
    	rootLayout.setCenter(runnerOverview);
    }
    
    public void showSettingsStage(){
    	rootLayout.setCenter(settingsOverview);
    }
   
    public void showClubRankingStage(){
    	rootLayout.setCenter(clubRankingOverview);
    }
    
    public void showRunnerRankingStage(){
    	rankingSceneController.setRanking();
    	rootLayout.setCenter(runnerRankingOverview);
    }
    
    //Import
    public void showImportStageOneStage(Competition competition, ObservableList<Runner> runners){
    	importStageOneController.setCompetition(competition, runners);
    	rootLayout.setCenter(importStageOneOverview);
    }
    
   /* public void showImportStageTwoStage(){
    	rootLayout.setCenter(importStageTwoOverview);
    }*/
    
    public MainApp getMainApp(){
    	return mainApp;
    }
	
}
