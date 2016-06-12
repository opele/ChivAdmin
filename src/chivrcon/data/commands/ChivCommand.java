package chivrcon.data.commands;

import chivrcon.data.ChivMessage;
import utils.ArrayUtils;
import utils.EncodeUtils;

/**
 * ChivCommands are messages sent to the server after being packed/ encoded.
 *
 */
public abstract class ChivCommand implements ChivMessage {

	/**
	 * Packs the specific data depending on the message type.
	 * Header format:
	 * 1. Short (2 Bytes) Message Type
	 * 2. Int (4 Bytes) Size of Data
	 */
	public byte[] getEncodedData() {
		byte[] msgType = EncodeUtils.encodeShort((short)getId().ordinal());
		byte[] msgBody = getEncodedMessageBody();
		byte[] msgSize = EncodeUtils.encodeInt(msgBody != null ? msgBody.length : 0);

		return ArrayUtils.appendByteArrays(ArrayUtils.appendByteArrays(msgType, msgSize), msgBody);
	}

	/**
	 * Packs only the message part without the head.
	 */
	abstract byte[] getEncodedMessageBody();

}
