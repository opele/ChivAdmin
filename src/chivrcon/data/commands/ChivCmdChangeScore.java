package chivrcon.data.commands;

import chivrcon.data.events.CustomChivEvent;
import steam.SteamId;
import utils.ArrayUtils;
import utils.EncodeUtils;



/**
 * Change the score of the given player.
 */
public class ChivCmdChangeScore extends ChivCommand implements CustomChivEvent {

	private static ChivMessageId ID;

	private final SteamId steamId;
	private final int score;

	public ChivCmdChangeScore(SteamId steamId, int score) {
		this.steamId = steamId;
		this.score = score;
	}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	public byte[] getEncodedMessageBody() {
		byte[] sId = EncodeUtils.encodeSteamId(steamId);
		byte[] s = EncodeUtils.encodeInt(score);

		return ArrayUtils.appendByteArrays(sId, s);
	}

	public SteamId getSteamId() {
		return steamId;
	}

	public int getScore() {
		return score;
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