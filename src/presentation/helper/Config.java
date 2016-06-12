package presentation.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import chivrcon.data.LoginData;
import presentation.ChivAdminApp;
import presentation.data.ServerView;
import presentation.helper.Logger.LogType;
import resources.Txt;
import utils.SecurityUtils;

/**
 * Stores configuration parameters for the app.
 */
public class Config {

	private static final String CONFIG_FILENAME = "ChivAdminConfig.txt";
	private static final Class<ChivAdminApp> USER_NODE_FOR_PACKAGE = ChivAdminApp.class;

	private static final String CONF_SERVERS = "servers";
	private static final String SERVER_SEPREATOR = "@";
	private static final String SERVER_PARAM_SEPERATOR = "|";
	private static final int SERVER_PARAM_NUMBER = 3;

	private static final String CONF_DISPLAY_PLAYER_AVATAR = "display_player_avatar";
	private static final String CONF_APPLY_TO_ALL_SERVERS = "apply_autokick_to_all_servers";
	private static final String CONF_PING_AUTOKICK_ENABLED = "ping_autokick_enabled";
	private static final String CONF_TEAMDAMAGE_AUTOKICK_ENABLED = "teamdamage_autokick_enabled";
	private static final String CONF_MAX_AUTOKICK_PING = "max_autokick_ping";
	private static final String CONF_MAX_AUTOKICK_TEAMDAMAGE = "max_autokick_teamdamage";
	private static final String CONF_PING_TOLERANCE = "ping_tolerance";

	private final Preferences config;

	public Config() {
		System.setProperty("java.util.prefs.PreferencesFactory", FilePreferencesFactory.class.getName());
		System.setProperty(FilePreferencesFactory.SYSTEM_PROPERTY_FILE, CONFIG_FILENAME);
		config = Preferences.userNodeForPackage(USER_NODE_FOR_PACKAGE);
	}

	public boolean isDisplayPlayerAvatar() {
		return config.getBoolean(CONF_DISPLAY_PLAYER_AVATAR, true);
	}

	public void setDisplayPlayerAvatar(boolean isDisplayPlayerAvatar) {
		config.putBoolean(CONF_DISPLAY_PLAYER_AVATAR, isDisplayPlayerAvatar);
	}

	public boolean isApplyAutoKickToAllServers() {
		return config.getBoolean(CONF_APPLY_TO_ALL_SERVERS, true);
	}

	public void setApplyAutoKickToAllServers(boolean isApplyToAll) {
		config.putBoolean(CONF_APPLY_TO_ALL_SERVERS, isApplyToAll);
	}

	public boolean isPingAutokickEnabled() {
		return config.getBoolean(CONF_PING_AUTOKICK_ENABLED, false);
	}

	public void setPingAutokickEnabled(boolean isEnabled) {
		config.putBoolean(CONF_PING_AUTOKICK_ENABLED, isEnabled);
	}

	public boolean isTeamdamageAutokickEnabled() {
		return config.getBoolean(CONF_TEAMDAMAGE_AUTOKICK_ENABLED, false);
	}

	public void setTeamdamageAutokickEnabled(boolean isEnabled) {
		config.putBoolean(CONF_TEAMDAMAGE_AUTOKICK_ENABLED, isEnabled);
	}

	public int getMaxAutoKickPing() {
		return config.getInt(CONF_MAX_AUTOKICK_PING, 0);
	}

	public void setMaxAutoKickPing(int ping) {
		config.putInt(CONF_MAX_AUTOKICK_PING, ping);
	}

	public int getMaxAutoKickTeamdamage() {
		return config.getInt(CONF_MAX_AUTOKICK_TEAMDAMAGE, 0);
	}

	public void setMaxAutoKickTeamdamage(int dmg) {
		config.putInt(CONF_MAX_AUTOKICK_TEAMDAMAGE, dmg);
	}

	public int getPingTolerance() {
		return config.getInt(CONF_PING_TOLERANCE, 0);
	}

	public void setPingTolerance(int tolerance) {
		config.putInt(CONF_PING_TOLERANCE, tolerance);
	}

	public void putServer(ServerView server) {
		String serverConfg = config.get(CONF_SERVERS, "");
		serverConfg += getServerConfigValue(server);
		config.put(CONF_SERVERS, serverConfg);
	}

	public void removeServer(ServerView server) {
		String serverConfig = config.get(CONF_SERVERS, "");
		String toDelete = getServerConfigValue(server);

		if (serverConfig.contains(toDelete)) {
			// note: Pattern.quote(serverConfig) does not work here
			config.put(CONF_SERVERS, serverConfig.replace(toDelete, ""));
		}
	}

	public List<ServerView> getServers() {
		List<ServerView> servers = new ArrayList<>();
		String serverConfig = config.get(CONF_SERVERS, "");

		if (serverConfig.length() > 0 && serverConfig.contains(SERVER_SEPREATOR)) {
			for (String s : serverConfig.split(SERVER_SEPREATOR)) {
				if (s.length() > 0 && s.contains(SERVER_PARAM_SEPERATOR)) {
					String[] serverPara = s.split(Pattern.quote(SERVER_PARAM_SEPERATOR));
					if (serverPara.length == SERVER_PARAM_NUMBER) {
						try {
							ServerView server = new ServerView(
									new LoginData(serverPara[0], Integer.parseInt(serverPara[1]), SecurityUtils.decryptDESede(serverPara[2])),
									"n.a.");
							server.isLoggedInProperty()
									.addListener(ChivAdminApp.getApp().getMainView().getIsLoggedInListener(server));
							servers.add(server);
						} catch (Exception e) {
							ChivAdminApp.getApp().log(Txt.errLoadServer(serverPara[0]), LogType.WARN);
						}
					}
				}
			}
		}

		return servers;
	}

	private String getServerConfigValue(ServerView server) {
		String serverPara = "";

		String ip = server.getLoginData().getAddress().getHostAddress();
		int port = server.getLoginData().getPort();
		String adminPw = server.getLoginData().getAdminPassword();
		adminPw = SecurityUtils.encryptDESede(adminPw);
		serverPara += ip + SERVER_PARAM_SEPERATOR + port + SERVER_PARAM_SEPERATOR + adminPw + SERVER_SEPREATOR;

		return serverPara;
	}
}
