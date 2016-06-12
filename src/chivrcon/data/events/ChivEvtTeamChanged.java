package chivrcon.data.events;

import java.io.BufferedInputStream;

import steam.SteamId;
import utils.ParseUtils;


/**
 * Sent when a player joins a team.
 */
public class ChivEvtTeamChanged extends ChivEvent {

	public enum EAOCFaction {
		AGATHA,
		MASON,
		NPC,
		NONE,
		ALL,
		FFA
	};

	private static ChivMessageId ID;

	private SteamId steamId;
	private EAOCFaction teamId;

	ChivEvtTeamChanged() {}

	@Override
	public ChivMessageId getId() {
		return ID;
	}

	@Override
	void parseDataInternal(BufferedInputStream data) {
		steamId = ParseUtils.parseSteamId(data);
		teamId = EAOCFaction.values()[ParseUtils.parseInt(data)];
	}

	public SteamId getSteamId() {
		return steamId;
	}

	public EAOCFaction getTeamId() {
		return teamId;
	}

	/**
	 * only used for initialization of the message id
	 *
	 * @param id
	 */
	public static void initId(ChivMessageId id) {
		if(ID == null) {
			ID = id;
			ChivEventFactory.addEvent(ID, ChivEvtTeamChanged::new);
		}
	}

}