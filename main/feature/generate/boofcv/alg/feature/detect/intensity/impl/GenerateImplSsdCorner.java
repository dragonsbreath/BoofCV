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

package boofcv.alg.feature.detect.intensity.impl;

import boofcv.misc.AutoTypeImage;
import boofcv.misc.CodeGeneratorBase;

import java.io.FileNotFoundException;

/**
 * @author Peter Abeles
 */
public class GenerateImplSsdCorner extends CodeGeneratorBase  {
	String className;
	String typeInput;
	String typeOutput;
	String dataInput;
	String dataOutput;
	String sumType;

	@Override
	public void generate() throws FileNotFoundException {
		createFile(AutoTypeImage.F32,AutoTypeImage.F32);
		createFile(AutoTypeImage.S16,AutoTypeImage.S32);
	}

	public void createFile( AutoTypeImage input , AutoTypeImage output ) throws FileNotFoundException {
		className = "ImplSsdCorner_"+input.getAbbreviatedType();

		typeInput = input.getSingleBandName();
		typeOutput = output.getSingleBandName();
		dataInput = input.getDataType();
		dataOutput = output.getDataType();
		sumType = input.getSumType();

		printPreamble();
		printHorizontal();
		printVertical();

		out.println("}");
	}

	private void printPreamble() throws FileNotFoundException {
		setOutputFile(className);
		out.print("import boofcv.struct.image." + typeInput + ";\n");
		if (typeInput.compareTo(typeOutput) != 0)
			out.print("import boofcv.struct.image." + typeOutput + ";\n");
		if( typeInput.compareTo("GrayF32") != 0 && typeOutput.compareTo("GrayF32") != 0 ) {
			out.print("import boofcv.struct.image.GrayF32;\n");
		}
		out.print("import javax.annotation.Generated;\n\n");

		out.print("/**\n" +
				" * <p>\n" +
				" * Implementation of {@link ImplSsdCornerBase} for {@link "+typeInput+"}.\n" +
				" * </p>\n" +
				" * \n" +
				" * <p>\n" +
				" * DO NOT MODIFY.  Code has been automatically generated by "+getClass().getSimpleName()+".\n" +
				" * </p>\n" +
				" *\n" +
				" * @author Peter Abeles\n" +
				" */\n" +
				"@Generated(\""+getClass().getCanonicalName()+"\")\n" +
				"public abstract class "+className+" extends ImplSsdCornerBase<"+typeInput+","+typeOutput+"> {\n" +
				"\n" +
				"\t// temporary storage for convolution along in the vertical axis.\n" +
				"\tprivate "+dataOutput+" tempXX[] = new "+dataOutput+"[1];\n" +
				"\tprivate "+dataOutput+" tempXY[] = new "+dataOutput+"[1];\n" +
				"\tprivate "+dataOutput+" tempYY[] = new "+dataOutput+"[1];\n" +
				"\n" +
				"\t// defines the A matrix, from which the eigenvalues are computed\n" +
				"\tprotected "+sumType+" totalXX, totalYY, totalXY;\n" +
				"\n" +
				"\tpublic "+className+"( int windowRadius) {\n" +
				"\t\tsuper(windowRadius,"+typeOutput+".class);\n" +
				"\t}\n" +
				"\n" +
				"\t@Override\n" +
				"\tpublic void setImageShape( int imageWidth, int imageHeight ) {\n" +
				"\t\tsuper.setImageShape(imageWidth,imageHeight);\n" +
				"\n" +
				"\t\tif( tempXX.length < imageWidth ) {\n" +
				"\t\t\ttempXX = new "+dataOutput+"[imageWidth];\n" +
				"\t\t\ttempXY = new "+dataOutput+"[imageWidth];\n" +
				"\t\t\ttempYY = new "+dataOutput+"[imageWidth];\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	protected void printHorizontal() {
		out.print("/**\n" +
				"\t * Compute the derivative sum along the x-axis while taking advantage of duplicate\n" +
				"\t * calculations for each window.\n" +
				"\t */\n" +
				"\t@Override\n" +
				"\tprotected void horizontal() {\n" +
				"\t\t" + dataInput + "[] dataX = derivX.data;\n" +
				"\t\t" + dataInput + "[] dataY = derivY.data;\n" +
				"\n" +
				"\t\t" + dataOutput + "[] hXX = horizXX.data;\n" +
				"\t\t" + dataOutput + "[] hXY = horizXY.data;\n" +
				"\t\t" + dataOutput + "[] hYY = horizYY.data;\n" +
				"\n" +
				"\t\tfinal int imgHeight = derivX.getHeight();\n" +
				"\t\tfinal int imgWidth = derivX.getWidth();\n" +
				"\n" +
				"\t\tint windowWidth = radius * 2 + 1;\n" +
				"\n" +
				"\t\tint radp1 = radius + 1;\n" +
				"\n" +
				"\t\tfor (int row = 0; row < imgHeight; row++) {\n" +
				"\n" +
				"\t\t\tint pix = row * imgWidth;\n" +
				"\t\t\tint end = pix + windowWidth;\n" +
				"\n" +
				"\t\t\t" + sumType + " totalXX = 0;\n" +
				"\t\t\t" + sumType + " totalXY = 0;\n" +
				"\t\t\t" + sumType + " totalYY = 0;\n" +
				"\n" +
				"\t\t\tint indexX = derivX.startIndex + row * derivX.stride;\n" +
				"\t\t\tint indexY = derivY.startIndex + row * derivY.stride;\n" +
				"\n" +
				"\t\t\tfor (; pix < end; pix++) {\n" +
				"\t\t\t\t"+dataInput+" dx = dataX[indexX++];\n" +
				"\t\t\t\t"+dataInput+" dy = dataY[indexY++];\n" +
				"\n" +
				"\t\t\t\ttotalXX += dx * dx;\n" +
				"\t\t\t\ttotalXY += dx * dy;\n" +
				"\t\t\t\ttotalYY += dy * dy;\n" +
				"\t\t\t}\n" +
				"\n" +
				"\t\t\thXX[pix - radp1] = totalXX;\n" +
				"\t\t\thXY[pix - radp1] = totalXY;\n" +
				"\t\t\thYY[pix - radp1] = totalYY;\n" +
				"\n" +
				"\t\t\tend = row * imgWidth + imgWidth;\n" +
				"\t\t\tfor (; pix < end; pix++, indexX++, indexY++) {\n" +
				"\n" +
				"\t\t\t\t"+dataInput+" dx = dataX[indexX - windowWidth];\n" +
				"\t\t\t\t"+dataInput+" dy = dataY[indexY - windowWidth];\n" +
				"\n" +
				"\t\t\t\t// saving these multiplications in an array to avoid recalculating them made\n" +
				"\t\t\t\t// the algorithm about 50% slower\n" +
				"\t\t\t\ttotalXX -= dx * dx;\n" +
				"\t\t\t\ttotalXY -= dx * dy;\n" +
				"\t\t\t\ttotalYY -= dy * dy;\n" +
				"\n" +
				"\t\t\t\tdx = dataX[indexX];\n" +
				"\t\t\t\tdy = dataY[indexY];\n" +
				"\n" +
				"\t\t\t\ttotalXX += dx * dx;\n" +
				"\t\t\t\ttotalXY += dx * dy;\n" +
				"\t\t\t\ttotalYY += dy * dy;\n" +
				"\n" +
				"\t\t\t\thXX[pix - radius] = totalXX;\n" +
				"\t\t\t\thXY[pix - radius] = totalXY;\n" +
				"\t\t\t\thYY[pix - radius] = totalYY;\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	public void printVertical() {
		out.print("\t/**\n" +
				"\t * Compute the derivative sum along the y-axis while taking advantage of duplicate\n" +
				"\t * calculations for each window and avoiding cache misses. Then compute the eigen values\n" +
				"\t */\n" +
				"\t@Override\n" +
				"\tprotected void vertical( GrayF32 intensity ) {\n" +
				"\t\t"+sumType+"[] hXX = horizXX.data;\n" +
				"\t\t"+sumType+"[] hXY = horizXY.data;\n" +
				"\t\t"+sumType+"[] hYY = horizYY.data;\n" +
				"\t\tfinal float[] inten = intensity.data;\n" +
				"\n" +
				"\t\tfinal int imgHeight = horizXX.getHeight();\n" +
				"\t\tfinal int imgWidth = horizXX.getWidth();\n" +
				"\n" +
				"\t\tfinal int kernelWidth = radius * 2 + 1;\n" +
				"\n" +
				"\t\tfinal int startX = radius;\n" +
				"\t\tfinal int endX = imgWidth - radius;\n" +
				"\n" +
				"\t\tfinal int backStep = kernelWidth * imgWidth;\n" +
				"\n" +
				"\t\tfor (x = startX; x < endX; x++) {\n" +
				"\t\t\tint srcIndex = x;\n" +
				"\t\t\tint destIndex = imgWidth * radius + x;\n" +
				"\t\t\ttotalXX = totalXY = totalYY = 0;\n" +
				"\n" +
				"\t\t\tint indexEnd = srcIndex + imgWidth * kernelWidth;\n" +
				"\t\t\tfor (; srcIndex < indexEnd; srcIndex += imgWidth) {\n" +
				"\t\t\t\ttotalXX += hXX[srcIndex];\n" +
				"\t\t\t\ttotalXY += hXY[srcIndex];\n" +
				"\t\t\t\ttotalYY += hYY[srcIndex];\n" +
				"\t\t\t}\n" +
				"\n" +
				"\t\t\ttempXX[x] = totalXX;\n" +
				"\t\t\ttempXY[x] = totalXY;\n" +
				"\t\t\ttempYY[x] = totalYY;\n" +
				"\n" +
				"\t\t\ty = radius;\n" +
				"\t\t\t// compute the eigen values\n" +
				"\t\t\tinten[destIndex] = computeIntensity();\n" +
				"\t\t\tdestIndex += imgWidth;\n" +
				"\t\t\ty++;\n" +
				"\t\t}\n" +
				"\n" +
				"\t\t// change the order it is processed in to reduce cache misses\n" +
				"\t\tfor (y = radius + 1; y < imgHeight - radius; y++) {\n" +
				"\t\t\tint srcIndex = (y + radius) * imgWidth + startX;\n" +
				"\t\t\tint destIndex = y * imgWidth + startX;\n" +
				"\n" +
				"\t\t\tfor (x = startX; x < endX; x++, srcIndex++, destIndex++) {\n" +
				"\t\t\t\ttotalXX = tempXX[x] - hXX[srcIndex - backStep];\n" +
				"\t\t\t\ttempXX[x] = totalXX += hXX[srcIndex];\n" +
				"\t\t\t\ttotalXY = tempXY[x] - hXY[srcIndex - backStep];\n" +
				"\t\t\t\ttempXY[x] = totalXY += hXY[srcIndex];\n" +
				"\t\t\t\ttotalYY = tempYY[x] - hYY[srcIndex - backStep];\n" +
				"\t\t\t\ttempYY[x] = totalYY += hYY[srcIndex];\n" +
				"\n" +
				"\t\t\t\tinten[destIndex] = computeIntensity();\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n");
	}

	public static void main( String args[] ) throws FileNotFoundException {
		GenerateImplSsdCorner gen = new GenerateImplSsdCorner();

		gen.generate();
	}
}
