package it.polito.elite.teaching.learnOpenCVBlocks;

import it.polito.elite.teaching.learnOpenCVBlocks.LearnBlock;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * @author Marco Bartolotta <bartolotta.marco @ gmail.com>
 * @since	2014-07-01
 */
public class AdaptiveThreshold extends LearnBlock {

	@FXML
	private ScrollBar maxValue;

	@FXML
	private ChoiceBox<String> adaptiveMethod;

	@FXML
	private ChoiceBox<String> thresholdType;

	@FXML
	private ChoiceBox<String> blockSize;

	@FXML
	private TextField c;

	@FXML
	private Label valueMax;

	private int methods[] = { Imgproc.ADAPTIVE_THRESH_MEAN_C,
			Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C };

	private int threshType[] = { Imgproc.THRESH_BINARY,
			Imgproc.THRESH_BINARY_INV };

	private int size[] = { 3, 5, 7 };

	private Mat dst;

	private Mat inputGrey;

	public AdaptiveThreshold() {
		dst = new Mat();
		error = "";
		adaptiveMethod.setItems(FXCollections.observableArrayList(
				"ADAPTIVE_THRESH_MEAN_C", "ADAPTIVE_THRESH_GAUSSIAN_C"));
		thresholdType.setItems(FXCollections.observableArrayList(
				"THRESH_BINARY", "THRESH_BINARY_INV"));
		blockSize.setItems(FXCollections.observableArrayList("3", "5", "7"));
		maxValue.setMax(255);
		maxValue.setMin(0);
		maxValue.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				valueMax.setText(Double.toString(maxValue.getValue()));
			}
		});

		thresholdType.getSelectionModel().select(0);
		adaptiveMethod.getSelectionModel().select(0);
		blockSize.getSelectionModel().select(0);
		c.setText("0");
	}

	@Override
	public boolean validate(final Mat input) {
		if (adaptiveMethod.getSelectionModel().isEmpty()
				|| thresholdType.getSelectionModel().isEmpty()
				|| blockSize.getSelectionModel().isEmpty()
				|| c.getText().isEmpty()) {
			error = "All parameters must be setted";
			return false;
		} else {
			try {
				Double.parseDouble(c.getText());
			} catch (Exception e) {
				error = "c must be a double value";
				return false;
			}
		}
		return true;
	}

	@Override
	public Mat elaborate(Mat input) {
		inputGrey = new Mat(input.width(), input.height(), CvType.CV_8UC1);
		Imgproc.cvtColor(input, inputGrey, Imgproc.COLOR_BGR2GRAY);

		Imgproc.adaptiveThreshold(
				inputGrey,
				dst,
				maxValue.getValue(),
				methods[adaptiveMethod.getSelectionModel().getSelectedIndex()],
				threshType[thresholdType.getSelectionModel().getSelectedIndex()],
				size[blockSize.getSelectionModel().getSelectedIndex()],
				Double.parseDouble(c.getText()));

		return dst;
	}

}
