package steam;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.json.stream.JsonParser;

import resources.Txt;

/**
 * Contains only a few properties from the GetPlayerSummaries request.
 *
 * Example: http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=XXXXXXXXXXXXXXXXXXXXXXX&steamids=76561197960435530
 *
 */
public class PlayerSummary extends SteamWebservice {

	private SteamId steamId;
	private String personaname;
	private String profileUrl;
	private String avatarMedium;
	private String countryCode;

	public PlayerSummary(SteamId steamId) throws IOException {
		super(new Object[] { steamId });
		this.steamId = steamId;
	}

	@Override
	URL createUrl(Object[] params) throws MalformedURLException {
	}

	void fillData(JsonParser parser) {
		switch (parser.getString()) {
		case "personaname":
			parser.next();
			personaname = parser.getString();
			break;
		case "profileurl":
			parser.next();
			profileUrl = parser.getString();
			break;
		case "avatarmedium":
			parser.next();
			avatarMedium = parser.getString();
			break;
		case "loccountrycode":
			parser.next();
			countryCode = parser.getString();
			break;
		}
	}

	public SteamId getSteamId() {
		return steamId;
	}

	public String getPersonaname() {
		return personaname;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public String getAvatarMedium() {
		return avatarMedium;
	}

	public String getCountryCode() {
		return countryCode;
	}

}
