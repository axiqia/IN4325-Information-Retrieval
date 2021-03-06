package nl.tudelft.in4325.a2.tfidf.top10;

import nl.tudelft.in4325.Constants;
import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.Text;

/**
 * Compares two String tokens in format %i:%s where %i is the query id and %s is
 * the score of a document.
 * 
 */
public class Top10GroupingComparator implements RawComparator<Text> {

	@Override
	public int compare(Text arg0, Text arg1) {
		return compare(arg0.toString(), arg1.toString());
	}

	@Override
	public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
		return compare(new String(b1, s1, l1), new String(b2, s2, l2));
	}

	private int compare(String string, String string2) {
		String queryID1 = string.split(Constants.FIELD_SEPARATOR)[0].trim();
		String queryID2 = string2.split(Constants.FIELD_SEPARATOR)[0].trim();

		if (queryID1.equals(queryID2)) {
			return 0;
		} else {
			return 1;
		}
	}
}
