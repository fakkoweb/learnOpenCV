package it.polito.elite.teaching.learnOpenCVBlocks;

import it.polito.elite.teaching.learnOpenCVBlocks.LearnBlock;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * @author Marco Bartolotta <bartolotta.marco @ gmail.com>
 * @since	2014-07-01
 */
public class CvtColor extends LearnBlock {

	@FXML
	private ChoiceBox<String> cvtCode;

	private Mat dst;
	int conversion[] = { Imgproc.COLOR_BGR2GRAY, Imgproc.COLOR_BGR2YCrCb,
			Imgproc.COLOR_BGR2HSV };

	public CvtColor() {
		dst = new Mat();
		error = "";
		cvtCode.setItems(FXCollections.observableArrayList("BGR2GRAY",
				"BGR2YCrCb", "BGR2HLS"));
		cvtCode.getSelectionModel().select(0);
	}

	@Override
	public boolean validate(final Mat input) {
		if (cvtCode.getSelectionModel().isEmpty()) {
			error = "Code must be setted";
			return false;
		}
		return true;
	}

	@Override
	public Mat elaborate(Mat input) {
		Imgproc.cvtColor(input, dst, conversion[cvtCode.getSelectionModel()
				.getSelectedIndex()]);

		return dst;
	}

}