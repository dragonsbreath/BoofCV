/*
 * Copyright 2011 Peter Abeles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gecv.alg.filter.derivative;

import gecv.alg.InputSanityCheck;
import gecv.alg.filter.convolve.border.ConvolveJustBorder_General;
import gecv.alg.filter.derivative.impl.HessianThree_Standard;
import gecv.core.image.border.ImageBorderExtended;
import gecv.struct.convolve.Kernel1D_F32;
import gecv.struct.convolve.Kernel1D_I32;
import gecv.struct.convolve.Kernel2D_F32;
import gecv.struct.convolve.Kernel2D_I32;
import gecv.struct.image.ImageFloat32;
import gecv.struct.image.ImageSInt16;
import gecv.struct.image.ImageUInt8;

/**
 * <p>
 * Computes the second derivative (Hessian) of an image using.  This hessian is derived by using the same gradient
 * function used in {@link GradientThree}, which uses a kernel of [-1 0 1].
 * </p>
 *
 * <p>
 * 
 * Kernel for &part; <sup>2</sup>f/&part; x<sup>2</sup> and &part;<sup>2</sup>f /&part; y<sup>2</sup> is
 * [1 0 -2 0 1] and &part;<sup>2</sup>f/&part; x&part;y is:<br>
 * <table border="1">
 * <tr> <td> 1 </td> <td> 0 </td> <td> -1 </td> </tr>
 * <tr> <td> 0 </td> <td> 0 </td> <td> 0 </td> </tr>
 * <tr> <td> -1 </td> <td> 0 </td> <td> 1 </td> </tr>
 * </table}
 * </p>
 *
 * @author Peter Abeles
 */
public class HessianThree {

	public static Kernel1D_I32 kernelXXYY_I32 = new Kernel1D_I32(5,1,0,-2,0,1);
	public static Kernel2D_I32 kernelCross_I32 = new Kernel2D_I32(new int[]{1,0,-1,0,0,0,-1,0,1},3);

	public static Kernel1D_F32 kernelXXYY_F32 = new Kernel1D_F32(5,0.5f,0,-1,0,0.5f);
	public static Kernel2D_F32 kernelCross_F32 = new Kernel2D_F32(new float[]{0.5f,0,-0.5f,0,0,0,-0.5f,0,0.5f},3);

	/**
	 * <p>
	 * Computes the second derivative of an {@link gecv.struct.image.ImageUInt8} along the x and y axes.
	 * </p>
	 *
	 * @param orig   Which which is to be differentiated. Not Modified.
	 * @param derivXX Second derivative along the x-axis. Modified.
	 * @param derivYY Second derivative along the y-axis. Modified.
	 * @param derivXY Second cross derivative. Modified.
	 * @param processBorder If the image's border is processed or not.
	 */
	public static void process( ImageUInt8 orig,
								ImageSInt16 derivXX, ImageSInt16 derivYY, ImageSInt16 derivXY ,
								boolean processBorder ) {
		InputSanityCheck.checkSameShape(orig, derivXX, derivYY, derivXY);
		HessianThree_Standard.process(orig, derivXX, derivYY,derivXY);

		if( processBorder ) {
			DerivativeHelperFunctions.processBorderHorizontal(orig, derivXX ,kernelXXYY_I32, 2 );
			DerivativeHelperFunctions.processBorderVertical(orig, derivYY ,kernelXXYY_I32, 2 );
			ConvolveJustBorder_General.convolve(kernelCross_I32, ImageBorderExtended.wrap(orig),derivXY,2);
		}
	}

	/**
	 * Computes the second derivative of an {@link gecv.struct.image.ImageUInt8} along the x and y axes.
	 *
	 * @param orig   Which which is to be differentiated. Not Modified.
	 * @param derivXX Second derivative along the x-axis. Modified.
	 * @param derivYY Second derivative along the y-axis. Modified.
	 * @param derivXY Second cross derivative. Modified.
	 * @param processBorder If the image's border is processed or not.
	 */
	public static void process( ImageFloat32 orig,
								ImageFloat32 derivXX, ImageFloat32 derivYY, ImageFloat32 derivXY,
								boolean processBorder ) {
		InputSanityCheck.checkSameShape(orig, derivXX, derivYY, derivXY);
		HessianThree_Standard.process(orig, derivXX, derivYY, derivXY);

		if( processBorder ) {
			DerivativeHelperFunctions.processBorderHorizontal(orig, derivXX ,kernelXXYY_F32, 2 );
			DerivativeHelperFunctions.processBorderVertical(orig, derivYY ,kernelXXYY_F32, 2 );
			ConvolveJustBorder_General.convolve(kernelCross_F32, ImageBorderExtended.wrap(orig),derivXY,2);
		}
	}
}