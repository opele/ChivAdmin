package chivrcon.data;

import java.util.Arrays;

import chivrcon.data.commands.*;
import chivrcon.data.events.*;



/**
 * Represent all Chivalry RCon messages.
 */
public interface ChivMessage {

	/**
	 * Identifies the type of this message defined by the Chivalry RCon protocol.
	 *
	 * @return integer value
	 */
	public abstract ChivMessageId getId();

	interface ChivMsgInit {
		void initIdForMsg();
	}

	public enum ChivMessageId implements ChivMsgInit {

		SERVER_CONNECT {
			@Override
			public void initIdForMsg() {
				ChivEvtServerConnect.initId(this);
			}
		},

		SERVER_CONNECT_SUCCESS {
			@Override
			public void initIdForMsg() {
				ChivEvtServerConnectSuccess.initId(this);
			}
		},

		PASSWORD {
			@Override
			public void initIdForMsg() {
				ChivCmdPassword.initId(this);
			}
		},

		PLAYER_CHAT {
			@Override
			public void initIdForMsg() {
				ChivEvtPlayerChat.initId(this);
			}
		},

		PLAYER_CONNECT {
			@Override
			public void initIdForMsg() {
				ChivEvtPlayerConnect.initId(this);
			}
		},

		PLAYER_DISCONNECT {
			@Override
			public void initIdForMsg() {
				ChivEvtPlayerDisconnect.initId(this);
			}
		},

		SAY_ALL {
			@Override
			public void initIdForMsg() {
				ChivCmdSayAll.initId(this);
			}
		},

		SAY_ALL_BIG {
			@Override
			public void initIdForMsg() {
				ChivCmdSayAllBig.initId(this);
			}
		},

		SAY {
			@Override
			public void initIdForMsg() {
				ChivCmdSay.initId(this);
			}
		},

		MAP_CHANGED {
			@Override
			public void initIdForMsg() {
				ChivEvtMapChanged.initId(this);
			}
		},

		MAP_LIST {
			@Override
			public void initIdForMsg() {
				ChivEvtMapList.initId(this);
			}
		},

		CHANGE_MAP {
			@Override
			public void initIdForMsg() {
				ChivCmdChangeMap.initId(this);
			}
		},

		ROTATE_MAP {
			@Override
			public void initIdForMsg() {
				ChivCmdRotateMap.initId(this);
			}
		},

		TEAM_CHANGED {
			@Override
			public void initIdForMsg() {
				ChivEvtTeamChanged.initId(this);
			}
		},

		NAME_CHANGED {
			@Override
			public void initIdForMsg() {
				ChivEvtNameChanged.initId(this);
			}
		},

		KILL {
			@Override
			public void initIdForMsg() {
				ChivEvtKill.initId(this);
			}
		},

		SUICIDE {
			@Override
			public void initIdForMsg() {
				ChivEvtSuicide.initId(this);
			}
		},

		KICK_PLAYER {
			@Override
			public void initIdForMsg() {
				ChivCmdKickPlayer.initId(this);
			}
		},

		TEMP_BAN_PLAYER {
			@Override
			public void initIdForMsg() {
				ChivCmdTmpBanPlayer.initId(this);
			}
		},

		BAN_PLAYER {
			@Override
			public void initIdForMsg() {
				ChivCmdBanPlayer.initId(this);
			}
		},

		UNBAN_PLAYER {
			@Override
			public void initIdForMsg() {
				ChivCmdUnbanPlayer.initId(this);
			}
		},

		ROUND_END {
			@Override
			public void initIdForMsg() {
				ChivEvtRoundEnd.initId(this);
			}
		},

		PING {
			@Override
			public void initIdForMsg() {
				ChivEvtPing.initId(this);
			}
		},

		PING_EXTENDED {
			@Override
			public void initIdForMsg() {
				ChivEvtPingExtended.initId(this);
			}
		},

		CHANGE_SCORE {
			@Override
			public void initIdForMsg() {
				ChivCmdChangeScore.initId(this);
			}
		},

		KILL_PLAYER {
			@Override
			public void initIdForMsg() {
				ChivCmdKillPlayer.initId(this);
			}
		},

		INEBRIATE {
			@Override
			public void initIdForMsg() {
				ChivCmdInebriate.initId(this);
			}
		},

		CHANGE_GAME_PASSWORD {
			@Override
			public void initIdForMsg() {
				ChivCmdChangeGamePassword.initId(this);
			}
		},

		CONSOLE_COMMAND {
			@Override
			public void initIdForMsg() {
				ChivCmdConsoleCommand.initId(this);
			}
		};

		static {
			Arrays.asList(values()).forEach(e -> e.initIdForMsg());
		}

		/**
		 * Tests if a corresponding ChivMessageId exists for the specified id value.
		 *
		 * @param id
		 * @return true if a match is found
		 */
		public static boolean containsId(int id) {
			return id < values().length && id >= 0;
		}

	}

}
