package presentation.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import chivrcon.data.LoginData;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import presentation.ChivAdminApp;
import presentation.data.ServerView;

public class Logger {

	// TODO: clear messages which are older than 2h
	private final Map<LoginData, Map<LogType, List<LogMessage>>> allLogs = new HashMap<>();
	private final TextFlow sysMsgControl;
	private final TextFlow chatMsgControl;
	private final ConcurrentLinkedQueue<LogMessage> pendingLogs = new ConcurrentLinkedQueue<Logger.LogMessage>();
	private final int DEFAULT_MAX_DISPLAYED_LOGS = 250;

	private LogType currentTypeFilter;
	private String currentTextFilter;
	private int maxDisplayedLogs = DEFAULT_MAX_DISPLAYED_LOGS;
	private boolean isScrollLocked;

	public enum LogType {
		DEBUG {
			Color getColor() {
				return Color.DARKTURQUOISE;
			}
		},
		EVENT {
			Color getColor() {
				return Color.SEAGREEN;
			}
		},
		COMMAND {
			Color getColor() {
				return Color.BROWN;
			}
		},
		INFO {
			Color getColor() {
				return Color.BLUE;
			}
		},
		WARN {
			Color getColor() {
				return Color.ORANGE;
			}
		},
		ERROR {
			Color getColor() {
				return Color.RED;
			}
		},
		CHAT {
			Color getColor() {
				return Color.BLACK;
			}
		};

		abstract Color getColor();
	}

	public Logger(TextFlow sysMsgControl, TextFlow chatMsgControl) {
		this.sysMsgControl = sysMsgControl;
		this.chatMsgControl = chatMsgControl;
	}

	public void log(String msg, LogType type, LoginData server, Color color) {
		LogMessage logMsg = new LogMessage(msg, type, server);
		logMsg.setColor(color);
		if (!Platform.isFxApplicationThread()) {
			pendingLogs.add(logMsg);
			Platform.runLater(() -> {
				LogMessage l;
				while ((l = pendingLogs.poll()) != null)
					actualLog(l);
			});
		} else
			actualLog(logMsg);
	}

	private void actualLog(LogMessage log) {
		addLog(log);

		if (shouldLogBeDisplayed(log)) {
			if (LogType.CHAT.equals(log.type))
				displayMsg(log.toText(), chatMsgControl, DEFAULT_MAX_DISPLAYED_LOGS);
			else
				displayMsg(log.toText(), sysMsgControl, maxDisplayedLogs + 1);
		}
	}

	private void addLog(LogMessage log) {
		if (log.server == null)
			return;

		if (!allLogs.containsKey(log.server)) {
			allLogs.put(log.server, new HashMap<LogType, List<LogMessage>>());
		}

		if (!allLogs.get(log.server).containsKey(log.type)) {
			allLogs.get(log.server).put(log.type, new ArrayList<LogMessage>());
		}

		allLogs.get(log.server).get(log.type).add(log);
	}

	private void displayMsg(Text txt, TextFlow container, int maxDisplayedLogs) {
		List<Node> logs = container.getChildren();
		while (logs.size() > maxDisplayedLogs) {
			logs.remove(0);
		}

		logs.add(txt);

		if (!isScrollLocked && container.getParent() != null && container.getParent().getParent() != null
				&& container.getParent().getParent().getParent() != null) {
			ScrollPane scroll = (ScrollPane) container.getParent().getParent().getParent();
			scroll.setVvalue(scroll.getVmax());
		}
	}

	/**
	 * For performance reasons this is only used to test if single new logs messages should be displayed. This is not used for rebuilding the whole system message output when a filter changes.
	 *
	 * @param log
	 * @return
	 */
	private boolean shouldLogBeDisplayed(LogMessage log) {
		ServerView currentServer = ChivAdminApp.getApp().getMainView().getCurrentDisplayedServer();
		LoginData serverData = currentServer != null ? currentServer.getLoginData() : null;

		if (serverData == null || log.server == null || serverData.equals(log.server)) {
			if (LogType.CHAT.equals(log.type)) {
				return true;
			} else {
				return (currentTextFilter == null || log.message.contains(currentTextFilter))
						&& (currentTypeFilter == null || currentTypeFilter.equals(log.type));
			}
		}

		return false;
	}

	/**
	 * Only messages containing the filter word are displayed.
	 *
	 * @param filter
	 */
	public void filterSysMsgByString(String filter) {
		currentTextFilter = filter;
		reapplySysMsgFilter();
	}

	/**
	 * Only messages of the specific type are displayed.
	 *
	 * @param filter
	 */
	public void filterSysMsgByType(LogType filter) {
		currentTypeFilter = filter;
		reapplySysMsgFilter();
	}

	/**
	 * Notify the logger that the log output needs to be redrawn, e.g. when the selected server changed.
	 */
	public void redrawLoggingDisplay() {
		reapplySysMsgFilter();
		reapplyChatFilter();
	}

	/**
	 * Redraws the displayed chat messages.
	 */
	private void reapplyChatFilter() {
		List<LogMessage> logs = getChatLogsToDisplay();
		chatMsgControl.getChildren().clear();

		for (LogMessage log : logs) {
			chatMsgControl.getChildren().add(log.toText());
		}
	}

