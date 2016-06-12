package presentation.data;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import chivrcon.business.ServerConnection;
import chivrcon.data.LoginData;
import chivrcon.data.events.ChivEvtPlayerConnect;
import chivrcon.data.events.ChivEvtTeamChanged.EAOCFaction;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import presentation.ChivAdminApp;
import presentation.data.PlayerDetailsView.PlayerDetails;
import presentation.helper.Config;
import presentation.helper.Logger.LogType;
import presentation.views.MainView;
import presentation.views.PlayerActionController;
import presentation.views.PlayerActionController.PlayerAction;
import resources.Txt;
import steam.PlayerSummary;
import steam.SteamId;

/**
 * Contains the relevant data to display the player.
 * Consists of several data types retrieved from various sources, like from Steam API or the server.
 */
public class PlayerView {

	private LoginData serverData;
	private String name;
	private SteamId steamId;
	private EAOCFaction team;
	private int ping;
	private int score;
	private int teamDamage;
	private int idleTime;
	private int kills;
	private int rank;
	private PlayerSummary steamInfo;
	private ScrollPane guiPlayerContainer;
	private PlayerDetailsView playerDetails;

	// AUTOCKICK SETTINGS
	private long timeSinceExceedingMaxPing = 0;

	public PlayerView(ChivEvtPlayerConnect connectEvt) {
		this.setName(connectEvt.getPlayerName());
		this.setSteamId(connectEvt.getSteamId());
		this.setServerData(connectEvt.getServerData());
	}

	/**
	 * Gets the player representation.
	 *
	 * @return a GUI container ready to be displayed as part of a parent container or null if not yet created
	 */
	public ScrollPane getGuiPlayerContainer() {
		return guiPlayerContainer;
	}

	/**
	 * Adjust the display according to the settings if necessary.
	 */
	public void refreshGuiPlayerContainer() {
		if (guiPlayerContainer == null)
			return;

		ImageView avatarContainer = (ImageView) ((FlowPane) guiPlayerContainer.getContent()).getChildren().get(0);
		CompletableFuture.runAsync(() -> updateAvatar(avatarContainer));
		updateContainer(guiPlayerContainer);
	}

	/**
	 * Retrieves the player GUI container ready to be displayed as part of a parent container.
	 * If it is not yet created, it is created asynchronously.
	 * The callback is executed in the main application thread.
	 *
	 * @param callback
	 *            which fires when the object is created
	 */
	public void requestGuiPlayerContainerAsync(Runnable callback) {
		if (guiPlayerContainer != null) {
			callback.run();
			return;
		}

		CompletableFuture.runAsync(() -> createView()).thenRun(new Runnable() {
			@Override
			public void run() {
				Platform.runLater(callback);
			}
		});
	}

	private void createView() {
		if (steamInfo == null && getSteamId() != null) {
			try {
				steamInfo = new PlayerSummary(getSteamId());
			} catch (IOException e) {
				ChivAdminApp.getApp().log(Txt.errPlayerSumId("" + getSteamId(), e.getMessage()), LogType.ERROR);
			}
		}

		FlowPane player = new FlowPane(Orientation.VERTICAL);
		player.setColumnHalignment(HPos.CENTER);
		player.maxHeightProperty().set(100);

		ImageView avatar = createAvatar();
		if (avatar != null)
			player.getChildren().add(avatar);

		Text name = createName();
		player.getChildren().add(name);

		ContextMenu menu = createPlayerActionContextMenu();

		guiPlayerContainer = createContainerWithContent(player, menu);
	}

	private Text createName() {
		Text txt = new Text(name);
		while (txt.getLayoutBounds().getWidth() > 90) {
			txt.setText(txt.getText().substring(0, txt.getText().length() - 1));
		}
		if (txt.getText().length() != name.length()) {
			txt.setText(txt.getText() + "..");
		}

		return txt;
	}

	private void updateNameForView() {
		if (guiPlayerContainer == null)
			return;

		Text avatarName = (Text) ((FlowPane) guiPlayerContainer.getContent()).getChildren().get(1);
		avatarName.setText(createName().getText());
	}

	private ImageView createAvatar() {
		ImageView imgContainer = new ImageView();
		updateAvatar(imgContainer);

		return imgContainer;
	}

	private void updateAvatar(ImageView imgContainer) {
		if (ChivAdminApp.getApp().getConfig().isDisplayPlayerAvatar()) {
			String imgUrl = "/resources/ChivAssets/icons/chivlogo_icon.png";
			if (steamInfo != null) {
				String avatarUrl = steamInfo.getAvatarMedium();
				if (avatarUrl != null && avatarUrl.length() > 0)
					imgContainer.setImage(new Image(steamInfo.getAvatarMedium(), true));
			} else {
				imgContainer.setPreserveRatio(false);
				imgContainer.fitHeightProperty().set(64);
				imgContainer.fitWidthProperty().set(64);
				imgContainer.setImage(new Image(imgUrl));
			}
		}
		else {
			imgContainer.setImage(null);
		}
	}

	private ContextMenu createPlayerActionContextMenu() {
		ContextMenu menu = new ContextMenu();
		MenuItem kick = new MenuItem("Kick");
		kick.setOnAction(createMenuOnAction(PlayerAction.KICK));

		MenuItem ban = new MenuItem("Ban");
		ban.setOnAction(createMenuOnAction(PlayerAction.BAN));

		MenuItem tmpBan = new MenuItem("TmpBan");
		tmpBan.setOnAction(createMenuOnAction(PlayerAction.TMP_BAN));

		menu.getItems().addAll(kick, ban, tmpBan);

		return menu;
	}

