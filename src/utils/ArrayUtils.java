package utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import resources.Txt;

public class ArrayUtils {

	/**
	 * Converts a byte array to a Stream.
	 *
	 * @param arr
	 * @return
	 */
	public static Stream<Integer> intsToStream(int[] arr) {
		return IntStream.range(0, arr.length).mapToObj(i -> arr[i]);
	}

	/**
	 * Concatenates arrays in the order the parameters are provided.
	 *
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static int[] appendIntArrays(int[] c1, int[] c2) {
		if(c1 == null) return c2;
		if(c2 == null) return c1;

		int [] c3 = new int[c1.length + c2.length];
		System.arraycopy(c1, 0, c3, 0, c1.length);
		System.arraycopy(c2, 0, c3, c1.length, c2.length);

		return c3;
	}

	/**
	 * Concatenates arrays in the order the parameters are provided.
	 *
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static byte[] appendByteArrays(byte[] c1, byte[] c2) {
		if(c1 == null) return c2;
		if(c2 == null) return c1;

		byte [] c3 = new byte[c1.length + c2.length];
		System.arraycopy(c1, 0, c3, 0, c1.length);
		System.arraycopy(c2, 0, c3, c1.length, c2.length);

		return c3;
	}

	/**
	 * Converts stream data to int array for representing the bytes.
	 *
	 * Reads bytes but they are not stored in a byte array because casting to
	 * int may return negative values because bytes are signed in Java.
	 *
	 * @param arrayLength
	 *            how much data to read
	 * @param data
	 *            the input stream
	 * @return
	 */
	public static int[] readToIntArray(int arrayLength, BufferedInputStream data) {
		int[] ints = new int[arrayLength];
		try {
			for (int i = 0; i < arrayLength && data.available() > 0; i++) {
				ints[i] = data.read();
				if (ints[i] == -1)
					throw new IllegalArgumentException(Txt.errParseNoData("byte data", arrayLength));
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(Txt.errParseNoData("byte data", arrayLength) + " " + e.getMessage(), e);
		}

		return ints;
	}

	/**
	 * Useful for passing the resulting byte array to a String constructor.
	 *
	 * @param arrayLength
	 * @param data
	 * @return
	 */
	public static byte[] readToByteArray(int arrayLength, BufferedInputStream data) {
		byte[] bytes = new byte[arrayLength];
		try {
			if (data.available() >= arrayLength)
				data.read(bytes);
			else
				throw new IllegalArgumentException(Txt.errParseNoData("string data", arrayLength));
		} catch (IOException e) {
			throw new IllegalArgumentException(Txt.errParseNoData("string data", arrayLength) + " " + e.getMessage(), e);
		}

		return bytes;
	}

	public static String intToHex(int[] arr) {
		return IntStream.range(0, arr.length)
				.mapToObj(i -> String.format("%02x", arr[i]))
				.reduce((s1,s2) -> s1 + s2).get();
	}

	public static String byteToHex(byte[] arr) {
		return IntStream.range(0, arr.length)
				.mapToObj(i -> String.format("%02x", arr[i]))
				.reduce((s1,s2) -> s1 + s2).get();
	}

	public static String charToHex(char[] arr) {
		return IntStream.range(0, arr.length)
				.mapToObj(i -> String.format("%02x", arr[i]))
				.reduce((s1,s2) -> s1 + s2).get();
	}
}
