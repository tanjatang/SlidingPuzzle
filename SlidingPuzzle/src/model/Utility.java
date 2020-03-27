package model;

public final class Utility {
	/**
	 * Deep copies an Integer array. The resulting array may be modified without affecting the original one.
	 * @param in Array to be copied
	 * @return Deep-copy of the array
	 */	
	//¸´ÖÆÊý×é
	public static Integer[][] deepCopyIntegerArray(Integer[][] in) {
		int x = in.length;
		Integer[][] result = new Integer[x][];
		
		for (int i=0; i< in.length; ++i) {
			result[i] = new Integer[in[i].length];
			for (int j=0; j<in[i].length; ++j) {
				result[i][j]=in[i][j];
			}
		}
		return result;
	}
	
	
	private Utility() {
	}
}
