package it.polito.elite.teaching.learnOpenCVBlocks;

import it.polito.elite.teaching.learnOpenCVBlocks.LearnBlock;

import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * @author Marco Bartolotta <bartolotta.marco @ gmail.com>
 * @since	2014-07-01
 */
public class AddWeighted extends LearnBlock {

	@FXML
	private ComboBox<String> position;

	@FXML
	private TextField weight;

	@FXML
	private TextField gamma;

	@FXML
	private ImageView sec;

	@FXML
	private TextField path;

	private Stage stage;

	private Mat dst;

	private Mat secondary;

	public AddWeighted() {
		//loading default test image
		File file = new File( getClass().getResource("../learnOpenCV/img/logo.jpg").getPath() );
		String imgpath=file.getAbsolutePath().toString();
		
		secondary = Highgui.imread( imgpath );

		path.setEditable(false);
		path.setVisible(false);
		setImg(secondary);
		position.setItems(FXCollections.observableArrayList("Top-left",
				"Bottom-left", "Center", "Top-right", "Bottom-right"));
		position.getSelectionModel().selectFirst();
		weight.setText("0.5");
		gamma.setText("0");

	}

	@Override
	public boolean validate(final Mat input) {
		if (secondary.height() > input.height()
				|| secondary.width() > input.width()
				|| secondary.height() == input.height()
				&& secondary.width() == input.width()
				&& position.getSelectionModel().getSelectedIndex() != 0) {
			error = "secondary image should have dimensions less or equal primary ones";
			return false;
		}

		if (position.getSelectionModel().isEmpty()
				|| weight.getText().isEmpty() || gamma.getText().isEmpty()) {
			error = "All parameters must be setted";
			return false;
		} else {
			try {
				Double.parseDouble(weight.getText());
				Double.parseDouble(gamma.getText());
				if (Double.parseDouble(weight.getText()) < 0
						|| Double.parseDouble(weight.getText()) > 1) {
					
					return true;
				}
			} catch (Exception e) {
				error = "Weight, gamma must be double values and 0 < weight < 1";
				return false;
			}
		}
		return true;
	}

	@Override
	public Mat elaborate(Mat input) {
		Mat sum = new Mat(input.size(), input.type());
		dst = new Mat(input.size(), input.type());
		input.copyTo(dst);
		Rect roi = null;

		codeVersion = position.getSelectionModel().getSelectedIndex();

		switch (codeVersion) {
		case 0:
			roi = new Rect(0, 0, secondary.width(), secondary.height());// top-left
			break;
		case 1:
			roi = new Rect(0, input.height() - secondary.height(),
					secondary.width(), secondary.height());// bottom-left
			break;
		case 2:
			roi = new Rect(input.width() / 4, input.height() / 4,
					secondary.width(), secondary.height());// center
			break;
		case 3:
			roi = new Rect(input.width() - secondary.width(), 0,
					secondary.width(), secondary.height());// top-right
			break;
		case 4:
			roi = new Rect(input.width() - secondary.width(), input.height()
					- secondary.height(), secondary.width(), secondary.height());// Bottom-Right
			break;
		}

		sum = new Mat(dst, roi);
		secondary.convertTo(secondary, input.type());
		Core.addWeighted(sum, Double.parseDouble(weight.getText()), secondary,
				1 - Double.parseDouble(weight.getText()),
				Double.parseDouble(gamma.getText()), sum);
		return dst;
	}

	private void setImg(Mat s) {
		Image image = Mat2Image(s);
		sec.setImage(image);
	}

	public void openSec() {
		try {
			FileChooser chooser = new FileChooser();
			chooser.setTitle("View Pictures");
			chooser.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter("All Images", "*.*"),
					new FileChooser.ExtensionFilter("JPG", "*.jpg"),
					new FileChooser.ExtensionFilter("PNG", "*.png"),
					new FileChooser.ExtensionFilter("BMP", "*.bmp"),
					new FileChooser.ExtensionFilter("gif", "*.gif"));

			File file = chooser.showOpenDialog(stage);
			String pt = file.getAbsolutePath().toString();
			secondary = Highgui.imread(pt);
			path.setText(pt);
			setImg(secondary);
		} catch (Exception e) {
			System.out.println("No file opened");
		}

	}

}