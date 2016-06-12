package chivrcon.data.commands;

import utils.EncodeUtils;



/**
 * Load the given map.
 */
public class ChivCmdChangeMap extends ChivCommand {

	private static ChivMessageId ID;

	private final String mapName;

	public ChivCmdChangeMap(String message) {
		this.mapName = message;
	}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	public byte[] getEncodedMessageBody() {
		return EncodeUtils.encodeString(mapName);
	}

	public String getMapName() {
		return mapName;
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