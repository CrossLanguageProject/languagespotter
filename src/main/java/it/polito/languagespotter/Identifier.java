package it.polito.languagespotter;
 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.TextDirectoryLoader;

public class Identifier {

	/**
	 * String that stores the text to guess its language.
	 */
	String text;
	/**
	 * Object that stores the instance.
	 */
	Instances instances;
	/**
	 * Object that stores the classifier.
	 */
	Classifier classifier;
	
	private static final int MIN_NUM_ATTRIBUTES = 2;
	private Instances testData;

		
	/**
	 * This method loads the text to be classified.
	 * @param fileName The name of the file that stores the text.
	 */
	public void load(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;
			text = "";
			while ((line = reader.readLine()) != null) {
                text = text + " " + line;
            }
			System.out.println("===== Loaded text data: " + fileName + " =====");
			reader.close();
			System.out.println(text);
		}
		catch (IOException e) {
			System.err.println("Problem found when reading: " + fileName);
            throw new RuntimeException(e);
		}
	}
			
	/**
	 * This method loads the model to be used as classifier.
	 * @param fileName The name of the file that stores the text.
	 */
	public void loadModel(String fileName) {
		try {
			classifier = (Classifier) weka.core.SerializationHelper.read(fileName);
 			System.out.println("===== Loaded model: " + fileName + " =====");
       } 
		catch (Exception e) {
			// Given the cast, a ClassNotFoundException must be caught along with the IOException
			System.out.println("Problem found when reading: " + fileName);
		}
	}
	
	/**
	 * This method creates the instance to be classified, from the text that has been read.
	 */
	public void makeInstance() {
	
		// Create the header
		List<Attribute> attributeList = new ArrayList<>(2);
		
		// Create first attribute, the class
		List<String> values = new ArrayList<>(); 
		//TODO list all the folder labels that are used as keys
		values.add("java");
		values.add("py");
		
		Attribute attribute1 = new Attribute("class", values);
		attributeList.add(attribute1);
		
		// Create second attribute, the text
		Attribute attribute2 = new Attribute("text",(List<String>) null);
		attributeList.add(attribute2);
		
		// Build instance set with just one instance
		instances = new Instances("Test relation", (java.util.ArrayList<Attribute>) attributeList, 1);           
		// Set class index
		instances.setClassIndex(0);

		// Create and add the instance
		DenseInstance instance = new DenseInstance(2);
		instance.setDataset(instances);
		instance.setValue(attribute2, text);
		instances.add(instance);
		
 		System.out.println("===== Instance created with reference dataset =====");
		System.out.println(instances);
	}
	
	/**
	 * This method performs the classification of the instance.
	 * Output is done at the command-line.
	 */
	public void classify() {
		try {
			double pred = classifier.classifyInstance(instances.firstInstance());
			System.out.println("===== Classified instance =====");
			System.out.println("Class predicted: " + instances.classAttribute().value((int) pred));
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void loadDatasetFromDirectory(String name) {
		try {
			TextDirectoryLoader loader = new TextDirectoryLoader();
		    loader.setDirectory(new File(name));
			testData = loader.getDataSet();
            if (testData.numAttributes()!=MIN_NUM_ATTRIBUTES){
                throw new IllegalStateException("Train data expected to have "+MIN_NUM_ATTRIBUTES+" attribute, it has "+testData.numAttributes());
            }
   			System.out.println("===== Loaded dataset from folder: " + name + " =====");

		}
		catch (Exception e) {
			System.err.println("Problem found when reading from folder: " + name);
			throw new RuntimeException(e);
		}
	}
	
	public void evaluate() {

		try {
			Evaluation eval = new Evaluation(testData);
		    eval.evaluateModel(classifier, testData);
		    String summaryString = eval
		            .toSummaryString("\nResults\n======\n", false);

		    System.out.println(summaryString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    //eval.crossValidateModel(classifier, data, 10, new Random(1));

	}
	
	public static void main (String[] args) {
	
		Identifier classifier = new Identifier();
		
		classifier.loadModel("models/github10-hmm-lexical.model");
		classifier.loadDatasetFromDirectory("profiles/gist10");
//		
//		File dir = new File("profiles/gist10");  	
//		File[] subDirs = dir.listFiles(new FileFilter() {
//			public boolean accept(File pathname) {
//				return pathname.isDirectory();
//			}
//		});  
//		   
//		for (File subDir : subDirs) {  
//		    System.out.println(subDir.getName());  
//		}  
//		
		
		
//		classifier.load("profiles/gist10");

//		classifier.makeInstance();
		classifier.evaluate();		
	}
}	