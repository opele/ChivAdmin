package utils;

import java.io.BufferedInputStream;
import java.io.UnsupportedEncodingException;

import presentation.ChivAdminApp;
import presentation.helper.Logger.LogType;
import resources.Txt;
import steam.SteamId;

/**
 * Utility methods for parsing various data types from a received message.
 */
public class ParseUtils {

	private final static int SHORT_BYTES = 2;
	private final static int INT_BYTES = 4;
	private final static int LONG_BYTES = 8;
	private final static int STEAMID_BYTES = 8;

	public static short parseShort(BufferedInputStream data) {
		return parseShort(ArrayUtils.readToIntArray(SHORT_BYTES, data));
	}

	public static short parseShort(int[] data) {
		short result = 0;

		for (int i = 0; i < SHORT_BYTES; i++) {
			result += data[i] << (SHORT_BYTES - 1 - i) * 8;
		}

		return result;
	}

	public static int parseInt(BufferedInputStream data) {
		return parseInt(ArrayUtils.readToIntArray(INT_BYTES, data));
	}

	public static int parseInt(int[] data) {
		int result = 0;

		for (int i = 0; i < INT_BYTES; i++) {
			result += data[i] << (INT_BYTES - 1 - i) * 8;
		}

		return result;
	}

	public static long parseLong(BufferedInputStream data) {
		return parseLong(ArrayUtils.readToIntArray(LONG_BYTES, data));
	}

	public static long parseLong(int[] data) {
		long result = 0l;

		for (int i = 0; i < LONG_BYTES; i++) {
			result += data[i] << (LONG_BYTES - 1 - i) * 8;
		}

		return result;
	}

	public static SteamId parseSteamId(BufferedInputStream data) {
		return parseSteamId(ArrayUtils.readToIntArray(STEAMID_BYTES, data));
	}

	public static SteamId parseSteamId(int[] data) {
		SteamId id = new SteamId();
		id.setSteamIdB(ParseUtils.parseInt(data));
		id.setSteamIdA(ParseUtils.parseInt(new int[] { data[4], data[5], data[6], data[7] }));

		return id;
	}

	public static String parseString(BufferedInputStream data) {
		int length = parseInt(data);
		String result = null;

		try {
			result = length > 0 ? new String(ArrayUtils.readToByteArray(length, data), "UTF-8").trim() : null;
		} catch (UnsupportedEncodingException e) {
			ChivAdminApp.getApp().log(Txt.errParseString("UTF-8", length, e.getLocalizedMessage()), LogType.ERROR);
		}

		return result;
	}

	public static String parseString(int[] data) {
		int length = parseInt(data);
		if (length <= 0)
			return null;

		byte[] stringData = new byte[length];
		for (int i = INT_BYTES; i < length + INT_BYTES; i++) {
			stringData[i - INT_BYTES] = (byte) data[i];
		}

		String result = null;
		try {
			result = new String(stringData, "UTF-8").trim();
		} catch (UnsupportedEncodingException e) {
			ChivAdminApp.getApp().log(Txt.errParseString("UTF-8", length, e.getLocalizedMessage()), LogType.ERROR);
		}

		return result;
	}

}
