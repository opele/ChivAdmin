package chivrcon.data.commands;

import chivrcon.data.events.CustomChivEvent;
import steam.SteamId;
import utils.ArrayUtils;
import utils.EncodeUtils;



/**
 * Sends a console command to the server for execution on either a specific player, all players or the game.
 */
public class ChivCmdConsoleCommand extends ChivCommand implements CustomChivEvent {

	public enum ConsoleCommandScope {
		SCOPE_GAME,
		SCOPE_PLAYER,
		SCOPE_ALL_PLAYERS
	}

	private static ChivMessageId ID;

	private final SteamId steamId;
	private final ConsoleCommandScope scope;
	private final String consoleCommand;

	public ChivCmdConsoleCommand(SteamId steamId, ConsoleCommandScope scope, String consoleCommand) {
		this.steamId = steamId;
		this.scope = scope;
		this.consoleCommand = consoleCommand;
	}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	public byte[] getEncodedMessageBody() {
		byte[] sId = EncodeUtils.encodeSteamId(steamId);
		byte[] execScope = EncodeUtils.encodeInt(scope.ordinal());
		byte[] cmd = EncodeUtils.encodeString(consoleCommand);

		return ArrayUtils.appendByteArrays(ArrayUtils.appendByteArrays(sId, execScope), cmd);
	}

	public SteamId getSteamId() {
		return steamId;
	}

	public ConsoleCommandScope getScope() {
		return scope;
	}

	public String getConsoleCommand() {
		return consoleCommand;
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