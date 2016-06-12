package chivrcon.data.commands;

import chivrcon.data.events.CustomChivEvent;
import utils.EncodeUtils;



/**
 * Sets the game password with takes effect without a server restart
 */
public class ChivCmdChangeGamePassword extends ChivCommand implements CustomChivEvent {

	private static ChivMessageId ID;

	private final String newPw;

	public ChivCmdChangeGamePassword(String newPw) {
		this.newPw = newPw;
	}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	public byte[] getEncodedMessageBody() {
		byte[] pw = EncodeUtils.encodeString(newPw);

		return pw;
	}

	public String getPassword() {
		return newPw;
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