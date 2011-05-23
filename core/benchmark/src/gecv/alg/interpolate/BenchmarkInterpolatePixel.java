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

package gecv.alg.interpolate;

import gecv.PerformerBase;
import gecv.ProfileOperation;
import gecv.alg.misc.ImageTestingOps;
import gecv.alg.interpolate.impl.BilinearPixel_F32;
import gecv.alg.interpolate.impl.NearestNeighborPixel_F32;
import gecv.struct.image.ImageFloat32;
import gecv.struct.image.ImageUInt8;

import java.util.Random;

/**
 * Benchmark for interpolating on a per-pixel basis
 *
 * @author Peter Abeles
 */
public class BenchmarkInterpolatePixel {
	static int imgWidth = 640;
	static int imgHeight = 480;
	static long TEST_TIME = 1000;

	static ImageFloat32 imgFloat32;
	static ImageUInt8 imgInt8;

	// defines the region its interpolation
	static float start = 10.1f;
	static float end = 310.1f;
	static float step = 1f;

	public static class Bilinear_Safe_F32 extends PerformerBase {
		BilinearPixel_F32 alg = new BilinearPixel_F32(imgFloat32);

		@Override
		public void process() {
			for (float x = start; x <= end; x += step)
				for (float y = start; y <= end; y += step)
					alg.get(x, y);
		}
	}

	public static class Bilinear_UnSafe_F32 extends PerformerBase {
		BilinearPixel_F32 alg = new BilinearPixel_F32(imgFloat32);

		@Override
		public void process() {
			for (float x = start; x <= end; x += step)
				for (float y = start; y <= end; y += step)
					alg.get_unsafe(x, y);
		}
	}

	public static class NearestNeighbor_Safe_F32 extends PerformerBase {
		NearestNeighborPixel_F32 alg = new NearestNeighborPixel_F32(imgFloat32);

		@Override
		public void process() {
			for (float x = start; x <= end; x += step)
				for (float y = start; y <= end; y += step)
					alg.get(x, y);
		}
	}

	public static void main(String args[]) {
		imgInt8 = new ImageUInt8(imgWidth, imgHeight);
		imgFloat32 = new ImageFloat32(imgWidth, imgHeight);

		Random rand = new Random(234);
		ImageTestingOps.randomize(imgInt8, rand, 0, 100);
		ImageTestingOps.randomize(imgFloat32, rand, 0, 200);

		System.out.println("=========  Profile Image Size " + imgWidth + " x " + imgHeight + " ==========");
		System.out.println();

		ProfileOperation.printOpsPerSec(new Bilinear_Safe_F32(), TEST_TIME);
		ProfileOperation.printOpsPerSec(new Bilinear_UnSafe_F32(), TEST_TIME);
		ProfileOperation.printOpsPerSec(new NearestNeighbor_Safe_F32(), TEST_TIME);
	}
}
