package ch.chnoch.thesis.renderer.util;

import java.util.List;

public class Util {

	
	/**
	 * Creates a primitive type float array from a List of reference Float type values
	 * @param list The List with the Float values
	 * @return a float array
	 */
	public static float[] listToArray(List<Float> list) {
		float[] floatArray = new float[list.size()];
		for (int i = 0; i < list.size(); i++) {
			floatArray[i] = list.get(i);
		}
		
		return floatArray;
	}
}
