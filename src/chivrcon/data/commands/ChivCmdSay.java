package chivrcon.data.commands;

import steam.SteamId;
import utils.ArrayUtils;
import utils.EncodeUtils;



/**
 * Display a message to a given player.
 */
public class ChivCmdSay extends ChivCommand {

	private static ChivMessageId ID;

	private final SteamId steamId;
	private final String message;

	public ChivCmdSay(String message, SteamId steamId) {
		this.steamId = steamId;
		this.message = message;
	}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	public byte[] getEncodedMessageBody() {
		byte[] sId = EncodeUtils.encodeSteamId(steamId);
		byte[] msg = EncodeUtils.encodeString(message);

		return ArrayUtils.appendByteArrays(sId, msg);
	}

	public String getMessage() {
		return message;
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