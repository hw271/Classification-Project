This project implements 8 classification approaches including:
1.Naive Bayes (NaiveBayes.java)
2.kNN (IBk.java)
3.edited kNN (EditedkNN.java)
4.decision tree (DecisionTree.java)
5.Hoeffding tree (HoeffdingTree.java)
6.Perceptron (Perceptron.java)
7.Kernal Perceptron: polynomial kernal and Gaussan kernal (KernalPerceptron.java)
8.Support Vector Machine (KernalSVM.java)

All models implements the interface Classifier, which is writter in Classifier.java
The interface contains three methods:
	void train(DataSet trainSet);
	int classify(Example example1);
	Performance classify(DataSet test);

All datasets in this project are downloaded from UCI: https://archive.ics.uci.edu/ml/datasets.html
We apply our approaches on 4 datasets: bike, breast-cancer-wisconsin, mushroom, pima-indians-diabetes




when running the program, in the following situations, there will be errors.
1.if the (# of examples)<kNN, then the output will be NaN(not a number)
2.silimar error happens when (# of examples) < kfoldscross.

please pay attention that when you do not enter -x or -k, default value for the parameters are 10 and 3. make sure # of examples>10 or 3 in such situations.
