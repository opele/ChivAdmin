package presentation.views;

import java.net.URL;
import java.util.ResourceBundle;

import chivrcon.business.ServerConnection;
import chivrcon.data.commands.ChivCmdConsoleCommand.ConsoleCommandScope;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import presentation.ChivAdminApp;
import presentation.data.PlayerView;
import presentation.helper.Logger.LogType;
import resources.Txt;
import steam.SteamId;

/**
 * Controller for the Player Action view.
 */
public class PlayerActionController extends AnchorPane implements Initializable {

	@FXML
	Accordion actionContainerControl;

	@FXML
	ChoiceBox<PlayerView> banPlayerPickControl;
	@FXML
	ChoiceBox<PlayerView> kickPlayerPickControl;
	@FXML
	ChoiceBox<PlayerView> tmpBanPlayerPickControl;
	@FXML
	ChoiceBox<PlayerView> changeScorePlayerPickControl;
	@FXML
	ChoiceBox<PlayerView> killPlayerPickControl;
	@FXML
	ChoiceBox<PlayerView> inebriatePlayerPickControl;
	@FXML
	ChoiceBox<PlayerView> consoleCommandPlayerPickControl;

	@FXML
	TextField banReasonControl;
	@FXML
	TextField kickReasonControl;
	@FXML
	TextField tmpBanReasonControl;
	@FXML
	TextField steamIdControl;
	@FXML
	TextField scoreInputControl;
	@FXML
	TextField consoleCommandControl;

	@FXML
	Slider banDurationControl;
	@FXML
	Label banDurationDisplayControl;
	@FXML
	CheckBox consoleCommandAllPlayersControl;

	public enum PlayerAction {
		BAN, KICK, TMP_BAN, UNBAN, CHANGE_SCORE, KILL, INEBRIATE
	}

	private final static String FORM_PATH = "/presentation/views/PlayerActionForm.fxml";

	private static Stage playerActionForm;
	private static PlayerAction preSelectedAction;

