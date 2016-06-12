package chivrcon.business;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;

import chivrcon.data.ChivMessage.ChivMessageId;
import chivrcon.data.commands.ChivCommand;
import chivrcon.data.events.ChivEvent;
import chivrcon.data.events.ChivEventFactory;
import presentation.ChivAdminApp;
import presentation.helper.Logger.LogType;
import resources.Txt;

/**
 * Manages the socket connection to the remote server for receiving and sending messages. Contains a Listener and a Sender for the corresponding tasks.
 */
public class Connector {

	private Socket socket;
	private boolean serverConnectSuccess = false;
	private Listener listener;
	private Sender sender;
	private ServerConnection sc;

	private static long LAST_DISCONNECT;
	private static int MIN_RECONNECT_DELAY = 500;
	private static int CONNECT_TIMEOUT = 5000;
	private static long LAST_CONNECT;
	private static int LOGIN_TIMEOUT = 10000;

	Connector(ServerConnection sc) {
		this.sc = sc;
		listener = new Listener();
		sender = new Sender();
	}

	/**
	 * Attempts to connect to the remote sever and starts listening for incoming data.
	 *
	 * @param receiver
	 *            callback method when a message arrives
	 */
	public void connect() {
		shutDown();
		keepMinReconnectDelay();

		if (sc.getLoginData() == null || !sc.getLoginData().isValid())
			throw new IllegalArgumentException(
					Txt.errConnBadHost("The validation check on the data did no succeed or the loginData was not available."));

		try {
			ChivAdminApp.getApp().log("Connecting to Server: " + sc.getLoginData(), LogType.INFO);
			socket = new Socket();
			socket.connect(new InetSocketAddress(sc.getLoginData().getAddress(), sc.getLoginData().getPort()), CONNECT_TIMEOUT);
			LAST_CONNECT = System.currentTimeMillis();
			listener.startListening();
		} catch (IOException e) {
			throw new RuntimeException(Txt.errConnFail(e.getMessage()), e);
		}
	}

	void sendCommand(ChivCommand cmd) {
		sender.sendCommand(cmd);
	}

	/**
	 * Sets the state to logged in if connected.
	 */
	public void notifyServerConnectSuccess() {
		sender.notifyServerConnectSuccess();
	}

	public boolean isConnected() {
		return socket != null && socket.isConnected() && !socket.isClosed();
	}

	public boolean isLoggedIn() {
		return isConnected() && serverConnectSuccess;
	}

	/**
	 * Caution: waits for the shutdown to finish: Listening thread terminating + 500 grace period for the TCP connection to close. Necessary to allow immediate reconnects.
	 */
	void shutDown() {
		listener.shutDownListener();
		serverConnectSuccess = false;

		try {
			if (socket != null) {
				socket.close();
				LAST_DISCONNECT = System.currentTimeMillis();
			}
		} catch (IOException e) {
			throw new RuntimeException(Txt.errConnCloseFail("the socket"), e);
		}
	}

	/**
	 * Remember last disconnect so that in case a new connect happens,
	 * the app needs to sleep before connecting depending on MIN_RECONNECT_DELAY.
	 * This is required for TCP connections to close and not just the socket.
	 */
	private void keepMinReconnectDelay() {
		Long timeSinceReconnect = System.currentTimeMillis() - LAST_DISCONNECT;
		if (timeSinceReconnect < MIN_RECONNECT_DELAY) {
			try {
				Thread.sleep(MIN_RECONNECT_DELAY - timeSinceReconnect);
			} catch (InterruptedException e) {
				e.printStackTrace();
				ChivAdminApp.getApp().log(Txt.errConnReconnect(e.getLocalizedMessage()), LogType.ERROR);
			}
		}
	}

	/**
	 * Responsible for receiving the messages sent by the server.
	 *
	 */
	class Listener {

		private static final int MIN_MESSAGE_SIZE = 6;

		private BufferedInputStream socketInput;
		private Thread listenerLoop;
		private boolean notifiedLoginTimeout;

		void startListening() {
			shutDownListener();
			initInputStream();
			notifiedLoginTimeout = false;
			listenerLoop = new Thread(this::listen);
			listenerLoop.setDaemon(true);
			listenerLoop.start();
		}

		private void listen() {
			while (isConnected()) {
				try {
					if (socketInput.available() < MIN_MESSAGE_SIZE) {
						if (!isLoggedIn() && !notifiedLoginTimeout && System.currentTimeMillis() - LAST_CONNECT > LOGIN_TIMEOUT) {
							sc.loginTimedOut();
							notifiedLoginTimeout = true;
						}
						Thread.sleep(100);
						continue;
					}
				} catch (IOException e) {
					throw new RuntimeException(Txt.errConnInterruptStream(e.getMessage()), e);
				} catch (InterruptedException e) {
					e.printStackTrace();
					ChivAdminApp.getApp().log(Txt.connThreadTerminated(e.getMessage()), LogType.WARN);
					sc.connectionLost();
					return;
				}

				ChivEvent evt = null;
				try {
					evt = ChivEventFactory.parseData(socketInput, sc.getLoginData());
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					ChivAdminApp.getApp().log(Txt.errConnDroppedMsg(e.getMessage()), LogType.WARN);
				}

				if (evt != null) {
					sc.getEventMngr().receive(evt);
				}
			}
		}

		private void initInputStream() {
			try {
				socketInput = socketInput == null ? new BufferedInputStream(socket.getInputStream()) : socketInput;
			} catch (IOException e) {
				shutDown();
				throw new RuntimeException(Txt.errConnSetOutputFail(e.getMessage()), e);
			}
		}

		void shutDownListener() {
			if (listenerLoop != null && listenerLoop.isAlive()) {
				listenerLoop.interrupt();
				try {
					listenerLoop.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
					ChivAdminApp.getApp().log(Txt.errConnThreadAbort("" + e), LogType.ERROR);
				}
			}
		}
	}

	/**
	 * Responsible for sending messages to the server.
	 *
	 */
	class Sender {

		private BufferedOutputStream socketOutput;
		private LinkedList<ChivCommand> pendingCmdQueue = new LinkedList<ChivCommand>();

		void notifyServerConnectSuccess() {
			if (isConnected()) {
				serverConnectSuccess = true;
				executePendingCommands();
			}
		}

		private void executePendingCommands() {
			while (pendingCmdQueue.size() > 0 && isLoggedIn()) {
				sendCommand(pendingCmdQueue.pop());
			}
		}

		void sendCommand(ChivCommand cmd) {
			if (!isConnected()) {
				pendingCmdQueue.add(cmd);
				connect();
			} else if (cmd.getId() != ChivMessageId.PASSWORD && !isLoggedIn()) {
				throw new IllegalArgumentException(Txt.errConnNotLoggedIn());
			} else {
				sendData(cmd.getEncodedData());
			}
		}

		private void sendData(byte[] data) {
			try {
				socketOutput = socketOutput == null ? new BufferedOutputStream(socket.getOutputStream()) : socketOutput;
				socketOutput.write(data);
				socketOutput.flush();
			} catch (IOException e) {
				shutDown();
				throw new RuntimeException(Txt.errConnSendFail(e.getMessage()), e);
			}
		}
	}

}
