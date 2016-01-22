package it.polito.elite.teaching.learnOpenCVBlocks;

import it.polito.elite.teaching.learnOpenCVBlocks.LearnBlock;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * @author Marco Bartolotta <bartolotta.marco @ gmail.com>
 * @since	2014-07-01
 */
public class ShowHistogram extends LearnBlock {

	@FXML
	private ComboBox<String> channel;

	public ShowHistogram() {
		channel.setItems(FXCollections.observableArrayList("Gray", "Red",
				"Blue", "Green", "RGB"));
		channel.getSelectionModel().select(0);
	}

	@Override
	public boolean validate(final Mat input) {

		return true;
	}

	@Override
	public Mat elaborate(Mat input) {
		List<Mat> images = new ArrayList<Mat>();

		// set the number of bins at 256
		MatOfInt histSize = new MatOfInt(256);
		// only one channel
		MatOfInt channels = new MatOfInt(0);
		// set the ranges
		MatOfFloat histRange = new MatOfFloat(0, 256);

		// compute the histograms for the B, G and R components
		Mat hist_b = new Mat();
		Mat hist_g = new Mat();
		Mat hist_r = new Mat();

		// draw the histogram
		int hist_w = 300; // width of the histogram image
		int hist_h = 300; // height of the histogram image
		int bin_w = (int) Math.round(hist_w / histSize.get(0, 0)[0]);
		Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC3, new Scalar(0,
				0, 0));

		codeVersion = channel.getSelectionModel().getSelectedIndex();

		switch (codeVersion) {
		case 0:
			Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2GRAY);
			Core.split(input, images);
			Imgproc.calcHist(images.subList(0, 1), channels, new Mat(), hist_b,
					histSize, histRange, false);
			Core.normalize(hist_b, hist_b, 0, histImage.rows(),
					Core.NORM_MINMAX, -1, new Mat());
			for (int i = 1; i < histSize.get(0, 0)[0]; i++)
				Core.line(
						histImage,
						new Point(bin_w * (i - 1), hist_h
								- Math.round(hist_b.get(i - 1, 0)[0])),
						new Point(bin_w * (i), hist_h
								- Math.round(hist_b.get(i, 0)[0])), new Scalar(
								255, 0, 0), 2, 8, 0);
			break;
		case 1:
			Core.split(input, images);
			Imgproc.calcHist(images.subList(2, 3), channels, new Mat(), hist_r,
					histSize, histRange, false);
			Core.normalize(hist_r, hist_r, 0, histImage.rows(),
					Core.NORM_MINMAX, -1, new Mat());
			for (int i = 1; i < histSize.get(0, 0)[0]; i++)
				Core.line(
						histImage,
						new Point(bin_w * (i - 1), hist_h
								- Math.round(hist_r.get(i - 1, 0)[0])),
						new Point(bin_w * (i), hist_h
								- Math.round(hist_r.get(i, 0)[0])), new Scalar(
								0, 0, 255), 2, 8, 0);
			break;
		case 2:
			Core.split(input, images);
			Imgproc.calcHist(images.subList(0, 1), channels, new Mat(), hist_b,
					histSize, histRange, false);
			Core.normalize(hist_b, hist_b, 0, histImage.rows(),
					Core.NORM_MINMAX, -1, new Mat());
			for (int i = 1; i < histSize.get(0, 0)[0]; i++)
				Core.line(
						histImage,
						new Point(bin_w * (i - 1), hist_h
								- Math.round(hist_b.get(i - 1, 0)[0])),
						new Point(bin_w * (i), hist_h
								- Math.round(hist_b.get(i, 0)[0])), new Scalar(
								255, 0, 0), 2, 8, 0);
			break;
		case 3:
			Core.split(input, images);
			Imgproc.calcHist(images.subList(1, 2), channels, new Mat(), hist_g,
					histSize, histRange, false);
			Core.normalize(hist_g, hist_g, 0, histImage.rows(),
					Core.NORM_MINMAX, -1, new Mat());
			for (int i = 1; i < histSize.get(0, 0)[0]; i++)
				Core.line(
						histImage,
						new Point(bin_w * (i - 1), hist_h
								- Math.round(hist_g.get(i - 1, 0)[0])),
						new Point(bin_w * (i), hist_h
								- Math.round(hist_g.get(i, 0)[0])), new Scalar(
								0, 255, 0), 2, 8, 0);
			break;
		case 4:
			Core.split(input, images);
			Imgproc.calcHist(images.subList(0, 1), channels, new Mat(), hist_b,
					histSize, histRange, false);
			Imgproc.calcHist(images.subList(2, 3), channels, new Mat(), hist_r,
					histSize, histRange, false);
			Imgproc.calcHist(images.subList(1, 2), channels, new Mat(), hist_g,
					histSize, histRange, false);

			Core.normalize(hist_b, hist_b, 0, histImage.rows(),
					Core.NORM_MINMAX, -1, new Mat());
			Core.normalize(hist_r, hist_r, 0, histImage.rows(),
					Core.NORM_MINMAX, -1, new Mat());
			Core.normalize(hist_g, hist_g, 0, histImage.rows(),
					Core.NORM_MINMAX, -1, new Mat());

			for (int i = 1; i < histSize.get(0, 0)[0]; i++) {
				Core.line(
						histImage,
						new Point(bin_w * (i - 1), hist_h
								- Math.round(hist_b.get(i - 1, 0)[0])),
						new Point(bin_w * (i), hist_h
								- Math.round(hist_b.get(i, 0)[0])), new Scalar(
								255, 0, 0), 2, 8, 0);
				Core.line(
						histImage,
						new Point(bin_w * (i - 1), hist_h
								- Math.round(hist_r.get(i - 1, 0)[0])),
						new Point(bin_w * (i), hist_h
								- Math.round(hist_r.get(i, 0)[0])), new Scalar(
								0, 0, 255), 2, 8, 0);
				Core.line(
						histImage,
						new Point(bin_w * (i - 1), hist_h
								- Math.round(hist_g.get(i - 1, 0)[0])),
						new Point(bin_w * (i), hist_h
								- Math.round(hist_g.get(i, 0)[0])), new Scalar(
								0, 255, 0), 2, 8, 0);
			}
			break;
		}

		return histImage;
	}
}