	public static void createForm(PlayerAction preSelectedAction) {
		PlayerActionController.preSelectedAction = preSelectedAction;
		playerActionForm = new Stage();
		try {
			playerActionForm.initModality(Modality.APPLICATION_MODAL);
			playerActionForm.initOwner(ChivAdminApp.getApp().getPrimaryStage());
			AnchorPane page = (AnchorPane) FXMLLoader.load(ChivAdminApp.class.getResource(FORM_PATH));
			playerActionForm.setScene(new Scene(page));
			playerActionForm.show();
		} catch (Exception e) {
			e.printStackTrace();
			ChivAdminApp.getApp().log(Txt.errLoadForm("player action", e.getLocalizedMessage()), LogType.ERROR);
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initBanDurationDisplay();
		initPlayerPickControls();

		if(preSelectedAction != null) {
			actionContainerControl.setExpandedPane((TitledPane) actionContainerControl.getPanes().get(preSelectedAction.ordinal()));
		}
	}

	private void initPlayerPickControls() {
		MainView control = ChivAdminApp.getApp().getMainView();
		ObservableList<PlayerView> players = FXCollections.observableArrayList(control.getPlayersByServer(null));

		banPlayerPickControl.setItems(players);
		kickPlayerPickControl.setItems(players);
		tmpBanPlayerPickControl.setItems(players);
		changeScorePlayerPickControl.setItems(players);
		killPlayerPickControl.setItems(players);
		inebriatePlayerPickControl.setItems(players);
		consoleCommandPlayerPickControl.setItems(players);

		PlayerView selectedPlayer = control.getSelectedPlayer();
		banPlayerPickControl.setValue(selectedPlayer);
		kickPlayerPickControl.setValue(selectedPlayer);
		tmpBanPlayerPickControl.setValue(selectedPlayer);
		changeScorePlayerPickControl.setValue(selectedPlayer);
		killPlayerPickControl.setValue(selectedPlayer);
		inebriatePlayerPickControl.setValue(selectedPlayer);
		consoleCommandPlayerPickControl.setValue(selectedPlayer);
	}

	private void initBanDurationDisplay() {
		banDurationControl.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				int min = newValue.intValue();
				String minute = min % 60 + " Minute" + (min % 60 > 1 ? "s " : " ");
				String hour = min / 60 > 0 ? (min % 1440) / 60 + " Hour" + ((min % 1440) / 60 != 1 ? "s " : " ") : "";
				String day = min / 1440 > 0 ? min / 1440 + " Day" + (min / 1440 != 1 ? "s " : " ") : "";

				banDurationDisplayControl.setText(day + hour + minute);
			}
		});
	}

	@FXML
	void confirmBanAction(ActionEvent event) {
		MainView control = ChivAdminApp.getApp().getMainView();
		ServerConnection conn = control.getConnectionForLogin(null);
		PlayerView player = banPlayerPickControl.getValue();

		if (player != null && conn != null)
			conn.getCommandMngr().sendBanPlayer(player.getSteamId(), banReasonControl.getText());

		playerActionForm.close();
	}

	@FXML
	void confirmKickAction(ActionEvent event) {
		PlayerView player = kickPlayerPickControl.getValue();
		if (player != null)
			player.kickFromServer(kickReasonControl.getText());

		playerActionForm.close();
	}

	@FXML
	void confirmTmpBanAction(ActionEvent event) {
		MainView control = ChivAdminApp.getApp().getMainView();
		ServerConnection conn = control.getConnectionForLogin(null);
		PlayerView player = tmpBanPlayerPickControl.getValue();

		if (player != null && conn != null)
			conn.getCommandMngr().sendTmpBanPlayer(player.getSteamId(),  tmpBanReasonControl.getText(), (int) banDurationControl.getValue() * 60);

		playerActionForm.close();
	}

	@FXML
	void confirmUnbanAction(ActionEvent event) {
		MainView control = ChivAdminApp.getApp().getMainView();
		ServerConnection conn = control.getConnectionForLogin(null);
		SteamId id = SteamId.parse(steamIdControl.getText());

		if (conn != null)
			conn.getCommandMngr().sendUnbanPlayer(id);

		playerActionForm.close();
	}

	@FXML
	void confirmChangeScoreAction(ActionEvent event) {
		MainView control = ChivAdminApp.getApp().getMainView();
		ServerConnection conn = control.getConnectionForLogin(null);
		PlayerView player = changeScorePlayerPickControl.getValue();
		int scoreDelta = 0;
		try {
			scoreDelta = Integer.parseInt(scoreInputControl.getText());
		}catch(Exception e){}

		if (player != null && conn != null)
			conn.getCommandMngr().sendChangeScore(player.getSteamId(), scoreDelta);

		playerActionForm.close();
	}

	@FXML
	void confirmKillPlayerAction(ActionEvent event) {
		MainView control = ChivAdminApp.getApp().getMainView();
		ServerConnection conn = control.getConnectionForLogin(null);
		PlayerView player = killPlayerPickControl.getValue();

		if (player != null && conn != null)
			conn.getCommandMngr().sendKillPlayer(player.getSteamId());

		playerActionForm.close();
	}

	@FXML
	void confirmInebriatePlayerAction(ActionEvent event) {
		MainView control = ChivAdminApp.getApp().getMainView();
		ServerConnection conn = control.getConnectionForLogin(null);
		PlayerView player = inebriatePlayerPickControl.getValue();

		if (player != null && conn != null)
			conn.getCommandMngr().sendInebriate(player.getSteamId());

		playerActionForm.close();
	}

	@FXML
	void confirmConsoleCommandPlayerAction(ActionEvent event) {
		MainView control = ChivAdminApp.getApp().getMainView();
		ServerConnection conn = control.getConnectionForLogin(null);
		PlayerView player = consoleCommandPlayerPickControl.getValue();
		ConsoleCommandScope cmdScope = consoleCommandAllPlayersControl.isSelected() ?
				ConsoleCommandScope.SCOPE_ALL_PLAYERS : ConsoleCommandScope.SCOPE_PLAYER;
		String cmd = consoleCommandControl.getText();

		if ((player != null || cmdScope == ConsoleCommandScope.SCOPE_ALL_PLAYERS)  && conn != null) {
			conn.getCommandMngr().sendConsoleCommand(player == null ? new SteamId() : player.getSteamId(), cmdScope, cmd);
		}

		playerActionForm.close();
	}

}
