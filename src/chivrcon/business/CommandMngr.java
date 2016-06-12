package chivrcon.business;

import chivrcon.data.commands.*;
import chivrcon.data.commands.ChivCmdConsoleCommand.ConsoleCommandScope;
import presentation.ChivAdminApp;
import presentation.helper.Logger.LogType;
import steam.SteamId;

/**
 * Responsible for sending commands to the remote server.
 *
 */
public class CommandMngr {

	private ServerConnection sc;

	CommandMngr(ServerConnection sc) {
		this.sc = sc;
	}

	void sendPassword(String adminPass, String challengeString) {
		ChivCmdPassword cmd = new ChivCmdPassword(adminPass, challengeString);
		sc.getConnector().sendCommand(cmd);
	}

	public void sendSayAll(String msg) {
		ChivCmdSayAll cmd = new ChivCmdSayAll(msg);
		sc.getConnector().sendCommand(cmd);
	}

	public void sendSayAllBig(String msg) {
		ChivCmdSayAllBig cmd = new ChivCmdSayAllBig(msg);
		sc.getConnector().sendCommand(cmd);
	}

	public void sendSay(String msg, SteamId steamId) {
		ChivCmdSay cmd = new ChivCmdSay(msg, steamId);
		sc.getConnector().sendCommand(cmd);
	}

	public void sendChangeMap(String mapName) {
		if (mapName != null && mapName.length() != 0) {
			ChivCmdChangeMap cmd = new ChivCmdChangeMap(mapName);
			sc.getConnector().sendCommand(cmd);
		}
	}

	public void sendRotateMap() {
		ChivCmdRotateMap cmd = new ChivCmdRotateMap();
		sc.getConnector().sendCommand(cmd);
	}

	public void sendKickPlayer(SteamId steamId, String reason) {
		ChivAdminApp.getApp().log("Kicking Player (" + steamId + "). Reason: " + reason, LogType.COMMAND, sc.getLoginData());
		ChivCmdKickPlayer cmd = new ChivCmdKickPlayer(steamId, reason);
		sc.getConnector().sendCommand(cmd);
	}

	public void sendTmpBanPlayer(SteamId steamId, String reason, int seconds) {
		ChivCmdTmpBanPlayer cmd = new ChivCmdTmpBanPlayer(steamId, reason, seconds);
		sc.getConnector().sendCommand(cmd);
	}

	public void sendBanPlayer(SteamId steamId, String reason) {
		ChivCmdBanPlayer cmd = new ChivCmdBanPlayer(steamId, reason);
		sc.getConnector().sendCommand(cmd);
	}

	public void sendUnbanPlayer(SteamId steamId) {
		if (steamId != null) {
			ChivCmdUnbanPlayer cmd = new ChivCmdUnbanPlayer(steamId);
			sc.getConnector().sendCommand(cmd);
		}
	}

	public void sendChangeScore(SteamId steamId, int score) {
		if (steamId != null && score != 0) {
			ChivCmdChangeScore cmd = new ChivCmdChangeScore(steamId, score);
			sc.getConnector().sendCommand(cmd);
		}
	}

	public void sendKillPlayer(SteamId steamId) {
		if (steamId != null) {
			ChivCmdKillPlayer cmd = new ChivCmdKillPlayer(steamId);
			sc.getConnector().sendCommand(cmd);
		}
	}

	public void sendInebriate(SteamId steamId) {
		if (steamId != null) {
			ChivCmdInebriate cmd = new ChivCmdInebriate(steamId);
			sc.getConnector().sendCommand(cmd);
		}
	}

	public void sendChangeGamePassword(String newPw) {
		if (newPw != null) {
			ChivCmdChangeGamePassword cmd = new ChivCmdChangeGamePassword(newPw);
			sc.getConnector().sendCommand(cmd);
		}
	}

	public void sendConsoleCommand(SteamId steamId, ConsoleCommandScope scope, String consoleCmd) {
		if (steamId != null && scope != null && consoleCmd != null && consoleCmd.length() > 0) {
			ChivCmdConsoleCommand cmd = new ChivCmdConsoleCommand(steamId, scope, consoleCmd);
			sc.getConnector().sendCommand(cmd);
		}
	}
}
