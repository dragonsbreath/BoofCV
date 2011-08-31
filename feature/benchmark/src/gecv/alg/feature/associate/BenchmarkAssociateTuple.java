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

package gecv.alg.feature.associate;

import gecv.Performer;
import gecv.ProfileOperation;
import gecv.abst.feature.associate.FactoryAssociationTuple;
import gecv.abst.feature.associate.GeneralAssociation;
import gecv.struct.FastArray;
import gecv.struct.feature.TupleDescArray;
import gecv.struct.feature.TupleDesc_F64;

import java.util.Random;


/**
 * @author Peter Abeles
 */
public class BenchmarkAssociateTuple {

	static final long TEST_TIME = 1000;
	static final Random rand = new Random(234234);
	static final int DOF = 10;
	static final int NUM_FEATURES = 2000;

	static final FastArray<TupleDesc_F64> listA = createSet();
	static final FastArray<TupleDesc_F64> listB = createSet();

	public static class General implements Performer {

		GeneralAssociation<TupleDesc_F64> alg;
		String name;

		public General(String name, GeneralAssociation<TupleDesc_F64> alg) {
			this.alg = alg;
			this.name = name;
		}

		@Override
		public void process() {
			alg.associate(listA,listB);
		}

		@Override
		public String getName() {
			return name;
		}
	}

	private static FastArray<TupleDesc_F64> createSet() {
		FastArray<TupleDesc_F64> ret = new TupleDescArray(DOF);

		for( int i = 0; i < NUM_FEATURES; i++ ) {
			TupleDesc_F64 t = ret.pop();
			for( int j = 0; j < DOF; j++ ) {
				t.value[j] = (rand.nextDouble()-0.5)*20;
			}
		}
		return ret;
	}

	public static void main( String argsp[ ] ) {
		System.out.println("=========  Profile Description Length "+DOF+" ========== Num Features "+NUM_FEATURES);
		System.out.println();

		ScoreAssociateTuple score = new ScoreAssociateEuclideanSq();
		int maxMatches = 200;

		ProfileOperation.printOpsPerSec(new General("Max Error", FactoryAssociationTuple.maxError(score,0.1)),TEST_TIME);
		ProfileOperation.printOpsPerSec(new General("Max Matches", FactoryAssociationTuple.maxMatches(score,maxMatches)),TEST_TIME);
		ProfileOperation.printOpsPerSec(new General("Inlier Error", FactoryAssociationTuple.inlierError(score,maxMatches,10)),TEST_TIME);
		ProfileOperation.printOpsPerSec(new General("Forward Backwards", FactoryAssociationTuple.forwardBackwards(score,maxMatches)),TEST_TIME);
	}
}
