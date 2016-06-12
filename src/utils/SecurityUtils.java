package utils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import presentation.ChivAdminApp;
import presentation.helper.Logger.LogType;
import resources.Txt;

public class SecurityUtils {

	private static String DESEDE_KEY = "ksTp7A3B4y";
	private static String BYTE_SEPERATOR = "_";

	/**
	 * SHA1 is a one way encryption, i.e. decryption is not intended.
	 *
	 * @param data
	 * @return
	 */
	public static String encryptSHA1(String data) {
		String encrypted = "";
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(data.getBytes("UTF-8"));
			encrypted = ArrayUtils.byteToHex(crypt.digest());
		} catch (Exception e) {
			throw new RuntimeException(Txt.errSendEncryptFail(e.getMessage()), e);
		}

		return encrypted;
	}

	public static String encryptDESede(String data) {
		if (data == null || data.length() == 0)
			return "";

		byte[] keyBytes = null;
		String result = "";
		try {
			final MessageDigest md = MessageDigest.getInstance("md5");
			final byte[] digestOfPassword = md.digest(DESEDE_KEY.getBytes("utf-8"));
			keyBytes = Arrays.copyOf(digestOfPassword, 24);
			for (int j = 0, k = 16; j < 8;) {
				keyBytes[k++] = keyBytes[j++];
			}

			final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
			final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
			final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);

			final byte[] plainTextBytes = data.getBytes("utf-8");
			final byte[] cipherText = cipher.doFinal(plainTextBytes);
			if (cipherText != null && cipherText.length > 0) {
				for (byte b : cipherText)
					result += b + BYTE_SEPERATOR;
				result = result.substring(0, result.length() - 1);
			}
		} catch (Exception e) {
			ChivAdminApp.getApp().log("Failed to encrypt text: " + e, LogType.ERROR);
			return "";
		}

		return result;
	}

	public static String decryptDESede(String data) {
		if (data == null || data.length() == 0)
			return "";

		List<Byte> bytes = new ArrayList<>();
		String result = "";
		try {
			if (data != null && data.length() > 0 && data.contains("_")) {
				for (String b : data.split("_")) {
					try {
						bytes.add(Byte.parseByte(b));
					} catch (Exception e) {
						ChivAdminApp.getApp().log("Failed to decrypt text: " + e, LogType.ERROR);
						return "";
					}
				}
			}
			byte[] input = new byte[bytes.size()];
			for (int i = 0; i < input.length; i++)
				input[i] = bytes.get(i);

			final MessageDigest md = MessageDigest.getInstance("md5");
			final byte[] digestOfPassword = md.digest(DESEDE_KEY.getBytes("utf-8"));
			final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
			for (int j = 0, k = 16; j < 8;) {
				keyBytes[k++] = keyBytes[j++];
			}

			final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
			final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
			final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
			decipher.init(Cipher.DECRYPT_MODE, key, iv);

			result = new String(decipher.doFinal(input), "UTF-8");
		} catch (Exception e) {
			ChivAdminApp.getApp().log("Failed to encrypt text: " + e, LogType.ERROR);
		}

		return result;
	}

}
