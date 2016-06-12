package chivrcon.business;

import java.util.HashMap;
import java.util.Map;

import chivrcon.data.ChivMessage.ChivMessageId;
import chivrcon.data.events.ChivEvent;
import chivrcon.data.events.ChivEvtKill;
import chivrcon.data.events.ChivEvtMapChanged;
import chivrcon.data.events.ChivEvtMapList;
import chivrcon.data.events.ChivEvtNameChanged;
import chivrcon.data.events.ChivEvtPing;
import chivrcon.data.events.ChivEvtPingExtended;
import chivrcon.data.events.ChivEvtPlayerChat;
import chivrcon.data.events.ChivEvtPlayerConnect;
import chivrcon.data.events.ChivEvtPlayerDisconnect;
import chivrcon.data.events.ChivEvtRoundEnd;
import chivrcon.data.events.ChivEvtServerConnect;
import chivrcon.data.events.ChivEvtSuicide;
import chivrcon.data.events.ChivEvtTeamChanged;
import presentation.ChivAdminApp;
import presentation.helper.Logger.LogType;
import resources.Txt;

/**
 * Manages messages of type event. Contains callbacks for the various message types received from the server.
 */
public class EventMngr {

	private ServerConnection sc;

	@FunctionalInterface
	private interface MessageProcessor {
		public void process(ChivEvent evt);
	}

	@SuppressWarnings("serial")
	private Map<ChivMessageId, MessageProcessor> msgRelay = new HashMap<ChivMessageId, MessageProcessor>() {
		{
			put(ChivMessageId.SERVER_CONNECT, EventMngr.this::processServerConnect);
			put(ChivMessageId.SERVER_CONNECT_SUCCESS, EventMngr.this::processServerConnectSuccess);
			put(ChivMessageId.PLAYER_CHAT, EventMngr.this::processPlayerChat);
			put(ChivMessageId.PLAYER_CONNECT, EventMngr.this::processPlayerConnect);
			put(ChivMessageId.PLAYER_DISCONNECT, EventMngr.this::processPlayerDisconnect);
			put(ChivMessageId.MAP_CHANGED, EventMngr.this::processMapChanged);
			put(ChivMessageId.ROUND_END, EventMngr.this::processRoundEnd);
			put(ChivMessageId.MAP_LIST, EventMngr.this::processMapList);
			put(ChivMessageId.TEAM_CHANGED, EventMngr.this::processTeamChanged);
			put(ChivMessageId.NAME_CHANGED, EventMngr.this::processNameChanged);
			put(ChivMessageId.KILL, EventMngr.this::processKill);
			put(ChivMessageId.SUICIDE, EventMngr.this::processSuicide);
			put(ChivMessageId.PING, EventMngr.this::processPing);
			put(ChivMessageId.PING_EXTENDED, EventMngr.this::processPingExtended);
		}
	};

	EventMngr(ServerConnection sc) {
		this.sc = sc;
	}

	void receive(ChivEvent evt) {
		msgRelay.get(evt.getId()).process(evt);
	}

	private void processServerConnect(ChivEvent evt) {
		if (!sc.getConnector().isLoggedIn()) {
			ChivEvtServerConnect e = (ChivEvtServerConnect) evt;
			ChivAdminApp.getApp().log("Challenge String received from server: " + e.getChallengeString(), LogType.EVENT, sc.getLoginData());
			ChivAdminApp.getApp().log("Logging in... ", LogType.INFO, sc.getLoginData());
			sc.getCommandMngr().sendPassword(sc.getLoginData().getAdminPassword(), e.getChallengeString());
		} else
			ChivAdminApp.getApp().log(Txt.errConnReceivedMultiLogin(), LogType.EVENT, sc.getLoginData());
	}

	private void processServerConnectSuccess(ChivEvent evt) {
		sc.getConnector().notifyServerConnectSuccess();
		ChivAdminApp.getApp().log("connection successfull", LogType.EVENT, sc.getLoginData());
		ChivAdminApp.getApp().getEventHandler().serverLoggedIn(sc.getLoginData());
	}

	private void processPlayerChat(ChivEvent evt) {
		ChivEvtPlayerChat e = (ChivEvtPlayerChat) evt;
		ChivAdminApp.getApp().log("received " + e.getId() + ": msg=" + e.getMessage() + ", steamId=" + e.getSteamId() + ", teamId=" + e.getTeamId(), LogType.EVENT, sc.getLoginData());
		ChivAdminApp.getApp().getEventHandler().playerChat(e);
	}

