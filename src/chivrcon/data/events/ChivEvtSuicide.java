package chivrcon.data.events;

import java.io.BufferedInputStream;

import steam.SteamId;
import utils.ParseUtils;


/**
 * Sent when a player commits suicide.
 */
public class ChivEvtSuicide extends ChivEvent {

	private static ChivMessageId ID;

	private SteamId steamId;

	ChivEvtSuicide() {}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	void parseDataInternal(BufferedInputStream data) {
		steamId = ParseUtils.parseSteamId(data);
	}

	public SteamId getSteamId() {
		return steamId;
	}

	/**
	 * only used for initialization of the message id
	 *
	 * @param id
	 */
	public static void initId(ChivMessageId id) {
		if(ID == null) {
			ID = id;
			ChivEventFactory.addEvent(ID, ChivEvtSuicide::new);
		}
	}

}