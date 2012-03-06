package nl.tudelft.in4325.a2.tfidf;

public class SimpleSublinearTFIDFMapper extends SimpleTFIDFMapper {

	@Override
	protected double calculateTFIDF(int frequency, double wordIDF) {
		if (frequency > 0) {
			return (1 + Math.log(frequency)) * wordIDF;
		} else {
			return 0;
		}
	}
}
