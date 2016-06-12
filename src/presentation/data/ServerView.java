package presentation.data;

import chivrcon.data.LoginData;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ServerView {

	private final SimpleBooleanProperty isLoggedIn;
	private final SimpleStringProperty address;
	private final SimpleIntegerProperty players;
	private final SimpleStringProperty map;
	private final LoginData loginData;

	public ServerView(LoginData loginData, String map) {
		this.loginData = loginData;
		this.address = new SimpleStringProperty("" + loginData);
		this.players = new SimpleIntegerProperty(0);
		this.map = new SimpleStringProperty(map);
		this.isLoggedIn = new SimpleBooleanProperty(false);
	}

	public String getAddress() {
		return address.get();
	}

	public void setAddress(String address) {
		this.address.set(address);
	}

	public int getPlayers() {
		return players.get();
	}

	public void setPlayers(int players) {
		this.players.set(players);
	}

	public SimpleIntegerProperty playersProperty() {
		return players;
	}

	public String getMap() {
		return map.get();
	}

	public void setMap(String map) {
		this.map.set(map);
	}

	public SimpleStringProperty mapProperty() {
		return map;
	}

	public boolean isLoggedIn() {
		return isLoggedIn.get();
	}

	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn.set(isLoggedIn);
	}

	public SimpleBooleanProperty isLoggedInProperty() {
		return isLoggedIn;
	}

	public LoginData getLoginData() {
		return loginData;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
	        return false;
	    }

	    if (!ServerView.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }

	    final ServerView other = (ServerView) obj;

		return loginData.equals(other.getLoginData());
	}

	@Override
	public int hashCode() {
	    return 17 * 7 + loginData.hashCode();
	}
}
