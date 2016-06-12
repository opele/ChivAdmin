package presentation;

import chivrcon.data.LoginData;
import chivrcon.data.events.ChivEvtMapChanged;
import chivrcon.data.events.ChivEvtNameChanged;
import chivrcon.data.events.ChivEvtPing;
import chivrcon.data.events.ChivEvtPingExtended;
import chivrcon.data.events.ChivEvtPlayerChat;
import chivrcon.data.events.ChivEvtPlayerConnect;
import chivrcon.data.events.ChivEvtPlayerDisconnect;
import chivrcon.data.events.ChivEvtTeamChanged;
import javafx.application.Platform;
import javafx.scene.control.TableView;
import presentation.data.PlayerView;
import presentation.data.ServerView;
import presentation.helper.Logger.LogType;
import presentation.views.MainView;

/**
 * Processes the event messages received from the server to update the GUI.
 * The methods are called from an extra thread which receives messages,
 * so make sure to relay the processing of the message to the javafx thread.
 */
public class EventHandler {

	public void serverLoggedIn(LoginData login) {
		Platform.runLater(() -> {
			MainView appControl = ChivAdminApp.getApp().getMainView();
			TableView<ServerView> serverControl = appControl.getServerControl();
			ServerView server = new ServerView(login, "n.a.");
			server.setLoggedIn(true);
			if (!serverControl.getItems().contains(server)) {
				server.isLoggedInProperty().addListener(appControl.getIsLoggedInListener(server));
				ChivAdminApp.getApp().getConfig().putServer(server);
				serverControl.getItems().add(server);
				serverControl.getSelectionModel().clearAndSelect(serverControl.getItems().size() - 1);
			} else {
				appControl.removeServerPlayerCache(login);
			}
		});
	}

	public void playerConnected(ChivEvtPlayerConnect evt) {
		Platform.runLater(() -> {
			ChivAdminApp.getApp().getMainView().addPlayerView(new PlayerView(evt));
		});
	}

	public void ping(ChivEvtPing evt) {
		Platform.runLater(() -> {
			MainView control = ChivAdminApp.getApp().getMainView();
			PlayerView player = control.getPlayerCacheById(evt.getSteamId());
			if (player != null) {
				player.setPing(evt.getPing());
			}
		});
	}

	public void pingExtended(ChivEvtPingExtended evt) {
		Platform.runLater(() -> {
			MainView control = ChivAdminApp.getApp().getMainView();
			PlayerView player = control.getPlayerCacheById(evt.getSteamId());
			if (player != null) {
				player.setPing(evt.getPing());
				player.setIdleTime(evt.getIdleTime());
				player.setKills(evt.getKills());
				player.setRank(evt.getRank());
				player.setScore(evt.getScore());
				player.setTeamDamage(evt.getTeamDamageDealt());
			}
		});
	}

	public void nameChanged(ChivEvtNameChanged evt) {
		Platform.runLater(() -> {
			MainView control = ChivAdminApp.getApp().getMainView();
			PlayerView player = control.getPlayerCacheById(evt.getSteamId());
			if (player != null) {
				player.setName(evt.getPlayerName());
			}
		});
	}

	public void playerDisconnect(ChivEvtPlayerDisconnect evt) {
		Platform.runLater(() -> {
			MainView control = ChivAdminApp.getApp().getMainView();
			PlayerView player = control.getPlayerCacheById(evt.getSteamId());
			if (player != null) {
				control.removePlayerCache(player);
			}
		});
	}

	public void teamChanged(ChivEvtTeamChanged evt) {
		Platform.runLater(() -> {
			MainView control = ChivAdminApp.getApp().getMainView();
			PlayerView player = control.getPlayerCacheById(evt.getSteamId());
			if (player != null) {
				player.setTeam(evt.getTeamId());
			}
		});
	}

	public void mapChanged(ChivEvtMapChanged evt) {
		Platform.runLater(() -> {
			MainView control = ChivAdminApp.getApp().getMainView();
			TableView<ServerView> servers = control.getServerControl();
			for (ServerView s : servers.getItems()) {
				if (s.getLoginData().equals(evt.getServerData())) {
					s.mapProperty().setValue(evt.getMapName());
				}
			}
		});
	}

	public void playerChat(ChivEvtPlayerChat evt) {
		Platform.runLater(() -> {
			MainView control = ChivAdminApp.getApp().getMainView();
			PlayerView player = control.getPlayerCacheById(evt.getSteamId());
			String playerNamePrefix = player != null ? player.getName() + ": " : "";
			ChivAdminApp.getApp().log(playerNamePrefix + evt.getMessage(), LogType.CHAT, evt.getServerData());
		});
	}

}
