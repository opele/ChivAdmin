package presentation.views;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import presentation.ChivAdminApp;
import presentation.helper.Logger.LogType;
import resources.Txt;

/**
 * Controller for the server login view.
 */
public class ConnectController extends AnchorPane implements Initializable {

	@FXML
	PasswordField passwordControl;
	@FXML
	CheckBox maskSwitchControl;
	@FXML
	TextField unmaskedPasswordControl;
	@FXML
	TextField portControl;
	@FXML
	TextField ipControl;

	private final static String FORM_PATH = "/presentation/views/ConnectForm.fxml";

	private static Stage form;

	public static void createForm() {
		form = new Stage();
		try {
			form.initModality(Modality.APPLICATION_MODAL);
			form.initOwner(ChivAdminApp.getApp().getPrimaryStage());
			AnchorPane page = (AnchorPane) FXMLLoader.load(ChivAdminApp.class.getResource(FORM_PATH));
			form.setScene(new Scene(page));
			form.show();
		} catch (Exception e) {
			e.printStackTrace();
			ChivAdminApp.getApp().log(Txt.errLoadForm("connect", e.getLocalizedMessage()), LogType.ERROR);
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initPasswordMaskSwitch();
		initPortTextFieldConstraints();
	}

	private void initPortTextFieldConstraints() {
		portControl.setTextFormatter(new TextFormatter<Integer>(ChivAdminApp.getApp().getMainView().getTextFieldNumberConstraint()));
	}

	private void initPasswordMaskSwitch() {
		unmaskedPasswordControl.managedProperty().bind(maskSwitchControl.selectedProperty());
		unmaskedPasswordControl.visibleProperty().bind(maskSwitchControl.selectedProperty());

		passwordControl.managedProperty().bind(maskSwitchControl.selectedProperty().not());
		passwordControl.visibleProperty().bind(maskSwitchControl.selectedProperty().not());

		unmaskedPasswordControl.textProperty().bindBidirectional(passwordControl.textProperty());
	}

	@FXML
	void confirmConnectAction(ActionEvent event) {
		ChivAdminApp.getApp().getMainView().connectToServer(ipControl.textProperty().getValue(), portControl.textProperty().getValue(), passwordControl.textProperty().getValue());
		form.close();
	}

}
