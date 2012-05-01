/*
 * Copyright (c) 2011-2012, Peter Abeles. All Rights Reserved.
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

package boofcv.alg.feature.disparity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Peter Abeles
 */
public class TestSelectSparseBasicWta_S32 {

	@Test
	public void simple() {
		int maxDisparity = 30;

		int scores[] = new int[50];
		for( int i = 0; i < maxDisparity; i++) {
			scores[i] = Math.abs(i-5)+2;
		}

		SelectSparseBasicWta_S32 alg = new SelectSparseBasicWta_S32();

		assertTrue(alg.select(scores,maxDisparity));

		assertEquals(5,(int)alg.getDisparity());
	}
}