	private EventHandler<ActionEvent> createMenuOnAction(PlayerAction action) {
		return new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				PlayerActionController.createForm(action);
			}

		};
	}

	private ScrollPane createContainerWithContent(FlowPane player, ContextMenu menu) {
		ScrollPane scroll = new ScrollPane();
		updateContainer(scroll);
		scroll.setContent(player);
		scroll.setContextMenu(menu);
		scroll.setOnMouseClicked(ChivAdminApp.getApp().getMainView().createMouseActionForPlayerSelection());

		return scroll;
	}

	private void updateContainer(ScrollPane scroll) {
		int height = ChivAdminApp.getApp().getConfig().isDisplayPlayerAvatar() ? 90 : 20;
		scroll.minHeight(height);
		scroll.prefHeight(height);
		scroll.setMaxHeight(height);
		scroll.minWidth(70);
		scroll.prefWidth(70);
		scroll.setMaxWidth(100);
		scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		scroll.setVbarPolicy(ScrollBarPolicy.NEVER);
	}

	/**
	 * Creates a new DetailsView and sets its content to the details list view.
	 *
	 * @param details
	 */
	public void createDetailsView(ListView<String> details) {
		playerDetails = new PlayerDetailsView(this);
		playerDetails.fillPlayerDetails(details);
	}

	public PlayerDetailsView getPlayerDetails() {
		return playerDetails;
	}

	public LoginData getServerData() {
		return serverData;
	}

	public void setServerData(LoginData serverData) {
		this.serverData = serverData;
	}

	public int getPing() {
		return ping;
	}

	public void setPing(int ping) {
		this.ping = ping;

		Config config = ChivAdminApp.getApp().getConfig();
		if (ping > config.getMaxAutoKickPing()) {
			if (timeSinceExceedingMaxPing == 0)
				timeSinceExceedingMaxPing = System.currentTimeMillis();
			if ((System.currentTimeMillis() - timeSinceExceedingMaxPing) / 1000 > config.getPingTolerance()) {
				if (config.isPingAutokickEnabled() && shouldAutokickFromServer())
					kickFromServer(Txt.autoKickedPing(config.getMaxAutoKickPing()));
			}
		} else {
			timeSinceExceedingMaxPing = 0;
		}

		if (playerDetails != null)
			playerDetails.updateDetails("" + ping, PlayerDetails.PING);
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
		if (playerDetails != null)
			playerDetails.updateDetails("" + score, PlayerDetails.SCORE);
	}

	public int getTeamDamage() {
		return teamDamage;
	}

	public void setTeamDamage(int teamDamage) {
		this.teamDamage = teamDamage;

		Config config = ChivAdminApp.getApp().getConfig();
		if (teamDamage > config.getMaxAutoKickTeamdamage()) {
			if (config.isTeamdamageAutokickEnabled() && shouldAutokickFromServer())
				kickFromServer(Txt.autoKickedTeamdamage(config.getMaxAutoKickTeamdamage()));
		}

		if (playerDetails != null)
			playerDetails.updateDetails("" + teamDamage, PlayerDetails.TEAMDAMAGE);
	}

	public int getIdleTime() {
		return idleTime;
	}

	public void setIdleTime(int idleTime) {
		this.idleTime = idleTime;
		if (playerDetails != null)
			playerDetails.updateDetails("" + idleTime, PlayerDetails.IDLE_TIME);
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
		if (playerDetails != null)
			playerDetails.updateDetails("" + kills, PlayerDetails.KILLS);
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
		if (playerDetails != null)
			playerDetails.updateDetails("" + rank, PlayerDetails.RANK);
	}

	public PlayerSummary getSteamInfo() {
		return steamInfo;
	}

	public void setSteamInfo(PlayerSummary steamInfo) {
		this.steamInfo = steamInfo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		if (playerDetails != null)
			playerDetails.updateDetails(name, PlayerDetails.NICK_NAME);
		updateNameForView();
	}

	public SteamId getSteamId() {
		return steamId;
	}

	public void setSteamId(SteamId steamId) {
		this.steamId = steamId;
	}

	public EAOCFaction getTeam() {
		return team;
	}

	public void setTeam(EAOCFaction team) {
		this.team = team;
		if (playerDetails != null)
			playerDetails.updateDetails("" + team, PlayerDetails.TEAM);
	}

	public void kickFromServer(String reason) {
		MainView control = ChivAdminApp.getApp().getMainView();
		ServerConnection conn = control.getConnectionForLogin(getServerData());

		if (conn != null)
			conn.getCommandMngr().sendKickPlayer(getSteamId(), reason);
	}

	private boolean shouldAutokickFromServer() {
		MainView controller = ChivAdminApp.getApp().getMainView();
		Config config = ChivAdminApp.getApp().getConfig();
		LoginData selectedServerData = controller.getCurrentDisplayedServer() != null ? controller.getCurrentDisplayedServer().getLoginData() : null;

		return config.isApplyAutoKickToAllServers() || (getServerData().equals(selectedServerData));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!PlayerView.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		final PlayerView other = (PlayerView) obj;

		return getSteamId().equals(other.getSteamId());
	}

	@Override
	public int hashCode() {
		return 47 * 7 + getSteamId().hashCode();
	}

	@Override
	public String toString() {
		return name;
	}

}
