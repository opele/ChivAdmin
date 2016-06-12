package chivrcon.data.commands;

import steam.SteamId;
import utils.ArrayUtils;
import utils.EncodeUtils;



/**
 * Temporarily ban the given player on the game server.
 */
public class ChivCmdTmpBanPlayer extends ChivCommand {

	private static ChivMessageId ID;

	private final SteamId steamId;
	private final String reason;
	private final int durationInSeconds;

	public ChivCmdTmpBanPlayer(SteamId steamId, String reason, int durationInSeconds) {
		this.steamId = steamId;
		this.reason = reason;
		this.durationInSeconds = durationInSeconds;
	}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	public byte[] getEncodedMessageBody() {
		byte[] sId = EncodeUtils.encodeSteamId(steamId);
		byte[] msg = EncodeUtils.encodeString(reason);
		byte[] dur = EncodeUtils.encodeInt(durationInSeconds);

		return ArrayUtils.appendByteArrays(ArrayUtils.appendByteArrays(sId, msg), dur);
	}

	public SteamId getSteamId() {
		return steamId;
	}

	public String getReason() {
		return reason;
	}

	public int getDurationInSeconds() {
		return durationInSeconds;
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