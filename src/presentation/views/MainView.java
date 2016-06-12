package presentation.views;

import java.util.Set;
import java.util.function.UnaryOperator;

import chivrcon.business.ServerConnection;
import chivrcon.data.LoginData;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.input.MouseEvent;
import presentation.data.PlayerView;
import presentation.data.ServerView;
import presentation.helper.Logger;
import steam.SteamId;

/**
 * Encapsulates all non-FXML exposed methods from the controller.
 */
public interface MainView {

	void connectToServer(String ip, String port, String adminPassword);

	void connectToServer(LoginData login);

	void disconnect(LoginData login);

	ServerConnection getConnectionForLogin(LoginData login);

	ChangeListener<Object> getIsLoggedInListener(ServerView server);

	void addPlayerView(PlayerView player);

	void refreshPlayerViews();

	Set<PlayerView> getPlayersByServer(LoginData login);

	PlayerView getPlayerCacheById(SteamId id);

	void removePlayerCache(PlayerView player);

	void removeServerPlayerCache(LoginData server);

	Logger getLogger();

	TableView<ServerView> getServerControl();

	PlayerView getSelectedPlayer();

	ServerView getCurrentDisplayedServer();

	ServerView getServerViewByLogin(LoginData login);

	UnaryOperator<Change> getTextFieldNumberConstraint();

	EventHandler<MouseEvent> createMouseActionForPlayerSelection();
}
