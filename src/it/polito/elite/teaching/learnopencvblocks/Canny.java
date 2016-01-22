package it.polito.elite.teaching.learnOpenCVBlocks;

import it.polito.elite.teaching.learnOpenCVBlocks.LearnBlock;

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
public class Canny extends LearnBlock {

	@FXML
	private ScrollBar thresh1;

	@FXML
	private ChoiceBox<String> thresh2;

	@FXML
	private ChoiceBox<String> apertureSize;

	@FXML
	private CheckBox l2Gradient;

	@FXML
	private Label valueThresh1;

	private Mat dst;

	private int size[] = { 3, 5, 7 };

	private int multip[] = { 3, 4, 5, 6 };

	public Canny() {
		dst = new Mat();
		error = "";
		apertureSize.setItems(FXCollections.observableArrayList("3x3", "5x5",
				"7x7"));
		thresh2.setItems(FXCollections.observableArrayList("3", "4", "5", "6"));
		thresh1.setMax(100);
		thresh1.setMin(0);
		thresh1.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				valueThresh1.setText(Double.toString(thresh1.getValue()));
			}
		});

		thresh2.getSelectionModel().select(0);
	}

	@Override
	public boolean validate(final Mat input) {
		if (thresh2.getSelectionModel().isEmpty()) {
			error = "Threshold2 must be setted";
			return false;
		} else {
			if (apertureSize.getSelectionModel().isEmpty()) {
				codeVersion = 0;
			} else {
				codeVersion = 1;
			}
		}
		return true;
	}

	@Override
	public Mat elaborate(Mat input) {

		switch (codeVersion) {
		case 0:
			Imgproc.Canny(input, dst, thresh1.getValue(),
					multip[thresh2.getSelectionModel().getSelectedIndex()]
							* thresh1.getValue());
			break;
		case 1:
			Imgproc.Canny(input, dst, thresh1.getValue(),
					multip[thresh2.getSelectionModel().getSelectedIndex()]
							* thresh1.getValue(), size[apertureSize
							.getSelectionModel().getSelectedIndex()],
					l2Gradient.isSelected());
			break;
		}
		return dst;
	}

}