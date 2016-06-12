package chivrcon.data.events;

import java.io.BufferedInputStream;

import utils.ParseUtils;


/**
 * Sent when a new map has started.
 */
public class ChivEvtMapChanged extends ChivEvent {

	private static ChivMessageId ID;

	private int mapRotationIndex;
	private String mapName;

	ChivEvtMapChanged() {}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	void parseDataInternal(BufferedInputStream data) {
		mapRotationIndex = ParseUtils.parseInt(data);
		mapName = ParseUtils.parseString(data);
	}

	public int getMapRotationIndex() {
		return mapRotationIndex;
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
			ChivEventFactory.addEvent(ID, ChivEvtMapChanged::new);
		}
	}

}