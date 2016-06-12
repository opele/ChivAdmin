package utils;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import presentation.ChivAdminApp;
import presentation.helper.Logger.LogType;
import resources.Txt;

public class WebUtils {

	/**
	 * Opens the URI in the default browser.
	 *
	 * @param url
	 * @return true if the website could be opened
	 */
	public static boolean openWebsite(String url) {
		boolean succeeded = false;
		try {
			openWebsite(new URI(url));
			succeeded = true;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			ChivAdminApp.getApp().log(Txt.errOpenWebsite("" + url, e.toString()), LogType.ERROR);
		}

		return succeeded;
	}

	/**
	 * Opens the URI in the default browser.
	 *
	 * @param uri
	 * @return true if the website could be opened
	 */
	public static boolean openWebsite(URI uri) {
		boolean succeeded = false;
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
				succeeded = true;
			} catch (Exception e) {
				e.printStackTrace();
				ChivAdminApp.getApp().log(Txt.errOpenWebsite("" + uri, e.toString()), LogType.ERROR);
			}
		}

		return succeeded;
	}

	/**
	 * Opens the URI in the default browser.
	 *
	 * @param url
	 * @return true if the website could be opened
	 */
	public static boolean openWebsite(URL url) {
		boolean succeeded = false;
		try {
			openWebsite(url.toURI());
			succeeded = true;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			ChivAdminApp.getApp().log(Txt.errOpenWebsite("" + url, e.toString()), LogType.ERROR);
		}

		return succeeded;
	}
}
