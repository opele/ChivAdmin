package presentation;

import chivrcon.data.LoginData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import presentation.data.ServerView;
import presentation.helper.Config;
import presentation.helper.Logger.LogType;
import presentation.views.MainAppController;
import presentation.views.MainView;
import resources.Txt;

/**
 * Entry point of the application.
 *
 */
public class ChivAdminApp extends Application {

	private Stage primaryStage;
	private static ChivAdminApp app;
	private EventHandler eventHandler;
	private MainAppController mainController;
	private final static String MAIN_APP_LAYOUT = "/presentation/views/MainAppLayout.fxml";

	private Config config;

	public ChivAdminApp() {
		this.config = new Config();
	}

	@Override
	public void start(Stage primaryStage) {
		app = this;
		eventHandler = new EventHandler();
		setPrimaryStage(primaryStage);
		primaryStage.setTitle("Chiv Admin RCon Tool");
		primaryStage.getIcons().add(new Image("/resources/ChivAssets/icons/chivlogo_icon.png"));

		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(ChivAdminApp.class.getResource(MAIN_APP_LAYOUT));
			SplitPane page = (SplitPane) fxmlLoader.load();
			Scene scene = new Scene(page);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
			ChivAdminApp.getApp().log(Txt.errLoadForm("admin app loadup", e.getLocalizedMessage()), LogType.ERROR);
		}

	}

	public static void main(String[] args) {
		launch(args);
	}

	public static ChivAdminApp getApp() {
		return app;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	private void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public EventHandler getEventHandler() {
		return eventHandler;
	}

	public void notifyConnectionLost(LoginData login) {
		getMainView().removeServerPlayerCache(login);
		ServerView server = getMainView().getServerViewByLogin(login);
		if(server != null)
			server.isLoggedInProperty().set(false);
	}

	/**
	 * Subset of the AppController containing custom controller specific methods.
	 *
	 * @return
	 */
	public MainView getMainView() {
		return (MainView) mainController;
	}

	public void setAppController(MainAppController mainController) {
		this.mainController = mainController;
	}

	public void log(String msg, LogType type) {
		log(msg, type, (Color) null);
	}

	public void log(String msg, LogType type, Color color) {
		log(msg, type, getMainView().getCurrentDisplayedServer(), color);
	}

	public void log(String msg, LogType type, ServerView server, Color color) {
		log(msg, type, server != null ? server.getLoginData() : null, color);
	}

	public void log(String msg, LogType type, LoginData login) {
		log(msg, type, login, (Color) null);
	}

	public void log(String msg, LogType type, LoginData login, Color color) {
		mainController.getLogger().log(msg, type, login, color);
	}

	public Config getConfig() {
		return config;
	}

}
