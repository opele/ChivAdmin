package chivrcon.data.events;

import java.io.BufferedInputStream;

import utils.ParseUtils;


/**
 * Sent when a connection attempt is received.  Use the challenge string and send a PASSWORD message.
 */
public class ChivEvtServerConnect extends ChivEvent {

	private static ChivMessageId ID;

	private String challengeString;

	ChivEvtServerConnect() {}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	void parseDataInternal(BufferedInputStream data) {
		challengeString = ParseUtils.parseString(data);
	}

	public String getChallengeString() {
		return challengeString;
	}

	/**
	 * only used for initialization of the message id
	 *
	 * @param id
	 */
	public static void initId(ChivMessageId id) {
		if(ID == null) {
			ID = id;
			ChivEventFactory.addEvent(ID, ChivEvtServerConnect::new);
		}
	}

}