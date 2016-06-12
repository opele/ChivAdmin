package presentation.views;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.UnaryOperator;

import chivrcon.business.ServerConnection;
import chivrcon.data.LoginData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import presentation.ChivAdminApp;
import presentation.data.PlayerView;
import presentation.data.ServerView;
import presentation.helper.Logger;
import presentation.helper.Logger.LogType;
import resources.Animations;
import resources.Txt;
import steam.SteamId;
import utils.WebUtils;

public class MainAppController extends AnchorPane implements Initializable, MainView {

	@FXML
	TextFlow sysMsgControl;
	@FXML
	TextFlow chatMsgControl;
	@FXML
	CheckBox wisperControl;
	@FXML
	ComboBox<String> sysMsgTypeFilterControl;
	@FXML
	TextField sysMsgMaxLinesFilterControl;
	@FXML
	TableView<ServerView> serverControl;
	@FXML
	FlowPane playerControl;
	@FXML
	ListView<String> playerDetails;

	private Map<LoginData, Set<PlayerView>> players = new HashMap<>();
	private Set<ServerConnection> serverConnections = new HashSet<>();
	private ServerView currentDisplayedServer;
	private Logger logger;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ChivAdminApp.getApp().setAppController(this);
		setLogger(new Logger(sysMsgControl, chatMsgControl));
		initComboBoxes();
		initServerViews();
		initPortTextFieldConstraints();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initServerViews() {
		((TableColumn) serverControl.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<ServerView, String>("isLoggedIn"));
		((TableColumn) serverControl.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<ServerView, String>("address"));
		((TableColumn) serverControl.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<ServerView, Integer>("players"));
		((TableColumn) serverControl.getColumns().get(3)).setCellValueFactory(new PropertyValueFactory<ServerView, String>("map"));
		((TableColumn) serverControl.getColumns().get(0)).setCellFactory(p -> new CheckBoxTableCell<ServerView, Boolean>());

		ObservableList<ServerView> data = FXCollections.observableArrayList();
		for (ServerView s : ChivAdminApp.getApp().getConfig().getServers()) {
			data.add(s);
		}
		serverControl.setItems(data);

		serverControl.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> arg0, Object oldValue, Object newValue) {
				if (newValue != null && !((ServerView) newValue).equals(currentDisplayedServer)) {
					currentDisplayedServerChanged((ServerView) newValue);
				}
			}
		});

		if (data.size() > 0)
			serverControl.getSelectionModel().select(0);
	}

	private void initComboBoxes() {
		ObservableList<String> items = FXCollections.observableArrayList(
				LogType.DEBUG.toString(),
				LogType.EVENT.toString(),
				LogType.COMMAND.toString(),
				LogType.INFO.toString(),
				LogType.WARN.toString(),
				LogType.ERROR.toString(),
				LogType.CHAT.toString(),
				"Disable Filter");
		sysMsgTypeFilterControl.setItems(items);
	}

	private void initPortTextFieldConstraints() {
		sysMsgMaxLinesFilterControl.setTextFormatter(new TextFormatter<Integer>(getTextFieldNumberConstraint()));
	}

	@Override
	public void connectToServer(String ip, String port, String adminPassword) {
		try {
			connectToServer(new LoginData(ip, Integer.parseInt(port), adminPassword));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			ChivAdminApp.getApp().log(Txt.errConnBadHost(e.getMessage()), LogType.ERROR);
		}
	}

	@Override
	public void connectToServer(LoginData login) {
		try {
			disconnect(login);
			ServerConnection sc = new ServerConnection(login);
			sc.connect((l) -> loginFailed(l));
			serverConnections.add(sc);
		} catch (Exception e) {
			serverControl.getItems()
					.stream()
					.filter(s -> s.getLoginData().equals(login))
					.forEach(s -> s.setLoggedIn(false));

			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Connection Failure");
			alert.setHeaderText(Txt.errConnFailPresentation("" + login));
			alert.setContentText("Reason: " + e.getMessage());

			alert.showAndWait();
		}
	}

	private void loginFailed(LoginData login) {
		disconnect(login);
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Login Failure");
		alert.setHeaderText("The admin login to the server " + login + " timed out.");
		alert.setContentText("Reason: " + Txt.errConnLoginTimeout());

		alert.showAndWait();
	}

	@Override
	public void disconnect(LoginData login) {
		ServerConnection con = getConnectionForLogin(login);
		if (con != null) {
			ChivAdminApp.getApp().log("Disconnecting from " + login.getAddress(), LogType.INFO);
			con.disconnect();
			serverConnections.remove(con);
		}
	}

	@Override
	public ServerView getServerViewByLogin(LoginData login) {
		if(login == null) return null;

		for(ServerView server : serverControl.getItems()) {
			if(server.getLoginData().equals(login))
				return server;
		}

		return null;
	}

	/**
	 * Gets the server connection object for specific login data or for the current selected server if null is passed.
	 * Via the server connection commands can be sent.
	 */
	@Override
	public ServerConnection getConnectionForLogin(LoginData login) {
		if (login == null && currentDisplayedServer != null)
			login = currentDisplayedServer.getLoginData();

		final LoginData loginData = login;
		Optional<ServerConnection> opt = serverConnections.stream()
				.filter(sc -> sc.getLoginData().equals(loginData))
				.findAny();

		if (opt.isPresent()) {
			return opt.get();
		}

		return null;
	}

	@Override
	public ChangeListener<Object> getIsLoggedInListener(ServerView server) {
		return new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> arg0, Object oldValue, Object newValue) {
				if ((Boolean) newValue) {
					connectToServer(server.getLoginData());
				} else {
					disconnect(server.getLoginData());
				}
			}
		};
	}

	@FXML
	void sandcastleImgControlClick(MouseEvent event) {
		effectClicked(event);
		WebUtils.openWebsite(Txt.urlSandcastleWorkshopPage());
	}

	/**
	 * Add and connect to server with ip and port configured via GUI.
	 *
	 * @param event
	 */
	@FXML
	void addServerIconClick(MouseEvent event) {
		effectClicked(event);
		ConnectController.createForm();
	}

	@FXML
	void playerActionIconClick(MouseEvent event) {
		effectClicked(event);
		PlayerActionController.createForm(null);
	}

	@FXML
	void commandsIconClick(MouseEvent event) {
		effectClicked(event);
		CommandController.createForm();
	}

	@FXML
	void settingsIconClick(MouseEvent event) {
		effectClicked(event);
		SettingsController.createForm();
	}

	@FXML
	void payPalDonationIconClick(MouseEvent event) {
		effectClicked(event);
		WebUtils.openWebsite(Txt.urlPayPalDonation());
	}

	@FXML
	void readMeIconClick(MouseEvent event) {
		effectClicked(event);
		ReadmeController.createForm();
	}

	private void effectClicked(MouseEvent event) {
		Animations.effectClicked((Node) event.getSource());
	}

	@FXML
	void hoverEffectEntered(MouseEvent event) {
		Animations.hoverEffectEntered((Node) event.getSource(), 1.15);
	}

	@FXML
	void hoverEffectEnteredSmall(MouseEvent event) {
		Animations.hoverEffectEntered((Node) event.getSource(), 1.08);
	}

	@FXML
	void hoverEffectExited(MouseEvent event) {
		Animations.hoverEffectExited((Node) event.getSource());
	}

	@FXML
	void scrollLockAction(ActionEvent event) {
		logger.setScrollLocked(((ToggleButton) event.getSource()).isSelected());
	}

	@FXML
	void selectFilterAction(ActionEvent event) {
		int selectIndex = ((ComboBox<?>) event.getSource()).getSelectionModel().getSelectedIndex();
		if (LogType.values().length <= selectIndex) {
			logger.setCurrentTypeFilter(null);
		} else
			logger.setCurrentTypeFilter(LogType.values()[selectIndex]);
	}

	@FXML
	void typedTextFilterAction(KeyEvent event) {
		if ("\r".equals(event.getCharacter())) {
			String filter = ((TextField) event.getSource()).getText();
			if (filter.length() == 0) {
				logger.setCurrentTextFilter(null);
			} else
				logger.setCurrentTextFilter(filter);
		}
	}

	@FXML
	void typedMaxLinesFilterAction(KeyEvent event) {
		if ("\r".equals(event.getCharacter())) {
			try {
				int maxLines = Integer.parseInt(((TextField) event.getSource()).getText());
				logger.setMaxDisplayedLogs(maxLines);
			} catch (Exception e) {
				logger.setMaxDisplayedLogs(-1);
			}
		}
	}

	@FXML
	void sendChatAction(KeyEvent event) {
		if ("\r".equals(event.getCharacter())) {
			TextField chatInput = ((TextField) event.getSource());
			String msg = chatInput.getText();
			if (msg != null && msg.length() > 0) {
				ServerConnection con = getConnectionForLogin(null);
				if (con != null) {
					msg = "ChivAdmin: " + msg;
					PlayerView selectedPlayer = getSelectedPlayer();
					if (selectedPlayer != null && wisperControl.isSelected()) {
						con.getCommandMngr().sendSay(msg, selectedPlayer.getSteamId());
						msg = "(whisper to " + selectedPlayer.getName() + ") " + msg;
						ChivAdminApp.getApp().log(msg, LogType.CHAT, Color.DARKMAGENTA);
					} else {
						con.getCommandMngr().sendSayAll(msg);
						ChivAdminApp.getApp().log(msg, LogType.CHAT, Color.DARKCYAN);
					}

					chatInput.setText("");
				}
			}
		}
	}

	@FXML
	void removeServerContextMenueTable(ActionEvent event) {
		if (serverControl.getSelectionModel().getSelectedItem() != null) {
			ServerView server = (ServerView) serverControl.getSelectionModel().getSelectedItem();
			ChivAdminApp.getApp().getConfig().removeServer(server);
			for (Iterator<ServerView> serverIter = serverControl.getItems().iterator(); serverIter.hasNext();) {
				ServerView serverEntry = (ServerView) serverIter.next();
				if (serverEntry.equals(server)) {
					serverIter.remove();
				}
			}
		}
	}

	@FXML
	void copyContextMenuPlayerDetailsAction(ActionEvent event) {
		if (playerDetails.getSelectionModel().getSelectedItem() != null) {
			String playerData = (String) playerDetails.getSelectionModel().getSelectedItem();
			copyToClipboard(playerData);
		}
	}

	@FXML
	void copyContextMenuChatAction(ActionEvent event) {
		StringBuffer chatTxt = new StringBuffer();
		for (Node txt : chatMsgControl.getChildrenUnmodifiable())
			chatTxt.append(((Text) txt).getText());

		copyToClipboard(chatTxt.toString());
	}

	@FXML
	void copyContextMenuSysMsgAction(ActionEvent event) {
		StringBuffer logTxt = new StringBuffer();
		for (Node txt : sysMsgControl.getChildrenUnmodifiable())
			logTxt.append(((Text) txt).getText());

		copyToClipboard(logTxt.toString());
	}

	/**
	 * Adds the player view to the GUI if it is from the current server and not yet displayed.
	 *
	 * @param player
	 */
	@Override
	public void addPlayerView(PlayerView player) {
		boolean isOfSelectedServer = currentDisplayedServer != null && player.getServerData().equals(currentDisplayedServer.getLoginData());
		Set<PlayerView> playersOfServer = getPlayersByServer(player.getServerData());
		if (playersOfServer.add(player))
			updateServerPlayerCount(player.getServerData());

		if (isOfSelectedServer) {
			player.requestGuiPlayerContainerAsync(new Runnable() {

				@Override
				public void run() {
					boolean isOfSelectedServer = currentDisplayedServer != null
							&& player.getServerData().equals(currentDisplayedServer.getLoginData());

					if (isOfSelectedServer && !playerControl.getChildren().contains(player.getGuiPlayerContainer())) {
						playerControl.getChildren().add(player.getGuiPlayerContainer());
					}
				}

			});
		}
	}

	/**
	 * Refreshes the display of each player in the server list according to settings.
	 */
	@Override
	public void refreshPlayerViews() {
		players.values()
			.stream()
			.flatMap(s -> s.stream())
			.forEach(p -> p.refreshGuiPlayerContainer());
	}

	/**
	 * Retrieves the cached players for a server.
	 *
	 * @param login
	 *            the server login info or null if the login of the current selected server should be used
	 * @return server specific players or an empty set
	 */
	@Override
	public Set<PlayerView> getPlayersByServer(LoginData login) {
		if (login == null)
			login = currentDisplayedServer.getLoginData();

		Set<PlayerView> playersOfServer = players.get(login);
		if (playersOfServer == null) {
			playersOfServer = new HashSet<>();
			players.put(login, playersOfServer);
		}

		return playersOfServer;
	}

	private void updateServerPlayerCount(LoginData login) {
		for (ServerView s : serverControl.getItems()) {
			if (s.getLoginData().equals(login)) {
				s.playersProperty().setValue(players.get(login).size());
			}
		}
	}

	@Override
	public PlayerView getPlayerCacheById(SteamId id) {
		Optional<PlayerView> player = players.keySet().stream()
				.flatMap(k -> players.get(k).stream())
				.filter(p -> p.getSteamId().equals(id))
				.findFirst();

		if (player.isPresent())
			return player.get();

		return null;
	}

	/**
	 * Clears all cached player data for the specific server
	 *
	 * @param server
	 *            login data
	 */
	@Override
	public void removeServerPlayerCache(LoginData server) {
		Set<PlayerView> serverPlayers = players.get(server);
		if (serverPlayers != null) {
			Set<PlayerView> playersCopy = new HashSet<>();
			playersCopy.addAll(serverPlayers);
			for (PlayerView p : playersCopy) {
				removePlayerCache(p);
			}
		}
	}

	/**
	 * Delete all info about a player when for instance the player disconnects.
	 *
	 * @param player
	 */
	@Override
	public void removePlayerCache(PlayerView player) {
		if (getSelectedPlayer() == player) {
			playerDetails.itemsProperty().unbind();
			playerDetails.itemsProperty().set(null);
		}

		if (player.getGuiPlayerContainer() != null)
			playerControl.getChildren().remove(player.getGuiPlayerContainer());

		Set<PlayerView> serverPlayers = players.get(player.getServerData());
		if (serverPlayers != null)
			serverPlayers.remove(player);

		updateServerPlayerCount(player.getServerData());
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	private void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	public TableView<ServerView> getServerControl() {
		return serverControl;
	}

	@Override
	public PlayerView getSelectedPlayer() {
		Set<PlayerView> serverPlayers = players.get(currentDisplayedServer.getLoginData());
		Optional<PlayerView> selectedPlayer = serverPlayers != null ? serverPlayers.stream()
				.filter(p -> p.getGuiPlayerContainer().getStyle().length() > 0).findAny() : Optional.empty();

		return selectedPlayer.isPresent() ? selectedPlayer.get() : null;
	}

	@Override
	public ServerView getCurrentDisplayedServer() {
		return currentDisplayedServer;
	}

	private void currentDisplayedServerChanged(ServerView currentDisplayedServer) {
		this.currentDisplayedServer = currentDisplayedServer;
		playerControl.getChildren().clear();
		playerDetails.itemsProperty().unbind();
		playerDetails.itemsProperty().set(null);

		if (players.containsKey(currentDisplayedServer.getLoginData())) {
			for (PlayerView p : players.get(currentDisplayedServer.getLoginData())) {
				addPlayerView(p);
			}
		}

		logger.redrawLoggingDisplay();
	}

	@Override
	public UnaryOperator<Change> getTextFieldNumberConstraint() {
		return c -> {
			if (c.isContentChange()) {
				String numbersOnly = "";
				for (char ch : c.getText().toCharArray()) {
					if (Character.isDigit(ch))
						numbersOnly += ch;
				}
				c.setText(numbersOnly);

				int newLength = c.getControlNewText().length();
				if (newLength > 5) {
					String tail = c.getControlNewText().substring(newLength - 5, newLength);
					c.setText(tail);
					int oldLength = c.getControlText().length();
					c.setRange(0, oldLength);
				}
			}
			return c;
		};
	}

	@Override
	public EventHandler<MouseEvent> createMouseActionForPlayerSelection() {
		return new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				PlayerView selectedPlayer = getSelectedPlayer();
				if (selectedPlayer != null)
					selectedPlayer.getGuiPlayerContainer().setStyle("");

				final ScrollPane playerContainer = (ScrollPane) event.getSource();
				playerContainer.setStyle("-fx-border-color: #057296; -fx-border-width: 3;");
				selectedPlayer = getSelectedPlayer();
				selectedPlayer.createDetailsView(playerDetails);
			}
		};
	}

	void copyToClipboard(String txt) {
		final Clipboard clipboard = Clipboard.getSystemClipboard();
		final ClipboardContent content = new ClipboardContent();

		content.putString(txt);
		clipboard.setContent(content);
	}

}
