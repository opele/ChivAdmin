package chivrcon.data.commands;

import utils.EncodeUtils;



/**
 * Broadcast a message to all players.
 */
public class ChivCmdSayAll extends ChivCommand {

	private static ChivMessageId ID;

	private final String message;

	public ChivCmdSayAll(String message) {
		this.message = message;
	}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	public byte[] getEncodedMessageBody() {
		return EncodeUtils.encodeString(message);
	}

	public String getMessage() {
		return message;
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