package chivrcon.data.commands;

import steam.SteamId;
import utils.EncodeUtils;



/**
 * Unban the given player on the game server.
 */
public class ChivCmdUnbanPlayer extends ChivCommand {

	private static ChivMessageId ID;

	private final SteamId steamId;

	public ChivCmdUnbanPlayer(SteamId steamId) {
		this.steamId = steamId;
	}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	public byte[] getEncodedMessageBody() {
		return EncodeUtils.encodeSteamId(steamId);
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