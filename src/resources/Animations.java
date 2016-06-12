package resources;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animations {

	/**
	 * Applies mouse hover effect: expand
	 *
	 * @param uiControl the node to apply the effect to
	 */
	public static void hoverEffectEntered(Node uiControl, double expandScale) {
		ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), uiControl);
		scaleTransition.setCycleCount(1);
		scaleTransition.setInterpolator(Interpolator.EASE_BOTH);
		scaleTransition.setFromX(uiControl.getScaleX());
		scaleTransition.setFromY(uiControl.getScaleY());
		scaleTransition.setToX(expandScale);
		scaleTransition.setToY(expandScale);
		scaleTransition.playFromStart();
	}

	/**
	 * Applies mouse leave effect: revert to normal size
	 *
	 * @param uiControl the node to apply the effect to
	 */
	public static void hoverEffectExited(Node uiControl) {
		ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), uiControl);
		scaleTransition.setCycleCount(1);
		scaleTransition.setInterpolator(Interpolator.EASE_BOTH);
		scaleTransition.setFromX(uiControl.getScaleX());
		scaleTransition.setFromY(uiControl.getScaleY());
		scaleTransition.setToX(1);
		scaleTransition.setToY(1);
		scaleTransition.playFromStart();
	}

	/**
	 * Applies clicked effect: expand and shrink
	 *
	 * @param uiControl the node to apply the effect to
	 */
	public static void effectClicked(Node uiControl) {
		ScaleTransition startScaleTransition = new ScaleTransition(Duration.millis(200), uiControl);
		startScaleTransition.setCycleCount(1);
		startScaleTransition.setInterpolator(Interpolator.EASE_BOTH);

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), uiControl);
		fadeTransition.setCycleCount(1);
		fadeTransition.setInterpolator(Interpolator.EASE_BOTH);

		ParallelTransition startTransition = new ParallelTransition();
		startTransition.setCycleCount(2);
		startTransition.setAutoReverse(true);
		startTransition.getChildren().addAll(startScaleTransition, fadeTransition);

		ScaleTransition initTransition = new ScaleTransition(Duration.millis(200), uiControl);
		initTransition.setToX(1);
		initTransition.setToY(1);
		initTransition.setCycleCount(1);
		initTransition.setInterpolator(Interpolator.EASE_BOTH);

		SequentialTransition startsSequentialTransition = new SequentialTransition();
		startsSequentialTransition.getChildren().addAll(startTransition, initTransition);

		startScaleTransition.setFromX(uiControl.getScaleX());
		startScaleTransition.setFromY(uiControl.getScaleY());
		startScaleTransition.setToX(2);
		startScaleTransition.setToY(2);
		fadeTransition.setFromValue(1.0f);
		fadeTransition.setToValue(0.5f);
		startsSequentialTransition.playFromStart();
	}
}