	private void processPlayerConnect(ChivEvent evt) {
		ChivEvtPlayerConnect e = (ChivEvtPlayerConnect) evt;
		ChivAdminApp.getApp().log("received " + e.getId() + ": playerName=" + e.getPlayerName() + ", steamId=" + e.getSteamId(), LogType.EVENT, sc.getLoginData());
		if(e.getSteamId().getSteamIdA() != 0) // filter out bots
			ChivAdminApp.getApp().getEventHandler().playerConnected(e);
	}

	private void processPlayerDisconnect(ChivEvent evt) {
		ChivEvtPlayerDisconnect e = (ChivEvtPlayerDisconnect) evt;
		ChivAdminApp.getApp().log("received " + e.getId() + ": steamId=" + e.getSteamId(), LogType.EVENT, sc.getLoginData());
		ChivAdminApp.getApp().getEventHandler().playerDisconnect(e);
	}

	private void processMapChanged(ChivEvent evt) {
		ChivEvtMapChanged e = (ChivEvtMapChanged) evt;
		ChivAdminApp.getApp().log("received " + e.getId() + ": mapName=" + e.getMapName() + ", mapRotationIndex=" + e.getMapRotationIndex(), LogType.EVENT, sc.getLoginData());
		ChivAdminApp.getApp().getEventHandler().mapChanged(e);
	}

	private void processRoundEnd(ChivEvent evt) {
		ChivEvtRoundEnd e = (ChivEvtRoundEnd) evt;
		ChivAdminApp.getApp().log("received " + e.getId() + ": winningTeamId=" + e.getTeamId(), LogType.EVENT, sc.getLoginData());
	}

	private void processMapList(ChivEvent evt) {
		ChivEvtMapList e = (ChivEvtMapList) evt;
		ChivAdminApp.getApp().log("received " + e.getId() + ": mapName=" + e.getMapName(), LogType.EVENT, sc.getLoginData());
	}

	private void processTeamChanged(ChivEvent evt) {
		ChivEvtTeamChanged e = (ChivEvtTeamChanged) evt;
		ChivAdminApp.getApp().log("received " + e.getId() + ": steamId=" + e.getSteamId() + ", teamId=" + e.getTeamId(), LogType.EVENT, sc.getLoginData());
		ChivAdminApp.getApp().getEventHandler().teamChanged(e);
	}

	private void processNameChanged(ChivEvent evt) {
		ChivEvtNameChanged e = (ChivEvtNameChanged) evt;
		ChivAdminApp.getApp().log("received " + e.getId() + ": playerName=" + e.getPlayerName() + ", steamId=" + e.getSteamId(), LogType.EVENT, sc.getLoginData());
		ChivAdminApp.getApp().getEventHandler().nameChanged(e);
	}

	private void processKill(ChivEvent evt) {
		ChivEvtKill e = (ChivEvtKill) evt;
		ChivAdminApp.getApp().log("received " + e.getId() + ": killerSteamId=" + e.getKillerSteamId() + ", victimSteamId=" + e.getVictimSteamId() + ", weaponName=" + e.getWeaponName(), LogType.EVENT, sc.getLoginData());
	}

	private void processSuicide(ChivEvent evt) {
		ChivEvtSuicide e = (ChivEvtSuicide) evt;
		ChivAdminApp.getApp().log("received " + e.getId() + ": steamId=" + e.getSteamId(), LogType.EVENT, sc.getLoginData());
	}

	private void processPing(ChivEvent evt) {
		ChivEvtPing e = (ChivEvtPing) evt;
		ChivAdminApp.getApp().log("received " + e.getId() + ": steamId=" + e.getSteamId() + ", ping=" + e.getPing(), LogType.EVENT, sc.getLoginData());
		ChivAdminApp.getApp().getEventHandler().ping(e);
	}

	private void processPingExtended(ChivEvent evt) {
		ChivEvtPingExtended e = (ChivEvtPingExtended) evt;
		ChivAdminApp.getApp()
				.log("received " + e.getId() + ": steamId=" + e.getSteamId() + ", ping=" + e.getPing() + ", kills=" + e.getKills() + ", idleTime="
						+ e.getIdleTime() + ", rank=" + e.getRank() + ", ping=" + e.getScore() + ", team damage dealt=" + e.getTeamDamageDealt(),
				LogType.EVENT, sc.getLoginData());
		ChivAdminApp.getApp().getEventHandler().pingExtended(e);
	}

}
