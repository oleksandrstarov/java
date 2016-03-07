package com.starov.ranking.view;

import javafx.fxml.FXML;


/**
 * Note that we load the panes with the FXMLLoader
 * on every use. This allows us to manipulate the
 * CSS between loads and have it take affect. 
 * 
 * Also, the panes should not save state internally.
 * Reloading the FXML forces better programming
 * design, because it is impossible to get lazy
 * and expect the panes to save their own state.
 */
public class TopMenuNavigationController {


  /**
   * Event handler for MenuItem one
   */
	// Reference to the main application.
	private ApplicationScreen applicationScreen;
      
	@FXML
  private void showRunnerRankingStage() {
		applicationScreen.showRunnerRankingStage();
  }
  
  @FXML
  private void showClubRankingStage() {
	  applicationScreen.showClubRankingStage();
  }
  
  	@FXML
	public void showHomeStage() {
  		applicationScreen.showHomeStage();
  }
  
  @FXML
  private void showRunnerStage() {
	  applicationScreen.showRunnerStage();
  }
  
  @FXML
  private void showSettingsStage() {
	  applicationScreen.showSettingsStage();
  }
  
  @FXML
  private void closeStage() {
      System.exit(0);
  }
  
  public void setApplicationScreen(ApplicationScreen applicationScreen) {
      this.applicationScreen = applicationScreen;

  }
 
}