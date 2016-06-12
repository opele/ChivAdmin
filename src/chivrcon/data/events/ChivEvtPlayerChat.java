package chivrcon.data.events;

import java.io.BufferedInputStream;

import steam.SteamId;
import utils.ParseUtils;


/**
 * Sent when a player types a message in the in-game chat.
 */
public class ChivEvtPlayerChat extends ChivEvent {

	private static ChivMessageId ID;

	private SteamId steamId;
	private String message;
	private int teamId;

	ChivEvtPlayerChat() {}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	void parseDataInternal(BufferedInputStream data) {
		steamId = ParseUtils.parseSteamId(data);
		message = ParseUtils.parseString(data);
		teamId = ParseUtils.parseInt(data);
	}

	public SteamId getSteamId() {
		return steamId;
	}

	public String getMessage() {
		return message;
	}

	public int getTeamId() {
		return teamId;
	}

	/**
	 * only used for initialization of the message id
	 *
	 * @param id
	 */
	public static void initId(ChivMessageId id) {
		if(ID == null) {
			ID = id;
			ChivEventFactory.addEvent(ID, ChivEvtPlayerChat::new);
		}
	}

}