package it.polito.elite.teaching.learnOpenCVBlocks;

import it.polito.elite.teaching.learnOpenCVBlocks.LearnBlock;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * @author Marco Bartolotta <bartolotta.marco @ gmail.com>
 * @since	2014-07-01
 */
public class Scharr extends LearnBlock {

	@FXML
	private ChoiceBox<String> ddepth;

	@FXML
	private ChoiceBox<String> dxdy;

	@FXML
	private TextField delta;

	@FXML
	private TextField scale;

	private int depth[] = { CvType.CV_16S, CvType.CV_32F, CvType.CV_64F };

	private Mat dst;

	public Scharr() {
		dst = new Mat();
		error = "";
		ddepth.setItems(FXCollections.observableArrayList("16", "32", "64"));
		dxdy.setItems(FXCollections.observableArrayList("dx", "dy"));
		ddepth.getSelectionModel().select(0);
		dxdy.getSelectionModel().select(0);
	}

	@Override
	public boolean validate(final Mat input) {
		if (ddepth.getSelectionModel().isEmpty()
				|| dxdy.getSelectionModel().isEmpty()) {
			error = "Ddepht,direction must be setted";
			return false;
		} else {
			if (delta.getText().isEmpty() && scale.getText().isEmpty()) {
				codeVersion = 0;
			} else {
				if (delta.getText().isEmpty() || scale.getText().isEmpty()) {
					error = "delta and scale must be setted together";
					return false;
				} else {
					try {
						Double.parseDouble(delta.getText());
						Double.parseDouble(scale.getText());
						codeVersion = 1;
					} catch (Exception e) {
						error = "delta and scale must be double values";
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
			Imgproc.Scharr(input, dst, depth[ddepth.getSelectionModel()
					.getSelectedIndex()], Math.abs(dxdy.getSelectionModel()
					.getSelectedIndex()), Math.abs(dxdy.getSelectionModel()
					.getSelectedIndex() - 1));
			break;

		case 1:
			Imgproc.Scharr(input, dst, depth[ddepth.getSelectionModel()
					.getSelectedIndex()], Math.abs(dxdy.getSelectionModel()
					.getSelectedIndex()), Math.abs(dxdy.getSelectionModel()
					.getSelectedIndex() - 1), Double.parseDouble(scale
					.getText()), Double.parseDouble(delta.getText()));
			break;
		}

		return dst;
	}

}