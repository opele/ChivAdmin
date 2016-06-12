package chivrcon.data.events;

import java.io.BufferedInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import chivrcon.data.ChivMessage.ChivMessageId;
import chivrcon.data.LoginData;
import resources.Txt;
import utils.ParseUtils;

/**
 * Creates the data representation of the received event. Use the public parse method of the
 * factory to retrieve ready-to-read data from a stream.
 */
public class ChivEventFactory {

	private static final Map<ChivMessageId, Supplier<ChivEvent>> CHIV_EVENTS = new HashMap<ChivMessageId, Supplier<ChivEvent>>();

	static void addEvent(ChivMessageId id, Supplier<ChivEvent> constructor) {
		CHIV_EVENTS.put(id, constructor);
	}

	/**
	 * Parses the package data and returns the corresponding event object. It
	 * is expected that the data is ready to be read.
	 *
	 * @param raw data
	 * @return corresponding message object
	 */
	public static ChivEvent parseData(BufferedInputStream socketInput, LoginData serverData) {
		ChivMessageId id = parseEventId(socketInput);
		ChivEvent chivEvt = CHIV_EVENTS.get(id).get();
		chivEvt.setServerData(serverData);
		int msgSize = ParseUtils.parseInt(socketInput);

		try {
			chivEvt.parseDataInternal(socketInput);
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException(Txt.errParseMalformed(id.toString(), msgSize), e);
		}

		return chivEvt;
	}

	private static ChivMessageId parseEventId(BufferedInputStream socketInput) {
		short msgId = ParseUtils.parseShort(socketInput);
		if (!ChivMessageId.containsId(msgId)) {
			throw new IllegalArgumentException(Txt.errParseUnknownType(msgId));
		}

		return ChivMessageId.values()[msgId];
	}

}
