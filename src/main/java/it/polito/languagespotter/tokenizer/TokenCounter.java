package it.polito.languagespotter.tokenizer;

import java.io.File;
import java.io.IOException;
import java.io.*;
import java.util.*;
import weka.core.tokenizers.WordTokenizer;

public class TokenCounter {

    private static String readFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    private static Map<String,List<Integer>> numberOfTokensByTokenizer = new HashMap<String,List<Integer>>();

    private static void examineDir(File dir) throws IOException {
        for (File child : dir.listFiles()){
            if (child.isDirectory()){
                examineDir(child);
            } else {
                examineFile(child);
            }
        }
    }

    private static String quantilesStr(List<Integer> values) {
        Collections.sort(values);
        int pos0   = 0;
        int pos25  = (int)(values.size()*0.25);
        int pos50  = (int)(values.size()*0.5);
        int pos75  = (int)(values.size()*0.75);
        int pos100 = values.size()-1;
        return ""+values.get(pos0)+":"+values.get(pos25)+":"+values.get(pos50)+":"+values.get(pos75)+":"+values.get(pos100);
    }

    private static int numberOfWords(String code){
        WordTokenizer word = new WordTokenizer();
        word.tokenize(code);
        int counter = 0;
        while (word.hasMoreElements()){
            word.nextElement();
            counter++;
        }
        return counter;
    }

    private static void examineFile(File file) throws IOException {
        String code = readFile(file);
        GenericProgrammingLanguageTokenizer t = new GenericProgrammingLanguageTokenizer();
        try {
            WekaLexicalProgrammingLanguageTokenizer lexical = new WekaLexicalProgrammingLanguageTokenizer();
            lexical.tokenize(code);
            List<String> tokens = lexical.getAllTokens();
            if (!numberOfTokensByTokenizer.containsKey("lexical")){
                numberOfTokensByTokenizer.put("lexical",new LinkedList<Integer>());
            }
            numberOfTokensByTokenizer.get("lexical").add(tokens.size());
        } catch (Exception e){
            System.err.println("Something went wrong while parsing file "+file);
        }

        int nbOfWords = numberOfWords(code);

        if (!numberOfTokensByTokenizer.containsKey("word")){
            numberOfTokensByTokenizer.put("word",new LinkedList<Integer>());
        }
        numberOfTokensByTokenizer.get("word").add(nbOfWords);
    }

    public static void main(String[] args) throws IOException {
        if (args.length!=1){
            System.err.println("Usage: <dir to examine>");
        }
        examineDir(new File(args[0]));
        for (String tokenizer : numberOfTokensByTokenizer.keySet()){
            System.out.println(tokenizer+":"+quantilesStr(numberOfTokensByTokenizer.get(tokenizer)));
        }
    }
}
