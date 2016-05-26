public interface Classifier {
	void train(DataSet trainSet);
	int classify(Example example1);
	Performance classify(DataSet test);
}
