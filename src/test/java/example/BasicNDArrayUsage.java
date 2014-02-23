package example;

import mikera.arrayz.NDArray;

public class BasicNDArrayUsage {

	public static void main(String[] args) {
		// create a new 3x3x3 array
		NDArray a1=NDArray.newArray(3,3,3); 
		
		// fill a slice on dimension 1 with zeros
		a1.slice(1,1).fill(2.0);
		
		// add 1.0 to the first slice (on dimension 0 by default)
		a1.slice(0).add(1);
		
		System.out.println(a1);
	}

}
