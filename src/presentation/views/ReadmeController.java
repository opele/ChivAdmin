package presentation.views;

import java.net.URL;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import presentation.ChivAdminApp;

/**
 * Displays the readme html document.
 */
class ReadmeController extends Region {

	private static Stage readmeFormStage;
	private static Scene readmeFormScene;

	final WebView browser = new WebView();
	final WebEngine webEngine = browser.getEngine();

	public static void createForm() {
		readmeFormStage = new Stage();
		readmeFormStage.setTitle("Infos");
		readmeFormScene = new Scene(new ReadmeController(), 750, 500, Color.web("#666970"));
		readmeFormStage.setScene(readmeFormScene);
		// readmeFormStage.initModality(Modality.APPLICATION_MODAL);
		readmeFormStage.initOwner(ChivAdminApp.getApp().getPrimaryStage());
		readmeFormStage.show();
	}

	public ReadmeController() {
		URL readmeURL = getClass().getResource("/resources/readme.html");
		webEngine.load(readmeURL.toExternalForm());
		getChildren().add(browser);
	}

	@Override
	protected void layoutChildren() {
		double w = getWidth();
		double h = getHeight();
		layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
	}

	@Override
	protected double computePrefWidth(double height) {
		return 750;
	}

	@Override
	protected double computePrefHeight(double width) {
		return 500;
	}
}