	private List<LogMessage> getChatLogsToDisplay() {
		List<LogMessage> logs = null;

		logs = getChatLogsFilteredByType(getLogsFilteredByServer());
		logs = getLogsFilteredByDisplayAmount(logs, DEFAULT_MAX_DISPLAYED_LOGS);

		return logs;
	}

	private List<LogMessage> getChatLogsFilteredByType(Map<LogType, List<LogMessage>> logs) {
		List<LogMessage> filtered = logs.get(LogType.CHAT);

		return filtered == null ? Collections.emptyList() : filtered;
	}

	/**
	 * Redraws the displayed system messages based on 3 filters:
	 * 1. amount of log messages to display
	 * 2. String filter
	 * 3. LogMessage type filter
	 */
	private void reapplySysMsgFilter() {
		List<LogMessage> logs = getSysLogsToDisplay();
		sysMsgControl.getChildren().clear();

		for (LogMessage log : logs) {
			sysMsgControl.getChildren().add(log.toText());
		}
	}

	private List<LogMessage> getSysLogsToDisplay() {
		List<LogMessage> logs = null;

		logs = getSysLogsFilteredByType(getLogsFilteredByServer());
		logs = getSysLogsFilteredByString(logs);
		logs = getSysLogsFilteredBySysMsgOnly(logs);
		logs = getLogsFilteredByDisplayAmount(logs, maxDisplayedLogs);

		return logs;
	}

	private Map<LogType, List<LogMessage>> getLogsFilteredByServer() {
		ServerView currentServer = ChivAdminApp.getApp().getMainView().getCurrentDisplayedServer();
		LoginData serverData = currentServer != null ? currentServer.getLoginData() : null;
		Map<LogType, List<LogMessage>> filtered = allLogs.get(serverData);
		return filtered == null ? Collections.emptyMap() : filtered;
	}

	private List<LogMessage> getSysLogsFilteredByType(Map<LogType, List<LogMessage>> logs) {
		List<LogMessage> filtered = null;
		if (currentTypeFilter != null) {
			filtered = logs.get(currentTypeFilter);
		} else {
			filtered = logs.values().stream()
					.flatMap(l -> l.stream())
					.collect(Collectors.toList());
		}

		return filtered == null ? Collections.emptyList() : filtered;
	}

	private List<LogMessage> getSysLogsFilteredByString(List<LogMessage> logs) {
		List<LogMessage> filtered = null;

		if (logs.size() == 0 || currentTextFilter == null)
			return logs;

		filtered = logs.stream()
				.filter(l -> l.message.contains(currentTextFilter))
				.collect(Collectors.toList());

		return filtered == null ? Collections.emptyList() : filtered;
	}

	private List<LogMessage> getSysLogsFilteredBySysMsgOnly(List<LogMessage> logs) {
		List<LogMessage> filtered = null;

		if (logs.size() == 0 || currentTypeFilter != null)
			return logs;

		filtered = logs.stream()
				.filter(l -> !l.type.equals(LogType.CHAT))
				.collect(Collectors.toList());

		return filtered == null ? Collections.emptyList() : filtered;
	}

	private List<LogMessage> getLogsFilteredByDisplayAmount(List<LogMessage> logs, int maxLogs) {
		return maxLogs > logs.size() ? logs : logs.subList(logs.size() - maxLogs, logs.size());
	}

	public boolean isScrollLocked() {
		return isScrollLocked;
	}

	public void setScrollLocked(boolean isScrollLocked) {
		this.isScrollLocked = isScrollLocked;
	}

	public int getMaxDisplayedLogs() {
		return maxDisplayedLogs;
	}

	public void setMaxDisplayedLogs(int maxDisplayedLogs) {
		this.maxDisplayedLogs = maxDisplayedLogs >= 0 ? maxDisplayedLogs : DEFAULT_MAX_DISPLAYED_LOGS;
		reapplySysMsgFilter();
	}

	public LogType getCurrentTypeFilter() {
		return currentTypeFilter;
	}

	public void setCurrentTypeFilter(LogType currentTypeFilter) {
		this.currentTypeFilter = currentTypeFilter;
		reapplySysMsgFilter();
	}

	public String getCurrentTextFilter() {
		return currentTextFilter;
	}

	public void setCurrentTextFilter(String currentTextFilter) {
		this.currentTextFilter = currentTextFilter;
		reapplySysMsgFilter();
	}

	class LogMessage {

		private final String message;
		private final LocalDateTime time;
		private final LogType type;
		private final LoginData server;
		private Color overrideColor;

		LogMessage(String message, LogType type, LoginData server) {
			this.message = message;
			this.type = type;
			this.time = LocalDateTime.now();
			this.server = server;
		}

		public Text toText() {
			Text txt = new Text(toString());
			txt.setFill(overrideColor == null ? type.getColor() : overrideColor);
			txt.setWrappingWidth(0);

			return txt;
		}

		/**
		 * Allows to override the default color defined by the message type.
		 *
		 * @param c
		 */
		public void setColor(Color c) {
			overrideColor = c;
		}

		@Override
		public String toString() {
			String t = "[" + time.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] ";

			return t + message + "\n";
		}
	}

}
