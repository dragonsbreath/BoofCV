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

package gecv.abst.detect.corner;

import gecv.abst.filter.blur.impl.MedianImageFilter;
import gecv.alg.detect.corner.MedianCornerIntensity;
import gecv.struct.QueueCorner;
import gecv.struct.image.ImageBase;
import gecv.struct.image.ImageFloat32;

/**
 * Wrapper around children of {@link gecv.alg.detect.corner.MedianCornerIntensity}.  This is a bit of a hack since
 * the median image is not provided as a standard input so it has to compute it internally
 * 
 * @author Peter Abeles
 */
public class WrapperMedianCornerIntensity<I extends ImageBase, D extends ImageBase> implements GeneralCornerIntensity<I,D> {

	MedianCornerIntensity<I> alg;
	MedianImageFilter<I> medianFilter;
	I medianImage;

	public WrapperMedianCornerIntensity(MedianCornerIntensity<I> alg ,
										MedianImageFilter<I> medianFilter ) {
		this.alg = alg;
		this.medianFilter = medianFilter;
	}

	@Override
	public void process(I input, D derivX , D derivY , D derivXX , D derivYY , D derivXY ) {
		if( medianImage == null ) {
			medianImage = (I)input._createNew(input.width,input.height);
		}
		medianFilter.process(input,medianImage);
		alg.process(input,medianImage);
	}

	@Override
	public ImageFloat32 getIntensity() {
		return alg.getIntensity();
	}

	@Override
	public QueueCorner getCandidates() {
		return null;
	}

	@Override
	public boolean getRequiresGradient() {
		return false;
	}

	@Override
	public boolean getRequiresHessian() {
		return false;
	}

	@Override
	public boolean hasCandidates() {
		return false;
	}
}