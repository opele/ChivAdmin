package chivrcon.data.commands;

import utils.EncodeUtils;
import utils.SecurityUtils;

/**
 * Send after receiving SERVER_CONNECT message.
 */
public class ChivCmdPassword extends ChivCommand {

	private static ChivMessageId ID;

	private final String token;

	public ChivCmdPassword(String password, String challengeString) {
		token = SecurityUtils.encryptSHA1((password == null ? "" : password) + challengeString).toUpperCase();
	}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	public byte[] getEncodedMessageBody() {
		return EncodeUtils.encodeString(token);
	}

	/**
	 * The token consist of the password concatenated with the challenge string (from SERVER_CONNECT).
	 * Encoded using SHA-1 and sent as a 40 character hex string.
	 */
	public String getToken() {
		return token;
	}

	/**
	 * only used for initialization of the message id
	 *
	 * @param id
	 */
	public static void initId(ChivMessageId id) {
		if (ID == null) {
			ID = id;
		}
	}

}