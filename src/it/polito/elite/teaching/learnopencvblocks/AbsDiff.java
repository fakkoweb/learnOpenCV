package it.polito.elite.teaching.learnOpenCVBlocks;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

/**
 * @author Marco Bartolotta <bartolotta.marco @ gmail.com>
 * @since	2014-07-01
 */
public class AbsDiff extends LearnBlock {
	@FXML
	private ImageView sec;

	@FXML
	private TextField path;

	private Stage stage;

	private Mat dst;

	private Mat secondary;

	public AbsDiff() {
		dst = new Mat();
		
		//loading default test image
		File file = new File( getClass().getResource("../learnOpenCV/img/adaptive.jpg").getPath() );
		String imgpath=file.getAbsolutePath().toString();
		secondary = Highgui.imread( imgpath );
		
		path.setText(imgpath);
		setImg(secondary);
		path.setEditable(false);
		path.setVisible(false);

	}

	@Override
	public boolean validate(final Mat input) {
		if (secondary.height() != input.height()
				|| secondary.width() != input.width()) {
			error = "secondary image should have dimensions equal to primary ones";
			return false;
		}

		return true;
	}

	@Override
	public Mat elaborate(Mat input) {
		Core.absdiff(input, secondary, dst);

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
