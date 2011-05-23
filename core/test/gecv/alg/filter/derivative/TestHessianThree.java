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

import gecv.alg.misc.ImageTestingOps;
import gecv.struct.image.ImageFloat32;
import gecv.struct.image.ImageSInt16;
import gecv.struct.image.ImageUInt8;
import org.junit.Test;

import java.util.Random;

/**
 * @author Peter Abeles
 */
public class TestHessianThree {

	Random rand = new Random(234);
	int width = 20;
	int height = 25;

//	@Test
//	public void checkInputShape() {
//		GenericDerivativeTests.checkImageDimensionValidation(new HessianThree(), 2);
//	}

	@Test
	public void compareToConvolve_I8() throws NoSuchMethodException {
		CompareDerivativeToConvolution validator = new CompareDerivativeToConvolution();
		validator.setTarget(HessianThree.class.getMethod("process",
				ImageUInt8.class, ImageSInt16.class, ImageSInt16.class, ImageSInt16.class, boolean.class ));

		validator.setKernel(0,HessianThree.kernelXXYY_I32,true);
		validator.setKernel(1,HessianThree.kernelXXYY_I32,false);
		validator.setKernel(2,HessianThree.kernelCross_I32);

		ImageUInt8 input = new ImageUInt8(width,height);
		ImageTestingOps.randomize(input, rand, 0, 10);
		ImageSInt16 derivXX = new ImageSInt16(width,height);
		ImageSInt16 derivYY = new ImageSInt16(width,height);
		ImageSInt16 derivXY = new ImageSInt16(width,height);

		validator.compare(input,derivXX,derivYY,derivXY);
	}

	@Test
	public void compareToConvolve_F32() throws NoSuchMethodException {
		CompareDerivativeToConvolution validator = new CompareDerivativeToConvolution();
		validator.setTarget(HessianThree.class.getMethod("process",
				ImageFloat32.class, ImageFloat32.class, ImageFloat32.class, ImageFloat32.class, boolean.class));

		validator.setKernel(0,HessianThree.kernelXXYY_F32,true);
		validator.setKernel(1,HessianThree.kernelXXYY_F32,false);
		validator.setKernel(2,HessianThree.kernelCross_F32);

		ImageFloat32 input = new ImageFloat32(width,height);
		ImageTestingOps.randomize(input, rand, 0, 10);
		ImageFloat32 derivXX = new ImageFloat32(width,height);
		ImageFloat32 derivYY = new ImageFloat32(width,height);
		ImageFloat32 derivXY = new ImageFloat32(width,height);

		validator.compare(input,derivXX,derivYY,derivXY);
	}
}
