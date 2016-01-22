package it.polito.elite.teaching.learnOpenCVBlocks;

import it.polito.elite.teaching.learnOpenCVBlocks.LearnBlock;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import org.opencv.imgproc.*;

/**
 * @author Marco Bartolotta <bartolotta.marco @ gmail.com>
 * @since	2014-07-01
 */
public class Laplacian extends LearnBlock {

	@FXML
	private ChoiceBox<String> ddepth;

	@FXML
	private ChoiceBox<String> ksize;

	@FXML
	private TextField scale;

	@FXML
	private TextField delta;

	private int depth[] = { CvType.CV_16S, CvType.CV_32F, CvType.CV_64F };

	private int dim[] = { 1, 3, 5, 7 };

	private Mat dest;

	public Laplacian() {
		dest = new Mat();
		error = "";
		ddepth.setItems(FXCollections.observableArrayList("8", "16", "32"));
		ksize.setItems(FXCollections.observableArrayList("1", "3", "5", "7"));
		ddepth.getSelectionModel().select(0);
	}

	@Override
	public boolean validate(final Mat input) {
		if (ddepth.getSelectionModel().isEmpty()) {
			error = "Ddepht must be setted.";
			return false;
		} else {
			if (ksize.getSelectionModel().isEmpty()
					&& scale.getText().isEmpty() && delta.getText().isEmpty()) {
				codeVersion = 0;
			} else {
				if (ksize.getSelectionModel().isEmpty()
						|| scale.getText().isEmpty()
						|| delta.getText().isEmpty()) {
					error = "Kernel Size, scale and delta must be setted together.";
					return false;
				} else {
					try {
						Double.parseDouble(scale.getText());
						Double.parseDouble(delta.getText());
						codeVersion = 1;
					} catch (Exception e) {
						error = "scale and delta must be double values.";
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public Mat elaborate(Mat input) {
		switch (codeVersion) {

		case 0:
			Imgproc.Laplacian(input, dest, depth[ddepth.getSelectionModel()
					.getSelectedIndex()]);
			break;

		case 1:
			Imgproc.Laplacian(input, dest, depth[ddepth.getSelectionModel()
					.getSelectedIndex()], dim[ksize.getSelectionModel()
					.getSelectedIndex()], Double.parseDouble(scale.getText()),
					Double.parseDouble(delta.getText()));
			break;
		}

		return dest;
	}

}