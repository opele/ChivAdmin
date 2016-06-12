package presentation.views;

import com.sun.javafx.scene.control.behavior.PasswordFieldBehavior;
import com.sun.javafx.scene.control.skin.TextFieldSkin;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

@SuppressWarnings("restriction")
public class PasswordFieldSkin extends TextFieldSkin {
	public static final char BULLET = '\u2022';

	public PasswordFieldSkin(PasswordField passwordField) {
		super(passwordField, new PasswordFieldBehavior(passwordField));
	}

	@Override
	protected String maskText(String txt) {
		TextField textField = getSkinnable();

		int n = textField.getLength();
		StringBuilder passwordBuilder = new StringBuilder(n);
		for (int i = 0; i < n; i++) {
			passwordBuilder.append(BULLET);
		}

		return passwordBuilder.toString();
	}
}
