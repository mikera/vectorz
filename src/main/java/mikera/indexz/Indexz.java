package mikera.indexz;

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

}
