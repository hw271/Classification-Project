This project implements 8 **_classification_** approaches including:

1.  Naive Bayes (NaiveBayes.java)
2.  kNN (IBk.java)
3.  edited kNN (EditedkNN.java)
4.  decision tree (DecisionTree.java)
5.  Hoeffding tree (HoeffdingTree.java)
6.  Perceptron (Perceptron.java)
7.  Kernal Perceptron: polynomial kernal and Gaussan kernal (KernalPerceptron.java)
8.  Support Vector Machine (KernalSVM.java)

All models implements the interface Classifier, which is writter in Classifier.java
The interface contains three methods:

1. void train(DataSet trainSet);
2. int classify(Example example1);
3. Performance classify(DataSet test);

All datasets in this project are downloaded from [UCI](https://archive.ics.uci.edu/ml/datasets.html).
We apply our approaches on 4 datasets: 

1. bike
2. breast-cancer-wisconsin
3. mushroom
4. pima-indians-diabetes

When running kNN model, the user might want to set the parameter k. 
For all models, the user might want to set k-folds-cross parameter x.
(For example, 
>java IBk -k 3 -x 10 breast-cancer-wisconsin

runs the kNN model using dataset breast-cancer-wisconsin. It calculates 3 nearest neighbor to conduct analysis and evaluate the model's performance by 10 folds cross validation.) 
Please pay attention that when you do not enter -x or -k, default value for the parameters are 10 and 3. 

In the following situations, the program will throw exceptions:

1.  If the (# of examples)<kNN, then the output will be NaN(not a number)
2.  Silimar errors happen when (# of examples) < x (x is the k-folds-cross parameter).


