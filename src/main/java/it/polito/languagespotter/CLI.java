package it.polito.languagespotter;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;


public class CLI {
	
	public static void main(String[] args) 
	{
		CommandLineParser parser=new PosixParser(); //create the command line parser
		
		try {
			CommandLine line = parser.parse(new Options(), args, true);
			String action= line.getArgs()[0];
			args=Arrays.copyOfRange(args, 1, args.length); //delete the argument of the action
			switch (action) {
			case "learn":
				Learner learner = new Learner();
				learner.run(args);
				break;
			case "classify":
				Identifier identifier = new Identifier();
				//identifier.run(args);
				break;
			
			default:
				throw new IllegalArgumentException("Invalid operation: " + action + ". Allowed operation: learn, classify");
			}
			
		} catch (ParseException e) {
			System.out.println("Unexpected exception: " + e.getMessage());
		}
	}
}
