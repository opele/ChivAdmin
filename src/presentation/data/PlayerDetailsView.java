package presentation.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * Contains the data for the player details view displayed when a player is selected.
 * The data fields are part of PlayerView. The details view only manages how they are represented in the list.
 * Is stored and managed as a member of PlayerView.
 */
class PlayerDetailsView {

	/**
	 * Enum of player info fields to be displayed by the player details view.
	 */
	public enum PlayerDetails {
		NICK_NAME {
			@Override
			public String toString() {
				return "Nickname: ";
			}
		},
		PING {
			@Override
			public String toString() {
				return "Ping: ";
			}
		},
		TEAM {
			@Override
			public String toString() {
				return "Team: ";
			}
		},
		SCORE {
			@Override
			public String toString() {
				return "Score: ";
			}
		},
		TEAMDAMAGE {
			@Override
			public String toString() {
				return "Teamdamage dealt: ";
			}
		},
		IDLE_TIME {
			@Override
			public String toString() {
				return "Idle time in seconds: ";
			}
		},
		KILLS {
			@Override
			public String toString() {
				return "Kills: ";
			}
		},
		RANK {
			@Override
			public String toString() {
				return "Rank: ";
			}
		},
		STEAM_ID {
			@Override
			public String toString() {
				return "Steam Id: ";
			}
		},
		COMMUNITY_ID {
			@Override
			public String toString() {
				return "Community Id: ";
			}
		},
		COUNTRY_CODE {
			@Override
			public String toString() {
				return "Country Code: ";
			}
		},
		PROFILE_URL {
			@Override
			public String toString() {
				return "Steam profile page: ";
			}
		};
	}

	private ListProperty<String> detailsObservable = new SimpleListProperty<>();

	PlayerDetailsView(PlayerView player) {
		List<String> details = new ArrayList<>();
		details.addAll(Arrays.asList(new String[PlayerDetails.values().length]));
		details = FXCollections.observableArrayList(details);
		detailsObservable.set((ObservableList<String>) details);

		updateDetails(player.getName(), PlayerDetails.NICK_NAME);
		updateDetails("" + player.getPing(), PlayerDetails.PING);
		updateDetails("" + player.getTeam(), PlayerDetails.TEAM);
		updateDetails("" + player.getKills(), PlayerDetails.KILLS);
		updateDetails("" + player.getIdleTime(), PlayerDetails.IDLE_TIME);
		updateDetails("" + player.getRank(), PlayerDetails.RANK);
		updateDetails("" + player.getTeamDamage(), PlayerDetails.TEAMDAMAGE);
		updateDetails("" + player.getScore(), PlayerDetails.SCORE);
		updateDetails(player.getSteamId().toSteamId3(), PlayerDetails.STEAM_ID);
		updateDetails("" + player.getSteamId().toCommunityId(), PlayerDetails.COMMUNITY_ID);
		updateDetails(player.getSteamInfo().getCountryCode(), PlayerDetails.COUNTRY_CODE);
		updateDetails(player.getSteamInfo().getProfileUrl(), PlayerDetails.PROFILE_URL);
	}

	/**
	 * Clears and fills the details view with player information.
	 *
	 * @param detailsList
	 *            the list of properties to display in the player details view
	 */
	void fillPlayerDetails(ListView<String> detailsList) {
		detailsList.itemsProperty().bind(detailsObservable);
	}

	void updateDetails(String newValue, PlayerDetails detailType) {
		detailsObservable.set(detailType.ordinal(), detailType + (isValid(newValue) ? newValue : ""));
	}

	private boolean isValid(String data) {
		return data != null && data.trim().length() > 0 && !data.trim().equalsIgnoreCase("null");
	}

}
