package chivrcon.data.events;

import java.io.BufferedInputStream;

import steam.SteamId;
import utils.ParseUtils;


/**
 * Each player's ping is sent periodically.
 * Replaces the old ping event and adds more info.
 */
public class ChivEvtPingExtended extends ChivEvent implements CustomChivEvent {

	private static ChivMessageId ID;

	private SteamId steamId;
	private int ping;
	private int score;
	private int idleTime;
	private int kills;
	private int teamDamageDealt;
	private int rank;

	ChivEvtPingExtended() {}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	void parseDataInternal(BufferedInputStream data) {
		steamId = ParseUtils.parseSteamId(data);
		ping = ParseUtils.parseInt(data);
		score = ParseUtils.parseInt(data);
		idleTime = ParseUtils.parseInt(data);
		kills = ParseUtils.parseInt(data);
		teamDamageDealt = ParseUtils.parseInt(data);
		rank = ParseUtils.parseInt(data);
	}

	public SteamId getSteamId() {
		return steamId;
	}

	public int getPing() {
		return ping;
	}

	public int getScore() {
		return score;
	}

	public int getIdleTime() {
		return idleTime;
	}

	public int getKills() {
		return kills;
	}

	public int getTeamDamageDealt() {
		return teamDamageDealt;
	}

	public int getRank() {
		return rank;
	}

	/**
	 * only used for initialization of the message id
	 *
	 * @param id
	 */
	public static void initId(ChivMessageId id) {
		if(ID == null) {
			ID = id;
			ChivEventFactory.addEvent(ID, ChivEvtPingExtended::new);
		}
	}

}