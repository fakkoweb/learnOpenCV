package it.polito.elite.teaching.learnOpenCVBlocks;

import it.polito.elite.teaching.learnOpenCVBlocks.LearnBlock;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import javafx.scene.control.ChoiceBox;

/**
 * @author Marco Bartolotta <bartolotta.marco @ gmail.com>
 * @since	2014-07-01
 */
public class Blur extends LearnBlock {

	@FXML
	private TextField anchorX;

	@FXML
	private TextField anchorY;

	@FXML
	private ChoiceBox<String> kernelWidth;

	@FXML
	private ChoiceBox<String> kernelHeight;

	private int height[] = { 3, 5, 7 };

	private int width[] = { 3, 5, 7 };

	public Blur() {
		kernelWidth.setItems(FXCollections.observableArrayList("3", "5", "7"));
		kernelHeight.setItems(FXCollections.observableArrayList("3", "5", "7"));

		error = "";
		kernelWidth.getSelectionModel().select(0);
		kernelHeight.getSelectionModel().select(0);
	}

	@Override
	public boolean validate(final Mat input) {
		if (kernelWidth.getSelectionModel().isEmpty()
				|| kernelHeight.getSelectionModel().isEmpty()) {
			error = "Kernel size must be setted";
			return false;
		} else {
			if (anchorX.getText().isEmpty() && anchorY.getText().isEmpty()) {
				// return true;
			} else {
				if (anchorX.getText().isEmpty() != anchorY.getText().isEmpty()) { // ||
					error = "Anchor X and anchor Y must be setted together";
					return false;
				} else {
					try {
						Integer.parseInt(anchorX.getText());
						Integer.parseInt(anchorY.getText());

					} catch (Exception e) {
						error = "Anchor points must be integer values";
						return false;
					}
					if (Integer.parseInt(anchorX.getText()) >= width[kernelWidth
							.getSelectionModel().getSelectedIndex()]
							|| Integer.parseInt(anchorY.getText()) >= height[kernelHeight
									.getSelectionModel().getSelectedIndex()]) {
						error = "Anchor point x and y must be less than kernel widht and height respectively";
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public Mat elaborate(Mat input) {
		if (anchorX.getText().isEmpty() && anchorY.getText().isEmpty()) {
			codeVersion = 0;
		} else {
			codeVersion = 1;
		}

		switch (codeVersion) {

		case 0:
			Imgproc.blur(input, input, new Size(height[kernelHeight
					.getSelectionModel().getSelectedIndex()], width[kernelWidth
					.getSelectionModel().getSelectedIndex()]));
			break;
		case 1:
			Imgproc.blur(
					input,
					input,
					new Size(height[kernelHeight.getSelectionModel()
							.getSelectedIndex()], width[kernelWidth
							.getSelectionModel().getSelectedIndex()]),
					new Point(Integer.parseInt(anchorX.getText()), Integer
							.parseInt(anchorY.getText())));
			break;
		}

		return input;
	}

}