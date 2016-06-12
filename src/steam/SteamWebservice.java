package steam;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

public abstract class SteamWebservice {

	public SteamWebservice(Object[] params) throws IOException {
		URL url = createUrl(params);
		try (InputStream is = url.openStream();
				JsonParser parser = Json.createParser(is)) {
			while (parser.hasNext()) {
				Event e = parser.next();
				if (e == Event.KEY_NAME) {
					fillData(parser);
				}
			}
		}
	}

	abstract URL createUrl(Object[] params) throws MalformedURLException;

	abstract void fillData(JsonParser parser);

}
