package it.polito.elite.teaching.learnOpenCVBlocks;

import it.polito.elite.teaching.learnOpenCVBlocks.LearnBlock;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import org.opencv.imgproc.*;
import org.opencv.core.Mat;
import org.opencv.core.Size;

/**
 * @author Marco Bartolotta <bartolotta.marco @ gmail.com>
 * @since	2014-07-01
 */
public class GaussianBlur extends LearnBlock {

	@FXML
	private ChoiceBox<String> ksizew;

	@FXML
	private ChoiceBox<String> ksizeh;

	@FXML
	private TextField sigmax;

	@FXML
	private TextField sigmay;

	private int height[] = { 3, 5, 7 };

	private int width[] = { 3, 5, 7 };

	public GaussianBlur() {
		ksizew.setItems(FXCollections.observableArrayList("3", "5", "7"));
		ksizeh.setItems(FXCollections.observableArrayList("3", "5", "7"));
		error = "";

		sigmax.setText("10");
		ksizew.getSelectionModel().select(0);
		ksizeh.getSelectionModel().select(0);
	}

	@Override
	public boolean validate(final Mat input) {

		if (ksizew.getSelectionModel().isEmpty()
				|| ksizeh.getSelectionModel().isEmpty()
				|| sigmax.getText().isEmpty()) {
			error = "Sigmax and kernel dimension must be setted";
			return false;
		} else {
			if (sigmay.getText().isEmpty()) {
				try {
					Integer.parseInt(sigmax.getText());
					codeVersion = 0;
				} catch (Exception e) {
					error = "sigmax must be integer value";
					return false;
				}
			} else {
				try {
					Double.parseDouble(sigmay.getText());
					Double.parseDouble(sigmax.getText());
					codeVersion = 1;

				} catch (Exception e) {
					error = "sigmax and sigmay must be integer values";
					return false;
				}
			}

		}
		return true;

	}

	@Override
	public Mat elaborate(Mat input) {

		switch (codeVersion) {
		case 0:
			Imgproc.GaussianBlur(input, input, new Size(width[ksizew
					.getSelectionModel().getSelectedIndex()], height[ksizeh
					.getSelectionModel().getSelectedIndex()]), Integer
					.parseInt(sigmax.getText()));
			break;
		case 1:
			Imgproc.GaussianBlur(input, input, new Size(width[ksizew
					.getSelectionModel().getSelectedIndex()], height[ksizeh
					.getSelectionModel().getSelectedIndex()]), Integer
					.parseInt(sigmax.getText()), Integer.parseInt(sigmay
					.getText()));
			break;
		}

		return input;
	}

}