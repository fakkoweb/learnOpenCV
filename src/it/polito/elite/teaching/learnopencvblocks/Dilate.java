package it.polito.elite.teaching.learnOpenCVBlocks;

import it.polito.elite.teaching.learnOpenCVBlocks.LearnBlock;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * @author Marco Bartolotta <bartolotta.marco @ gmail.com>
 * @since	2014-07-01
 */
public class Dilate extends LearnBlock {

	@FXML
	private TextField anchorX;

	@FXML
	private TextField anchorY;

	@FXML
	private TextField iterations;

	@FXML
	private ChoiceBox<String> kernelType;

	@FXML
	private ChoiceBox<String> kernelWidth;

	@FXML
	private ChoiceBox<String> kernelHeight;

	private Mat kernel;

	private int kType[] = { Imgproc.MORPH_RECT, Imgproc.MORPH_CROSS,
			Imgproc.MORPH_ELLIPSE };

	private int height[] = { 3, 5, 7 };

	private int widht[] = { 3, 5, 7 };

	public Dilate() {
		kernelType.setItems(FXCollections.observableArrayList("MORPH_RECT",
				"MORPH_CROSS", "MORPH_ELLIPSE"));
		kernelWidth.setItems(FXCollections.observableArrayList("3", "5", "7"));
		kernelHeight.setItems(FXCollections.observableArrayList("3", "5", "7"));
		error = "";
		kernelType.getSelectionModel().select(0);
		kernelWidth.getSelectionModel().select(0);
		kernelHeight.getSelectionModel().select(0);
	}

	@Override
	public boolean validate(final Mat input) {
		if (kernelType.getSelectionModel().isEmpty()
				|| kernelWidth.getSelectionModel().isEmpty()
				|| kernelHeight.getSelectionModel().isEmpty()) {
			error = "Type and size of the kernel must be setted";
			return false;
		} else {
			if (anchorX.getText().isEmpty() && anchorY.getText().isEmpty()
					&& iterations.getText().isEmpty())
				codeVersion = 0;
			else {
				if (anchorX.getText().isEmpty() || anchorY.getText().isEmpty()
						|| iterations.getText().isEmpty()) {
					error = "Anchor points and iterations must be setted together";
					return false;
				} else {
					try {
						Integer.parseInt(anchorX.getText());
						Integer.parseInt(anchorY.getText());
						Integer.parseInt(iterations.getText());
						codeVersion = 1;
					} catch (Exception e) {
						error = "Anchor points and iterations must be integer values";
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public Mat elaborate(Mat input) {
		Mat kernel = Imgproc.getStructuringElement(kType[kernelType
				.getSelectionModel().getSelectedIndex()], new Size(
				widht[kernelWidth.getSelectionModel().getSelectedIndex()],
				height[kernelHeight.getSelectionModel().getSelectedIndex()]));

		switch (codeVersion) {

		case 0:
			Imgproc.dilate(input, input, kernel);
			break;
		case 1:
			Imgproc.dilate(
					input,
					input,
					kernel,
					new Point(Integer.parseInt(anchorX.getText()), Integer
							.parseInt(anchorY.getText())), Integer
							.parseInt(iterations.getText()));
			break;
		}

		return input;
	}

}