package chivrcon.data.commands;

import chivrcon.data.events.CustomChivEvent;
import steam.SteamId;
import utils.EncodeUtils;



/**
 * Kill the given player.
 */
public class ChivCmdKillPlayer extends ChivCommand implements CustomChivEvent {

	private static ChivMessageId ID;

	private final SteamId steamId;

	public ChivCmdKillPlayer(SteamId steamId) {
		this.steamId = steamId;
	}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	public byte[] getEncodedMessageBody() {
		byte[] sId = EncodeUtils.encodeSteamId(steamId);

		return sId;
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
		}
	}

}