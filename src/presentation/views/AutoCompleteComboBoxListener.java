package presentation.views;

import java.util.List;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

//TODO: refactoring
@SuppressWarnings("restriction")
public class AutoCompleteComboBoxListener<T> {

	protected ComboBox<T> comboBox;

	public AutoCompleteComboBoxListener(ComboBox<T> comboBox, List<T> defaultItems) {
		this.comboBox = comboBox;
		fillWithItems(defaultItems);

		comboBox.setEditable(true);
		comboBox.setOnKeyReleased(event -> {
			if (processedSpecialKeys(event))
				return;

			StringBuilder sb = new StringBuilder();
			sb.append(comboBox.getEditor().getText());
			/* remove selected string index until end so only unselected text will be recorded
			IndexRange ir = comboBox.getEditor().getSelection();
			try {
				sb.delete(ir.getStart(), sb.length());
			} catch (Exception ignored) {
			}
			*/

			ObservableList<T> items = comboBox.getItems();
			for (int i = 0; i < items.size(); i++) {
				if (items.get(i) != null && comboBox.getEditor().getText() != null
						&& items.get(i).toString().toLowerCase().startsWith(comboBox.getEditor().getText().toLowerCase())) {
					try {
						comboBox.getEditor().setText(sb.toString() + items.get(i).toString().substring(sb.toString().length()));
						comboBox.setValue(items.get(i));
						comboBox.getSelectionModel().select(i);
					} catch (Exception e) {
						comboBox.getEditor().setText(sb.toString());
					}
					comboBox.getEditor().positionCaret(sb.toString().length());
					comboBox.getEditor().selectEnd();
					break;
				}
			}
		});

		comboBox.setOnMouseClicked(event -> selectClosestResultBasedOnTextFieldValue(true, true));
	}

	private boolean processedSpecialKeys(KeyEvent event) {
		if (isControlKey(event))
			return true;

		if (event.getCode().equals(KeyCode.DOWN)) {
			comboBox.show();
			return true;
		}

		return false;
	}

	private static boolean isControlKey(KeyEvent event) {
		return event.isControlDown() || event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
				|| event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.HOME || event.getCode() == KeyCode.END
				|| event.getCode() == KeyCode.TAB;
	}

	/**
	 * Selects the item and scrolls to it when the popup is shown.
	 *
	 * @param affect
	 *            true if combobox is clicked to show popup so text and caret position will be readjusted.
	 *
	 * @param inFocus
	 *            true if combobox has focus. If not, programmatically press enter key to add new entry to list.
	 */
	private void selectClosestResultBasedOnTextFieldValue(boolean affect, boolean inFocus) {
		ObservableList<T> items = comboBox.getItems();
		boolean found = false;
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i) != null && comboBox.getEditor().getText() != null
					&& comboBox.getEditor().getText().toLowerCase().equals(items.get(i).toString().toLowerCase())) {
				try {
					ListView<?> lv = ((ComboBoxListViewSkin<?>) comboBox.getSkin()).getListView();
					lv.getSelectionModel().clearAndSelect(i);
					lv.scrollTo(lv.getSelectionModel().getSelectedIndex());
					found = true;
					break;
				} catch (Exception ignored) {
				}
			}
		}

		String s = comboBox.getEditor().getText();
		if (!found && affect) {
			comboBox.getSelectionModel().clearSelection();
			comboBox.getEditor().setText(s);
			comboBox.getEditor().end();
		}

		if (!found) {
			comboBox.getEditor().setText(null);
			comboBox.getSelectionModel().select(null);
			comboBox.setValue(null);
		}
	}

	private void fillWithItems(List<T> defaultItems) {
		ObservableList<T> items = comboBox.getItems();
		items.addAll(defaultItems);
		// prevents a jsf error "addTrailingCells INFO: index exceeds maxCellCount."
		if (items.size() > 2)
			comboBox.setVisibleRowCount(items.size() - 1);
	}

}
