package it.polito.clj.languagespotter;
 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class LanguageIdentifier {

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
			System.out.println("Problem found when reading: " + fileName);
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
	
	public static void main (String[] args) {
	
		LanguageIdentifier classifier = new LanguageIdentifier();
		classifier.load("test/test.java");
		classifier.loadModel("profiles.model");
		classifier.makeInstance();
		classifier.classify();		
	}
}	