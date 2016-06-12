package chivrcon.data.events;

import java.io.BufferedInputStream;

import utils.ParseUtils;


/**
 * The map list is sent one time after SERVER_CONNECT_SUCCESS.  Each map name is sent as an individual message.
 */
public class ChivEvtMapList extends ChivEvent {

	private static ChivMessageId ID;

	private String mapName;

	ChivEvtMapList() {}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	void parseDataInternal(BufferedInputStream data) {
		mapName = ParseUtils.parseString(data);
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
			ChivEventFactory.addEvent(ID, ChivEvtMapList::new);
		}
	}

}