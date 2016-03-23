/*
 * Copyright (c) 2011-2016, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.examples.calibration;

import boofcv.alg.distort.AdjustmentType;
import boofcv.alg.distort.ImageDistort;
import boofcv.alg.distort.LensDistortionOps;
import boofcv.core.image.border.BorderType;
import boofcv.gui.ListDisplayPanel;
import boofcv.gui.image.ImagePanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.UtilIO;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.calib.IntrinsicParameters;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.Planar;

import java.awt.image.BufferedImage;

/**
 * All real camera lens have distortion.  This distortion causes large errors when attempting to recover the
 * scene's structure and camera's motion.  The following example demonstrates how the lens distortion can be
 * removed from an image after the camera has been calibrated.
 *
 * After lens distortion has been removed the new image will not be properly contained inside the original
 * image side.  Methods are provided for scaling and translating the image to maximize the view area
 * using different metrics.  After this adjustment has been done the new image is equivalent to one being
 * generated by a virtual camera with a different set of intrinsic parameters.
 *
 * @author Peter Abeles
 */
public class ExampleRemoveLensDistortion {

	public static void main( String args[] ) {
		String calibDir = UtilIO.pathExample("calibration/mono/Sony_DSC-HX5V_Chess/");
		String imageDir = UtilIO.pathExample("structure/");

		// load calibration parameters from the previously calibrated camera
		IntrinsicParameters param = UtilIO.loadXML(calibDir , "intrinsic.xml");

		// load images and convert the image into a color BoofCV format
		BufferedImage orig = UtilImageIO.loadImage(imageDir , "dist_cyto_01.jpg");
		Planar<GrayF32> distortedImg =
				ConvertBufferedImage.convertFromMulti(orig, null,true, GrayF32.class);

		int numBands = distortedImg.getNumBands();

		// create new transforms which optimize view area in different ways.
		// shrink makes sure there are no dead zones inside the image
		// fullView will include the entire original image
		// The border is VALUE, which defaults to black, just so you can see it
		ImageDistort allInside = LensDistortionOps.imageRemoveDistortion(AdjustmentType.EXPAND, BorderType.VALUE, param, null,
				ImageType.pl(numBands, GrayF32.class));
		ImageDistort fullView = LensDistortionOps.imageRemoveDistortion(AdjustmentType.FULL_VIEW, BorderType.VALUE, param, null,
				ImageType.pl(numBands, GrayF32.class));

		// NOTE: After lens distortion has been removed the intrinsic parameters is changed.  If you pass
		//       in  a set of IntrinsicParameters to the 4th variable it will save it there.
		// NOTE: Type information was stripped from ImageDistort simply because it becomes too verbose with it here.
		//       Would be nice if this verbosity issue was addressed by the Java language.

		// render and display the different types of views in a window
		displayResults(orig, distortedImg, allInside, fullView );
	}

	/**
	 * Displays results in a window for easy comparison..
	 */
	private static void displayResults(BufferedImage orig,
									   Planar<GrayF32> distortedImg,
									   ImageDistort allInside, ImageDistort fullView ) {
		// render the results
		Planar<GrayF32> undistortedImg = new Planar<GrayF32>(GrayF32.class,
				distortedImg.getWidth(),distortedImg.getHeight(),distortedImg.getNumBands());

		allInside.apply(distortedImg, undistortedImg);
		BufferedImage out1 = ConvertBufferedImage.convertTo(undistortedImg, null,true);

		fullView.apply(distortedImg,undistortedImg);
		BufferedImage out2 = ConvertBufferedImage.convertTo(undistortedImg, null,true);

		// display in a single window where the user can easily switch between images
		ListDisplayPanel panel = new ListDisplayPanel();
		panel.addItem(new ImagePanel(orig), "Original");
		panel.addItem(new ImagePanel(out1), "Undistorted All Inside");
		panel.addItem(new ImagePanel(out2), "Undistorted Full View");

		ShowImages.showWindow(panel, "Removing Lens Distortion", true);
	}
}
