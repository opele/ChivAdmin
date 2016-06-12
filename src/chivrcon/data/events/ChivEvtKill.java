package chivrcon.data.events;

import java.io.BufferedInputStream;

import steam.SteamId;
import utils.ParseUtils;


/**
 * Sent when a player kills another player.
 */
public class ChivEvtKill extends ChivEvent {

	private static ChivMessageId ID;

	private SteamId killerSteamId;
	private SteamId victimSteamId;
	private String weaponName;

	ChivEvtKill() {}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	void parseDataInternal(BufferedInputStream data) {
		killerSteamId = ParseUtils.parseSteamId(data);
		victimSteamId = ParseUtils.parseSteamId(data);
		weaponName = ParseUtils.parseString(data);
	}

	public SteamId getKillerSteamId() {
		return killerSteamId;
	}

	public SteamId getVictimSteamId() {
		return victimSteamId;
	}

	public String getWeaponName() {
		return weaponName;
	}

	/**
	 * only used for initialization of the message id
	 *
	 * @param id
	 */
	public static void initId(ChivMessageId id) {
		if(ID == null) {
			ID = id;
			ChivEventFactory.addEvent(ID, ChivEvtKill::new);
		}
	}

}