package chivrcon.data.events;

import java.io.BufferedInputStream;

import utils.ParseUtils;


/**
 * Sent when a round is over.
 */
public class ChivEvtRoundEnd extends ChivEvent {

	private static ChivMessageId ID;

	private int winningTeamId;

	ChivEvtRoundEnd() {}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	void parseDataInternal(BufferedInputStream data) {
		winningTeamId = ParseUtils.parseInt(data);
	}

	public int getTeamId() {
		return winningTeamId;
	}

	/**
	 * only used for initialization of the message id
	 *
	 * @param id
	 */
	public static void initId(ChivMessageId id) {
		if(ID == null) {
			ID = id;
			ChivEventFactory.addEvent(ID, ChivEvtRoundEnd::new);
		}
	}

}