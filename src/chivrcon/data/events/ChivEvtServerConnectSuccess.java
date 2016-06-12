package chivrcon.data.events;

import java.io.BufferedInputStream;


/**
 * Sent after the password has been accepted.
 * Contains no data.
 */
public class ChivEvtServerConnectSuccess extends ChivEvent {

	private static ChivMessageId ID;

	ChivEvtServerConnectSuccess() {}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	void parseDataInternal(BufferedInputStream data) {}

	/**
	 * only used for initialization of the message id
	 *
	 * @param id
	 */
	public static void initId(ChivMessageId id) {
		if(ID == null) {
			ID = id;
			ChivEventFactory.addEvent(ID, ChivEvtServerConnectSuccess::new);
		}
	}
}