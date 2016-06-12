package chivrcon.data.events;

import java.io.BufferedInputStream;

import steam.SteamId;
import utils.ParseUtils;


/**
 * Each player's ping is sent periodically.
 */
public class ChivEvtPing extends ChivEvent {

	private static ChivMessageId ID;

	private SteamId steamId;
	private int ping;

	ChivEvtPing() {}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	void parseDataInternal(BufferedInputStream data) {
		steamId = ParseUtils.parseSteamId(data);
		ping = ParseUtils.parseInt(data);
	}

	public SteamId getSteamId() {
		return steamId;
	}

	public int getPing() {
		return ping;
	}

	/**
	 * only used for initialization of the message id
	 *
	 * @param id
	 */
	public static void initId(ChivMessageId id) {
		if(ID == null) {
			ID = id;
			ChivEventFactory.addEvent(ID, ChivEvtPing::new);
		}
	}

}