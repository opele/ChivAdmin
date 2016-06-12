package chivrcon.data.commands;

import steam.SteamId;
import utils.ArrayUtils;
import utils.EncodeUtils;



/**
 * Ban the given player on the game server.
 */
public class ChivCmdBanPlayer extends ChivCommand {

	private static ChivMessageId ID;

	private final SteamId steamId;
	private final String reason;

	public ChivCmdBanPlayer(SteamId steamId, String reason) {
		this.steamId = steamId;
		this.reason = reason;
	}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	public byte[] getEncodedMessageBody() {
		byte[] sId = EncodeUtils.encodeSteamId(steamId);
		byte[] msg = EncodeUtils.encodeString(reason);

		return ArrayUtils.appendByteArrays(sId, msg);
	}

	public SteamId getSteamId() {
		return steamId;
	}

	public String getReason() {
		return reason;
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