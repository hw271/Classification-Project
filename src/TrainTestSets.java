



public class TrainTestSets implements OptionHandler{
	protected DataSet	test;
	protected DataSet	train;
	
	public TrainTestSets(){
		test = new DataSet();
		train = new DataSet();
	}
	public TrainTestSets(DataSet train, DataSet test){
		this.train=train;
		this.test=test;
		
	}
	public TrainTestSets(java.lang.String[] options) throws Exception{
		this.setOptions(options);
	}
	
	public DataSet	getTestingSet(){
		//Returns the testing set of this train/test set.
		return test;
	}

	public DataSet	getTrainingSet(){
		//Returns the training set of this train/test set.
		return train;
	}

	static void	main(java.lang.String[] args){
		
	} 
	public void setOptions(java.lang.String[] options)
            throws java.lang.Exception{
		//Sets the options for this train/test set.
		String trainFile="";
		String testFile="";
		for(int i=0;i<options.length;i++){
			if(options[i].equals("-t")){
				trainFile=options[++i];
			}
			if(options[i].equals("-T")){
				testFile=options[++i];
			}
		}
		
		if(!trainFile.isEmpty()){
			train=new DataSet(trainFile);
		}
		if(!testFile.isEmpty()){
			test=new DataSet(testFile);
		}	
	}
	
	public void	setTestingSet(DataSet test){
		//Sets the testing set of this train/test set to the specified data set.
		this.test=test;
	}
	
	public void	setTrainingSet(DataSet train){
		//Sets the training set of this train/test set to the specified data set.
		this.train=train;
	}
	
	public String	toString(){
		String st=train.toString();
		if(test!=null){
			st+="\n"+test.toString();
		}
		return st;
	}
}
