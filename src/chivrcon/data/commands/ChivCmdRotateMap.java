package chivrcon.data.commands;




/**
 * Load the next map in the rotation. No data.
 */
public class ChivCmdRotateMap extends ChivCommand {

	private static ChivMessageId ID;

	public ChivCmdRotateMap() { }

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	public byte[] getEncodedMessageBody() {
		return null;
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