package it.polito.languagespotter;

import it.polito.languagespotter.tokenizer.WekaLexicalProgrammingLanguageTokenizer;
import it.polito.languagespotter.tokenizer.WekaStructuralProgrammingLanguageTokenizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.HMM;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ArffSaver;
import weka.core.converters.TextDirectoryLoader;
import weka.core.tokenizers.Tokenizer;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Reorder;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class Learner {
	
	private String in;
	private String out;
	private Tokenizer tokenizer = new WekaStructuralProgrammingLanguageTokenizer();
	private Classifier classifier = new NaiveBayes();
	private Instances trainData;
	
	private static final int MIN_NUM_ATTRIBUTES = 2;
			
	/**
	 * This method loads a dataset in ARFF format. 
	 */
	public void loadDataset(String name) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(name));
			ArffReader arff = new ArffReader(reader);
			trainData = arff.getData();
            if (trainData.numAttributes()!=MIN_NUM_ATTRIBUTES){
                throw new IllegalStateException("Train data expected to have at least "+MIN_NUM_ATTRIBUTES+" attributes, while currently it has "+trainData.numAttributes());
            }
			System.out.println("===== Loaded dataset: " + name + " =====");
			reader.close();
		}
		catch (Exception e) {
			System.out.println("Problem found when reading: " + name);
            throw new RuntimeException(e);
		}
	}
	
	/**
	 * This method loads a data set from a directory. 
	 */
	public void loadDatasetFromDirectory(String name) {
		try {
			TextDirectoryLoader loader = new TextDirectoryLoader();
		    loader.setDirectory(new File(name));
			trainData = loader.getDataSet();
            if (trainData.numAttributes()!=MIN_NUM_ATTRIBUTES){
                throw new IllegalStateException("Train data expected to have "+MIN_NUM_ATTRIBUTES+" attribute, it has "+trainData.numAttributes());
            }
   			System.out.println("===== Loaded dataset from folder: " + name + " =====");

		}
		catch (Exception e) {
			System.err.println("Problem found when reading from folder: " + name);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * This method evaluates the classifier. 
	 */
	public void evaluate() {
		try {
			if (null==trainData){
                throw new IllegalStateException("Train data not initialized");
            }
			Evaluation eval = new Evaluation(trainData);
			eval.crossValidateModel(classifier, trainData, 10, new Random(1));
			System.out.println(eval.toSummaryString());
			System.out.println(eval.toClassDetailsString());
			System.out.println("===== Evaluating on filtered (training) dataset done =====");
		}
		catch (Exception e) {
			System.err.println("Problem found while evaluating: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * This method trains the classifier on the loaded data set.
	 */
	public void learn() {
		try {
			if (MIN_NUM_ATTRIBUTES!=trainData.numAttributes()){
                throw new IllegalStateException("Expected to have at least: "+MIN_NUM_ATTRIBUTES+" attributes: "+trainData.numAttributes()+" found");
            }	
		
			StringToWordVector filter = new StringToWordVector();
			filter.setInputFormat(trainData);
            filter.setTokenizer(tokenizer);
           		
			//structural
//            filter.setOptions(
//					weka.core.Utils.splitOptions("-W 100000 " + 
//				                                 "-tokenizer \"it.polito.clj.languagespotter.WekaStructuralProgrammingLanguageTokenizer\" "));		
            
            trainData = Filter.useFilter(trainData, filter);

	        Reorder r = new Reorder();
	        r.setAttributeIndices("2-last,1");
	        r.setInputFormat(trainData);
	        trainData = Filter.useFilter(trainData, r);
		    //System.out.println(trainData);
	       
	        saveArff(trainData);
	        
	        classifier.buildClassifier(trainData);
		    System.out.println("\n\nClassifier model:\n\n" + classifier);

			System.out.println("===== Training on filtered (training) dataset done =====");
		}
		catch (Exception e) {
			System.out.println("Problem found when training");
			throw new RuntimeException(e);
		}
	}
	
	private void saveArff(Instances dataSet)
	{
        System.out.println("===== Serializing to /tmp/lexical.arff ====");
		ArffSaver saver = new ArffSaver();
		saver.setInstances(dataSet);
	    try {
			saver.setFile(new File(getOut() + ".arff"));
			saver.writeBatch();
			System.out.println("==== Serialization done ====");
	    } catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * This method saves the trained model into a file. This is done by
	 * simple serialization of the classifier object.
	 * @param fileName The name of the file that will store the trained model.
	 */
	public void saveModel(String fileName) {
		try {
			weka.core.SerializationHelper.write(fileName, classifier);
 			System.out.println("===== Saved model: " + fileName + " =====");
        } 
		catch (Exception e) {
			System.out.println("Problem found when writing: " + fileName);
		}
	}
	
	public static void main (String[] args) 
	{
		Learner learner = new Learner();
		learner.setIn("profiles/gist10/");
		learner.setOut("models/gist10-lexical.out");
		
		learner.loadDatasetFromDirectory(learner.getIn());
		learner.learn();
		learner.saveModel(learner.getOut());
		learner.evaluate();
	}
	
	
	/*
	 * CLI
	 */
	public void run(String[] args) 
	{
	   // define list of options
       Options options = initOptions();

       // read options
       Boolean option = readOptions(options, args);
       
       if (option) {
    	   loadDatasetFromDirectory(getIn());
   		   learn();
   		   saveModel(getOut());
   		   /*this is optional, but it gives an idea about the performance of your data*/
   		   evaluate();
       }
	}

	private Boolean readOptions(Options options, String[] args)
	{
		CommandLineParser parser = new PosixParser();
		CommandLine line;
		
		try {
			line = parser.parse( options, args );

		    if (line.hasOption("in") && line.hasOption("out") 
		    		&& line.hasOption("classifier") && line.hasOption("tokenizer")) 
		    {
		    	setIn( line.getOptionValue("in") );
		        setOut( line.getOptionValue("out") );
		        String tokenizer = line.getOptionValue("tokenizer");
		        
		        switch (tokenizer) {
				case "word":
					setTokenizer( new WordTokenizer() );
					break;
				case "lexical":
					setTokenizer( new WekaLexicalProgrammingLanguageTokenizer() );
					break;
				case "structural":
					setTokenizer( new WekaStructuralProgrammingLanguageTokenizer() );
					break;
				default:
					break;
				}
		        
		        String classifierName = line.getOptionValue("classifier");
		        switch (classifierName) {
				case "naivebayes":
					setClassifier( new NaiveBayes() );
					break;
				case "hmm":
					HMM hmm = new HMM();
					hmm.setNumStates(3);
					setClassifier( hmm );
					break;
				case "svm":
					setClassifier( new SMO() );
					break;
				default:
					break;
				}
		        
		        return true;
			}
		    else
            {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "languagespotter.jar learn --in foldername --out outputmodel --tokenizer tokenizername --classifier classifiername", options );               
            }

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static Options initOptions() 
	{
		 // create the Options
        Options options = new Options();
        options.addOption( "I", "in", true, "folder name" );
        options.addOption( "O", "out", true, "name of the model");
        options.addOption( "T", "tokenizer", true, "name of the tokenizer");
        options.addOption( "C", "classifier", true, "classifier: naivebayes, hmm, svm");
        options.addOption( "H", "help", false, "print the help" );
        
        return options;
	}

	/*
	 * Gets and Setters
	 */
	public String getIn() {
		return in;
	}

	public void setIn(String in) {
		this.in = in;
	}

	public String getOut() {
		return out;
	}

	public void setOut(String out) {
		this.out = out;
	}

    public Classifier getClassifier() {
		return classifier;
	}

	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}

	public Tokenizer getTokenizer() {
		return tokenizer;
	}

	public void setTokenizer(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

}	