package it.polito.elite.teaching.learnOpenCVBlocks;

import it.polito.elite.teaching.learnOpenCVBlocks.LearnBlock;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * @author Marco Bartolotta <bartolotta.marco @ gmail.com>
 * @since	2014-07-01
 */
public class FourierTransform extends LearnBlock {

	private List<Mat> planes;
	private Mat complexImage;

	public FourierTransform() {
		this.planes = new ArrayList<>();
		this.complexImage = new Mat();
	}

	@Override
	public boolean validate(final Mat input) {

		return true;
	}

	@Override
	public Mat elaborate(Mat input) {

		Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2GRAY);
		// init
		Mat padded = new Mat();
		// get the optimal rows size for dft
		int addPixelRows = Core.getOptimalDFTSize(input.rows());
		// get the optimal cols size for dft
		int addPixelCols = Core.getOptimalDFTSize(input.cols());
		// apply the optimal cols and rows size to the image
		Imgproc.copyMakeBorder(input, padded, 0, addPixelRows - input.rows(),
				0, addPixelCols - input.cols(), Imgproc.BORDER_CONSTANT,
				Scalar.all(0));

		padded.convertTo(padded, CvType.CV_32F);
		this.planes.add(padded);
		this.planes.add(Mat.zeros(padded.size(), CvType.CV_32F));
		// prepare a complex image for performing the dft
		Core.merge(this.planes, this.complexImage);
		// dft
		// complexImage.convertTo(complexImage, CvType.CV_64FC2);
		Core.dft(this.complexImage, this.complexImage);

		List<Mat> newPlanes = new ArrayList<>();
		Mat mag = new Mat();
		// split the comples image in two planes
		Core.split(complexImage, newPlanes);
		// compute the magnitude
		Core.magnitude(newPlanes.get(0), newPlanes.get(1), mag);

		// move to a logarithmic scale
		Core.add(mag, Scalar.all(1), mag);
		Core.log(mag, mag);

		mag = mag.submat(new Rect(0, 0, mag.cols() & -2, mag.rows() & -2));
		int cx = mag.cols() / 2;
		int cy = mag.rows() / 2;

		Mat q0 = new Mat(mag, new Rect(0, 0, cx, cy));
		Mat q1 = new Mat(mag, new Rect(cx, 0, cx, cy));
		Mat q2 = new Mat(mag, new Rect(0, cy, cx, cy));
		Mat q3 = new Mat(mag, new Rect(cx, cy, cx, cy));

		Mat tmp = new Mat();
		q0.copyTo(tmp);
		q3.copyTo(q0);
		tmp.copyTo(q3);

		q1.copyTo(tmp);
		q2.copyTo(q1);
		tmp.copyTo(q2);

		Core.normalize(mag, mag, 0, 255, Core.NORM_MINMAX);
		planes = new ArrayList<>();
		return mag;
	}

}