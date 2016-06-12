package chivrcon.data.events;

import java.io.BufferedInputStream;

import steam.SteamId;
import utils.ParseUtils;


/**
 * Sent when a player joins the game.
 */
public class ChivEvtPlayerConnect extends ChivEvent {

	private static ChivMessageId ID;

	private SteamId steamId;
	private String playerName;

	ChivEvtPlayerConnect() {}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	void parseDataInternal(BufferedInputStream data) {
		steamId = ParseUtils.parseSteamId(data);
		playerName = ParseUtils.parseString(data);
	}

	public SteamId getSteamId() {
		return steamId;
	}

	public String getPlayerName() {
		return playerName;
	}

	/**
	 * only used for initialization of the message id
	 *
	 * @param id
	 */
	public static void initId(ChivMessageId id) {
		if(ID == null) {
			ID = id;
			ChivEventFactory.addEvent(ID, ChivEvtPlayerConnect::new);
		}
	}

}