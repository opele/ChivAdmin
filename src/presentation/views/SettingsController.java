package presentation.views;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import presentation.ChivAdminApp;
import presentation.helper.Config;
import presentation.helper.Logger.LogType;
import resources.Txt;

/**
 * Controller for the settings view.
 */
public class SettingsController extends AnchorPane implements Initializable {

	@FXML
	RadioButton applyAutoKickForSelectedServer;
	@FXML
	RadioButton applyAutoKickForAllServers;
	@FXML
	CheckBox enablePingAutoKick;
	@FXML
	CheckBox enableTeamdamageAutoKick;
	@FXML
	TextField maxPing;
	@FXML
	TextField pingTolerance;
	@FXML
	TextField maxTeamdamage;
	@FXML
	CheckBox displayPlayerAvatar;

	private final static String FORM_PATH = "/presentation/views/SettingsForm.fxml";

	private static Stage form;

	private Config config;

	public static void createForm() {
		form = new Stage();
		form.setTitle("Settings");
		form.initModality(Modality.APPLICATION_MODAL);
		try {
			form.initOwner(ChivAdminApp.getApp().getPrimaryStage());
			ScrollPane page = (ScrollPane) FXMLLoader.load(ChivAdminApp.class.getResource(FORM_PATH));
			form.setScene(new Scene(page));
			form.show();
		} catch (Exception e) {
			e.printStackTrace();
			ChivAdminApp.getApp().log(Txt.errLoadForm("settings", e.getLocalizedMessage()), LogType.ERROR);
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		maxPing.setTextFormatter(new TextFormatter<Integer>(ChivAdminApp.getApp().getMainView().getTextFieldNumberConstraint()));
		pingTolerance.setTextFormatter(new TextFormatter<Integer>(ChivAdminApp.getApp().getMainView().getTextFieldNumberConstraint()));
		maxTeamdamage.setTextFormatter(new TextFormatter<Integer>(ChivAdminApp.getApp().getMainView().getTextFieldNumberConstraint()));

		config = ChivAdminApp.getApp().getConfig();
		applyAutoKickForSelectedServer.setSelected(!config.isApplyAutoKickToAllServers());
		applyAutoKickForAllServers.setSelected(config.isApplyAutoKickToAllServers());
		enablePingAutoKick.setSelected(config.isPingAutokickEnabled());
		enableTeamdamageAutoKick.setSelected(config.isTeamdamageAutokickEnabled());
		maxPing.setText("" + config.getMaxAutoKickPing());
		maxTeamdamage.setText("" + config.getMaxAutoKickTeamdamage());
		pingTolerance.setText("" + config.getPingTolerance());

		displayPlayerAvatar.setSelected(config.isDisplayPlayerAvatar());
	}

	@FXML
	void saveAndApply() {
		int maxPingValue = 0;
		int maxTeamDamageValue = 0;
		int pingToleranceValue = 0;
		try {
			maxPingValue = Integer.parseInt(maxPing.getText());
			maxTeamDamageValue = Integer.parseInt(maxTeamdamage.getText());
			pingToleranceValue = Integer.parseInt(pingTolerance.getText());
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Save Failure");
			alert.setHeaderText(Txt.errParseConfig());
			alert.setContentText("Reason: " + e.getMessage());

			alert.showAndWait();
		}

		config.setApplyAutoKickToAllServers(applyAutoKickForAllServers.isSelected());
		config.setPingAutokickEnabled(enablePingAutoKick.isSelected());
		config.setTeamdamageAutokickEnabled(enableTeamdamageAutoKick.isSelected());
		config.setMaxAutoKickPing(maxPingValue);
		config.setMaxAutoKickTeamdamage(maxTeamDamageValue);
		config.setPingTolerance(pingToleranceValue);

		config.setDisplayPlayerAvatar(displayPlayerAvatar.isSelected());
		ChivAdminApp.getApp().getMainView().refreshPlayerViews();

		form.close();
	}

}
