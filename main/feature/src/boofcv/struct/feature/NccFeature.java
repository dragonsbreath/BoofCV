/*
 * Copyright (c) 2011, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://www.boofcv.org).
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

package boofcv.struct.feature;

/**
 * Description for normalized cross correlation (NCC)
 *
 * @author Peter Abeles
 */
public class NccFeature extends TupleDesc_F64 {

	// mean pixel intensity
	public double mean;
	// variance deviation
	public double variance;

	public NccFeature(int numFeatures) {
		super(numFeatures);
	}

	public void setTo(NccFeature src) {
		this.mean = src.mean;
		this.variance = src.variance;
		System.arraycopy(src.value,0,value,0,value.length);
	}
}