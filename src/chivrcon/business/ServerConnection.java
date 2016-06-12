package chivrcon.business;

import java.util.function.Consumer;

import chivrcon.data.LoginData;
import javafx.application.Platform;
import presentation.ChivAdminApp;

/**
 * Represents a server connection and provides access to business logic to perform the tasks triggered by the GUI.
 * These tasks consist of remote controlling the Chivalry server.
 * Notice that for each server is managed by one ServerConnection instance.
 */
public class ServerConnection {

	private final Connector connector = new Connector(this);
	private final CommandMngr commandMngr = new CommandMngr(this);
	private final EventMngr eventMngr = new EventMngr(this);
	private final LoginData loginData;
	private Consumer<LoginData> loginTimeout;

	public ServerConnection(LoginData loginData) {
		this.loginData = loginData;
	}

	/**
	 * Attempts to establish a socket connection and login to the server.
	 * There is a distinct timeout for each.
	 *
	 * @param loginTimeout is called after the timeout is reached to receive the successful connect event
	 */
	public void connect(Consumer<LoginData> loginTimeout) {
		this.loginTimeout = loginTimeout;
		connector.connect();
	}

	public void disconnect() {
		connector.shutDown();
	}

	void connectionLost() {
		disconnect();
		ChivAdminApp.getApp().notifyConnectionLost(loginData);
	}

	public LoginData getLoginData() {
		return loginData;
	}

	public CommandMngr getCommandMngr() {
		return commandMngr;
	}

	void loginTimedOut() {
		if (loginTimeout == null) return;

		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> loginTimeout.accept(loginData));
		} else
			loginTimeout.accept(loginData);
	}

	Connector getConnector() {
		return connector;
	}

	EventMngr getEventMngr() {
		return eventMngr;
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
	        return false;
	    }

	    if (!ServerConnection.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }

	    final ServerConnection other = (ServerConnection) obj;

	    return loginData.equals(other.getLoginData());
	}

	@Override
	public int hashCode() {
	    return 53 * 7 + this.loginData.hashCode();
	}

}
