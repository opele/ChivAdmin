package presentation.views;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

import chivrcon.business.CommandMngr;
import chivrcon.business.ServerConnection;
import chivrcon.data.commands.ChivCmdConsoleCommand.ConsoleCommandScope;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import presentation.ChivAdminApp;
import presentation.helper.Logger.LogType;
import resources.Txt;
import steam.SteamId;

/**
 * Controller for the server login view.
 */
public class CommandController extends AnchorPane implements Initializable {

	private final static String FORM_PATH = "/presentation/views/CommandForm.fxml";

	private static Stage form;

	public static void createForm() {
		form = new Stage();
		try {
			form.initModality(Modality.NONE);
			form.initOwner(ChivAdminApp.getApp().getPrimaryStage());
			VBox page = (VBox) FXMLLoader.load(ChivAdminApp.class.getResource(FORM_PATH));
			form.setScene(new Scene(page));
			form.show();
		} catch (Exception e) {
			e.printStackTrace();
			ChivAdminApp.getApp().log(Txt.errLoadForm("command", e.getLocalizedMessage()), LogType.ERROR);
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	@FXML
	void changeMapAction(ActionEvent event) {
		// note that a text field for server parameters is intentionally omitted
		// because mutators (and mods?) survive the map travel
		createView("Change Map", (CommandMngr cmdMngr, String input) -> cmdMngr.sendChangeMap(input), getAllMaps());
	}

	@FXML
	void rotateMapAction(ActionEvent event) {
		createView("Rotate Map",
				(CommandMngr cmdMngr, String input) -> cmdMngr.sendRotateMap(),
				null);
	}

	@FXML
	void changeGamePasswordAction(ActionEvent event) {
		createView("Set Password     ",
				(CommandMngr cmdMngr, String input) -> cmdMngr.sendChangeGamePassword(input),
				getExamplePasswords());
	}

	@FXML
	void gameConsoleCommandAction(ActionEvent event) {
		createView("Game Command ",
				(CommandMngr cmdMngr, String input) -> cmdMngr.sendConsoleCommand(new SteamId(), ConsoleCommandScope.SCOPE_GAME, input),
				getExampleGameConsoleCommands());
	}

	// variables for storing initial position of the stage at the beginning of drag
	private double initX;
	private double initY;

	/**
	 * Creates 2 kind of command windows:
	 * 1. contains only a command button and some default controls/ displays
	 * 2. contains a combobox in addition. Its value is passed to the BiConsumer.
	 *
	 * @param name
	 *            title of the command
	 * @param cmd
	 *            the command to execute on the CommandMngr, optionally with the combobox value
	 * @param comboItems
	 *            the values to display to pick from
	 */
	private void createView(String name, BiConsumer<CommandMngr, String> cmd, List<String> comboItems) {
		final Stage stage = new Stage(StageStyle.TRANSPARENT);

		double sizeScale = comboItems != null ? 1.7 : 0.9;
		Group rootNode = new Group();
		Scene scene = new Scene(rootNode, 200 * sizeScale, 200 * sizeScale, Color.TRANSPARENT);
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.show();

		// when mouse button is pressed, save the initial position of screen
		rootNode.setOnMousePressed((MouseEvent me) -> {
			initX = me.getScreenX() - stage.getX();
			initY = me.getScreenY() - stage.getY();
		});

		// when screen is dragged, translate it accordingly
		rootNode.setOnMouseDragged((MouseEvent me) -> {
			stage.setX(me.getScreenX() - initX);
			stage.setY(me.getScreenY() - initY);
		});

		List<Node> controls = new ArrayList<>();
		// close icon
		ImageView close = new ImageView(new Image("/resources/ChivAssets/icons/close-icon.png"));
		close.setOnMouseClicked((MouseEvent event) -> {
			stage.close();
		});
		controls.add(close);

		// display the command text
		Text text = new Text(name);
		text.setFill(Color.WHITESMOKE);
		text.setEffect(new Lighting());
		text.setBoundsType(TextBoundsType.VISUAL);
		text.setFont(Font.font(Font.getDefault().getFamily(), name.length() * 3));
		controls.add(text);

		// optional input combobox
		ComboBox<String> combo = new ComboBox<>();
		AutoCompleteComboBoxListener<String> input = comboItems != null ? new AutoCompleteComboBoxListener<>(combo, comboItems) : null;
		if (input != null)
			controls.add(combo);

		// command action
		Button cmdButt = new Button("Send Command");
		cmdButt.setOnAction((ActionEvent evt) -> {
			MainView control = ChivAdminApp.getApp().getMainView();
			ServerConnection conn = control.getConnectionForLogin(null);
			if (conn != null) {
				cmd.accept(conn.getCommandMngr(), combo.getValue());
			}
		});
		cmdButt.setAlignment(Pos.BOTTOM_CENTER);
		controls.add(cmdButt);

		// USE A LAYOUT VBOX FOR EASIER POSITIONING OF THE VISUAL NODES ON SCENE
		VBox vBox = new VBox();
		vBox.setSpacing(input != null ? 20 : 10);
		vBox.setPadding(new Insets(0, 0, 0, 20));
		vBox.setAlignment(Pos.TOP_CENTER);
		vBox.getChildren().addAll(controls);

		Circle dragger = new Circle(100 * sizeScale, 100 * sizeScale, 100 * sizeScale);

		// fill the dragger with some nice radial background
		dragger.setFill(new RadialGradient(-0.3, 135, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop[] {
				new Stop(0, Color.DARKGRAY),
				new Stop(1, Color.BLACK)
		}));

		// add all nodes to main root group
		rootNode.getChildren().addAll(dragger, vBox);
	}

	@SuppressWarnings("serial")
	private List<String> getAllMaps() {
		return new ArrayList<String>() {
			{
				add("TO2-Horde_Sandcastle_p.udk");
				add("AOCTO-Battlegrounds_v3_P");
				add("AOCTO-Belmez-CM_p");
				add("AOCTO-CastleAssault-CM_P");
				add("AOCTO-Citadel_p");
				add("AOCTO-Coldfront_p");
				add("AOCTO-Cove-CM_p");
				add("AOCTO-Darkforest_p");
				add("AOCTO-DrunkenBazaar-CM_p");
				add("AOCTO-Hillside_P");
				add("AOCTO-KingsGarden-CM_p");
				add("AOCTO-Outpost_p");
				add("AOCTO-Stoneshill_P");
				add("AOCTO-Hideout-CM_p");
				add("AOCTO-Irilla-CM_P");
				add("AOCTO-Shore-CM_p");
				add("AOCLTS-Arena3_p");
				add("AOCLTS-ArgonsWall_p");
				add("AOCLTS-Battlegrounds_Farm_p");
				add("AOCLTS-Battlegrounds_p");
				add("AOCLTS-Belmez-CM_p");
				add("AOCLTS-Bridge_p");
				add("AOCLTS-CastleAssault-CM_P");
				add("AOCLTS-Cistern_p");
				add("AOCLTS-Courtyard_p");
				add("AOCLTS-Cove-CM_p");
				add("AOCLTS-Darkforest_Valley_p");
				add("AOCLTS-Darkforest_XL_p");
				add("AOCLTS-Dininghall_p");
				add("AOCLTS-Frigid_p");
				add("AOCLTS-FrostPeak_p");
				add("AOCLTS-Hillside_P");
				add("AOCLTS-HillsidePyre_P");
				add("AOCLTS-Mines_p");
				add("AOCLTS-Moor_p");
				add("AOCLTS-Ruins_Large_P");
				add("AOCLTS-Ruins_P");
				add("AOCLTS-Shipyard_p");
				add("AOCLTS-StoneshillVillage_P");
				add("AOCLTS-ThroneRoom_P");
				add("AOCLTS-Colosseum-CM_p");
				add("AOCLTS-Forest-CM_p");
				add("AOCLTS-Impasse-CM_p");
				add("AOCLTS-NoMercy-CM_p");
				add("AOCCTF-Frigid_p");
				add("AOCCTF-Moor_p");
				add("AOCCTF-Ruins_Large_P");
				add("AOCCTF-Ruins_P");
				add("AOCCTF-Colosseum-CM_p");
				add("AOCDuel-Arena_Flat_p");
				add("AOCDuel-Arena_p");
				add("AOCDuel-Bridge_p");
				add("AOCDuel-CastleAssault-CM_P");
				add("AOCDuel-Cistern_p");
				add("AOCDuel-Courtyard_p");
				add("AOCDuel-Dininghall_p");
				add("AOCDuel-FrostPeak_p");
				add("AOCDuel-Mines_p");
				add("AOCDuel-Moor_p");
				add("AOCDuel-Shaft_p");
				add("AOCDuel-Shipyard_p");
				add("AOCDuel-ThroneRoom_p");
				add("AOCDuel-Tower_p");
				add("AOCDuel-Colosseum-CM_p");
				add("AOCFFA-Arena3_p");
				add("AOCFFA-Bridge_p");
				add("AOCFFA-CastleAssault-CM_P");
				add("AOCFFA-Cistern_p");
				add("AOCFFA-Courtyard_p");
				add("AOCFFA-Cove-CM_p");
				add("AOCFFA-Darkforest_Cistern_p");
				add("AOCFFA-Darkforest_Valley_p");
				add("AOCFFA-Dininghall_p");
				add("AOCFFA-FrostPeak_p");
				add("AOCFFA-Hillside_P");
				add("AOCFFA-HillsidePyre_P");
				add("AOCFFA-Mines_p");
				add("AOCFFA-Moor_p");
				add("AOCFFA-Ruins_P");
				add("AOCFFA-Shipyard_p");
				add("AOCFFA-StoneshillVillage_P");
				add("AOCFFA-Tavern_p");
				add("AOCFFA-ThroneRoomXL_P");
				add("AOCFFA-ColosseumClassicDuel-CM_p");
				add("AOCFFA-Colosseum-CM_p");
				add("AOCFFA-Forest-CM_p");
				add("AOCKOTH-Arena3_p");
				add("AOCKOTH-Darkforest_Valley_p");
				add("AOCKOTH-Hillside_P");
				add("AOCKOTH-Moor_p");
				add("AOCKOTH-Colosseum-CM_p");
				add("AOCKOTH-Impasse-CM_p");
				add("AOCTD-ArgonsWall_p");
				add("AOCTD-Battlegrounds_Farm_p");
				add("AOCTD-Battlegrounds_p");
				add("AOCTD-Bridge_p");
				add("AOCTD-CastleAssault-CM_P");
				add("AOCTD-Cistern_p");
				add("AOCTD-Courtyard_p");
				add("AOCTD-Cove-CM_p");
				add("AOCTD-Darkforest_Valley_p");
				add("AOCTD-Darkforest_XL_p");
				add("AOCTD-Dininghall_p");
				add("AOCTD-Frigid_p");
				add("AOCTD-FrostPeak_p");
				add("AOCTD-Hillside_P");
				add("AOCTD-HillsidePyre_P");
				add("AOCTD-Mines_p");
				add("AOCTD-Moor_p");
				add("AOCTD-Ruins_Large_P");
				add("AOCTD-Ruins_P");
				add("AOCTD-Shipyard_p");
				add("AOCTD-StoneshillVillage_P");
				add("AOCTD-ThroneRoom_P");
				add("AOCTD-Colosseum-CM_p");
				add("AOCTD-ColosseumPendulum-CM_p");
				add("AOCTD-Forest-CM_P");
				add("AOCTD-Impasse-CM_p");
				add("TO2-Crypts");
				add("TO2-Hordetown");
			}
		};
	}

	@SuppressWarnings("serial")
	private List<String> getExamplePasswords() {
		return new ArrayList<String>() {
			{
				add("welcome");
				add("password123");
				add("qwerty");
				add("letmein");
			}
		};
	}

	@SuppressWarnings("serial")
	private List<String> getExampleGameConsoleCommands() {
		return new ArrayList<String>() {
			{
				add("addRedBots 2");
				add("addBlueBots 2");
				add("KillBots");
				add("ManuallyEndGame");
			}
		};
	}

}
