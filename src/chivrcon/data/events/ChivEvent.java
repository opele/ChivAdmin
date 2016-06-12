package chivrcon.data.events;

import java.io.BufferedInputStream;

import chivrcon.data.ChivMessage;
import chivrcon.data.LoginData;

/**
 * ChivEvents are messages which are received from the server and need to be
 * parsed.
 */
public abstract class ChivEvent implements ChivMessage {

	private LoginData serverData;

	/**
	 * Parses the specific data depending on the message type. Expects only to
	 * read the message part of the received package.
	 */
	abstract void parseDataInternal(BufferedInputStream socketInput);

	/**
	 * Retrieves data about the server which this message was received from.
	 *
	 * @return
	 */
	public LoginData getServerData() {
		return serverData;
	}

	/**
	 * Stores data about the server which this message was received from.
	 *
	 * @param serverData
	 */
	public void setServerData(LoginData serverData) {
		this.serverData = serverData;
	}

}
