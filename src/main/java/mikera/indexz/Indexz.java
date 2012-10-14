package mikera.indexz;

import mikera.util.Rand;

/**
 * Class for static index methods
 * 
 * @author Mike
 *
 */
public class Indexz {

	public static Index createProgression(int start, int length, int skip) {
		Index result=new Index(length);
		int pos=start;
		for (int i=0; i<length; i++) {
			result.set(i,pos);
			pos+=skip;
		}
		return result;
	}

	public static Index createSequence(int start, int length) {
		Index result=new Index(length);
		for (int i=0; i<length; i++) {
			result.set(i,start+i);
		}
		return result;
	}

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
	
	public static Index createRandomChoice(int count, Index source) {
		Index result=createSequence(count);
		Rand.chooseIntegers(result.data, 0, count, source.length());
		assert(result.isDistinct()&&result.isSorted());
		result.lookupWith(source);
		return result;
	}
	

	public static Index createLength(int length) {
		return new Index(length);
	}

}
