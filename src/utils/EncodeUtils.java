package utils;

import java.nio.charset.Charset;

import steam.SteamId;


/**
 * Utility methods for converting various data types to a message for sending.
 *
 * Here we work with byte arrays instead of int array because the data is easier
 * to write and does not need to be converted to a high level representation.
 */
public class EncodeUtils {

	private final static int SHORT_BYTES = 2;
	private final static int INT_BYTES = 4;
	private final static int LONG_BYTES = 8;

	public static byte[] encodeShort(short num) {
		byte[] result = new byte[SHORT_BYTES];
		for (int i = 0; i < SHORT_BYTES; i++) {
			result[i] = (byte) ((num >> (SHORT_BYTES - i - 1) * 8) & 0xFF);
		}

		return result;
	}

	public static byte[] encodeInt(int num) {
		byte[] result = new byte[INT_BYTES];
		for (int i = 0; i < INT_BYTES; i++) {
			result[i] = (byte) ((num >> (INT_BYTES - i - 1) * 8) & 0xFF);
		}

		return result;
	}

	public static byte[] encodeLong(long num) {
		byte[] result = new byte[LONG_BYTES];
		for (int i = 0; i < LONG_BYTES; i++) {
			result[i] = (byte) ((num >> (LONG_BYTES - i - 1) * 8) & 0xFF);
		}

		return result;
	}

	public static byte[] encodeSteamId(SteamId id) {
		byte[] idB = EncodeUtils.encodeInt(id.getSteamIdB());
		byte[] idA = EncodeUtils.encodeInt(id.getSteamIdA());

		return ArrayUtils.appendByteArrays(idB, idA);
	}

	public static byte[] encodeString(String s) {
		byte[] data = s.getBytes(Charset.forName("UTF-8"));
		return ArrayUtils.appendByteArrays(encodeInt(data.length), data);
	}
}
