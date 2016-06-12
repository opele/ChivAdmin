package resources;

/**
 * Contains text resources
 */
public class Txt {

	//// ----- errors messages ----- ////

	// -- parsing -- //
	public static String errParseNoData(String object, int expectedLength) {
		return String.format("Error: Can't retrieve '%s' of expected length %d from input reader. No data available or cut off.", object,
				expectedLength);
	}

	public static String errParseUnknownType(int type) {
		return String.format("Error: The message of type '%d' is unknown.", type);
	}

	public static String errParseMalformed(String typeName, int len) {
		return String.format("The message with typeName '%s' of length %d is malformed.", typeName, len);
	}

	public static String errParseString(String encoding, int len, String reason) {
		return String.format("Failed to parse a text message with %s encoding received from the server of length %d. Reason: ", encoding, len, reason);
	}

	// -- connector -- //
	public static String errConnBadIp(int bytes) {
		return String.format(
				"Error: The IP-Address you specified is of illegal length. It contains %d bytes. IPv4 address byte array must be 4 bytes long and IPv6 byte array must be 16 bytes long.",
				bytes);
	}

	public static String errConnBadHost(String reason) {
		return String.format("Error: The IP-Address you specified is invalid or could not be resolved. Reason: %s", reason);
	}

	public static String errConnFail(String msg) {
		return String.format("Unable to communicate with the endpoint. Is the server running and reachable? (%s)", msg);
	}

	public static String errConnLoginTimeout() {
		return "The server ip and port seem correct but the login could not be completed. Please verify the admin password and open the port.";
	}

	public static String errConnCloseFail(String msg) {
		return String.format("Error: The connection could not properly shut down for %s", msg);
	}

	// - receiving - //
	public static String errConnReceivedMultiLogin() {
		return "Server sent PASSWORD message type again, even though we are already logged in. Ignoring.";
	}

	public static String errConnThreadAbort(String msg) {
		return String.format("Listener thread caused exception while waiting for it to terminate:  %s", msg);
	}

	public static String errConnInterruptStream(String msg) {
		return String.format("Error: The listening stream was interrupted: %s", msg);
	}

	public static String errConnDroppedMsg(String msg) {
		return String.format("Warning: Dropping message received from server. Reason: %s", msg);
	}

	// - sending - //
	public static String errConnAlreadyLoggedIn() {
		return String.format("Warning: A connection is already establisted. Aborting sending PASSWORD message.");
	}

	public static String errConnNotLoggedIn() {
		return String.format("Error: Commands can not be sent before logging in. Requires sending PASSWORD message first.");
	}

	public static String errSendEncryptFail(String msg) {
		return String.format("Error: Failure to encrypt the password to establish a connection: %s", msg);
	}

	public static String errConnSendFail(String msg) {
		return String.format("Error: Unable to write the command to the output stream: %s", msg);
	}

	public static String errConnSetOutputFail(String msg) {
		return String.format("Error: Unable to open write output stream: %s", msg);
	}

	public static String errConnReconnect(String msg) {
		return String.format("Exception occured while waiting for the TCP connections to close before connecting again: %s", msg);
	}

	// -- steam -- //
	public static String errPlayerSumId(String steamId, String reason) {
		return String.format("Error: Can't retrieve PlayerSummary for SteamId %s. Reason: %s", steamId, reason);
	}

	public static String errOpenWebsite(String url, String reason) {
		return String.format("Error: Unable to open the link %s in the defualt browser. Reason: %s", url, reason);
	}

	// -- presentation -- //
	public static String errConnFailPresentation(String server) {
		return String.format("The connection could not be established to %s", server);
	}

	public static String errLoadServer(String ip) {
		return String.format("The server with ip '%s' could not be loaded from the saved configuration.", ip);
	}

	public static String errLoadForm(String formName, String reason) {
		return String.format("Could not load the %s form controller. Reason: %s", formName, reason);
	}

	public static String errParseConfig() {
		return "Unable to save and apply one or more values. The values you specified are not valid.";
	}


	//// ----- URLs ----- ////

	public static String urlSteamWebRequest(String methodName, String serviceVersion, String key, String steamids) {
		return String.format("http://api.steampowered.com/ISteamUser/%s/%s/?key=%s&steamids=%s&format=json", methodName, serviceVersion, key,
				steamids);
	}

	public static String urlSandcastleWorkshopPage() {
		return "http://steamcommunity.com/sharedfiles/filedetails/?id=289849044";
	}

	public static String urlPayPalDonation() {
		return "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=3WGSV4XCXYG3L";
	}

	////----- notifications ----- ////

	public static String connThreadTerminated(String msg) {
		return String.format("The listening thread was terminated: %s", msg);
	}

	public static String autoKickedPing(int maxPing) {
		return String.format("ChivAdmin: Your ping is too high (> %d)", maxPing);
	}

	public static String autoKickedTeamdamage(int maxTeamdamage) {
		return String.format("ChivAdmin: Your ping is too high (> %d)", maxTeamdamage);
	}

}
