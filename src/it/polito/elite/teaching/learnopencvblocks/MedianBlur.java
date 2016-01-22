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
public class MedianBlur extends LearnBlock {

	@FXML
	private ChoiceBox<String> kSize;

	private int dim[] = { 3, 5, 7 };

	public MedianBlur() {
		error = "";
		kSize.setItems(FXCollections.observableArrayList("3", "5", "7"));
		kSize.getSelectionModel().select(0);
	}

	@Override
	public boolean validate(final Mat input) {
		if (kSize.getSelectionModel().isEmpty()) {
			error = "Size of the kernel must be setted";
			return false;
		}
		return true;
	}

	@Override
	public Mat elaborate(Mat input) {

		Imgproc.medianBlur(input, input, dim[kSize.getSelectionModel()
				.getSelectedIndex()]);
		return input;
	}

}