package mikera.indexz;

import java.util.ArrayList;
import java.util.List;

import mikera.util.Rand;

/**
 * Class for static index methods
 * 
 * @author Mike
 *
 */
public class Indexz {

	private Indexz(){}
	
	public static Index create(List<Integer> data) {
		int length=data.size();
		Index result=new Index(length);
		for (int i=0; i<length; i++) {
			result.set(i,data.get(i));
		}		
		return result;
	}
	
	public static Index create(AIndex source) {
		Index result=new Index(source.length());
		source.copyTo(result.getData(), 0);
		return result;
	}
	
	public static Index create(int[] source) {
		return Index.of(source);
	}

	/**
	 * Create an Index containing an arithmetic progression
	 */
	public static Index createProgression(int start, int length, int skip) {
		Index result=new Index(length);
		int pos=start;
		for (int i=0; i<length; i++) {
			result.set(i,pos);
			pos+=skip;
		}
		return result;
	}

	/**
	 * Create an Index containing an arithmetic sequence
	 * (a, a+1, a+2 ..... a+length-1)
	 */
	public static Index createSequence(int start, int length) {
		Index result=new Index(length);
		for (int i=0; i<length; i++) {
			result.set(i,start+i);
		}
		return result;
	}

	/**
	 * Creates a Index containing the sequence 0, 1, ... length-1
	 * @param length
	 * @return
	 */
	public static Index createSequence(int length) {
		return createSequence(0,length);
	}
	
	public static Index createRandomPermutation(int length) {
		Index result=createSequence(length);
		for (int j=length-1; j>0; j--) {
			result.swap(Rand.r(j+1), j);
		}
		return result;
	}
	
	public static Index createRandomChoice(int count, int max) {
		Index result=createSequence(count);
		Rand.chooseIntegers(result.data, 0, count, max);
		assert(result.isDistinct()&&result.isSorted());
		return result;
	}
	
	public static Index createRandomChoice(int count, Index source) {
		Index result=createSequence(count);
		Rand.chooseIntegers(result.data, 0, count, source.length());
		assert(result.isDistinct()&&result.isSorted());
		result.lookupWith(source);
		return result;
	}
	
	/**
	 * Returns a random subset of an Index, including each element with the given probability
	 * @param probability
	 * @return
	 */
	public static Index createRandomSubset(AIndex index, double probability) {
		int len=index.length();
		// estimated capacity required = 3 standard deviations
		int elen=(int)(len*probability+3.0*Math.sqrt(len*probability*(1-probability)));
		ArrayList<Integer> al=new ArrayList<Integer>(elen);
		for (int i=0; i<len; i++) {
			if (Rand.chance(probability)) {
				al.add(index.get(i));
			}
		}
		return Indexz.create(al);
	}
	
	public static Index createRandomSubset(int length,double probability) {
		return createRandomSubset(Indexz.createSequence(length),probability);
	}
	
	/**
	 * Creates a random selection of given length from the numbers 0..maxValue-1
	 * 
	 * Results are not sorted. May contain duplicates
	 * 
	 * @param length
	 * @param probability
	 * @return
	 */
	public static Index createRandomSelection(int length, int maxValue) {
		int[] sel=new int[length];
		for (int i=0; i<length; i++) {
			sel[i]=Rand.r(maxValue);
		}
		return new Index(length);
	}

	public static Index createLength(int length) {
		return new Index(length);
	}



}
