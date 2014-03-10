package it.polito.languagespotter.tokenizer;

import java.io.File;
import java.io.IOException;
import java.io.*;
import java.util.*;

/**
 * Created by federico on 3/5/14.
 */
public class TryingOutTheTokenizer {

    private static void collectFiles(File dir, List<File> files){
        File[] children = dir.listFiles();
        if (null==children){
            System.out.println("No children for "+dir);
        }
        for(File child : children){
            if (child.isDirectory()){
                collectFiles(child,files);
            } else {
                files.add(child);
            }
        }
    }

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

    private static boolean tryToTokenize(File file, Map<String,Integer> counter){
        String code = null;
        try {
            code = readFile(file);
        } catch (IOException e){
            throw new RuntimeException("That is strange...");
        }
        try {
            GenericProgrammingLanguageTokenizer t = new GenericProgrammingLanguageTokenizer();
            List<Token> tokens = t.parse(code);
            for (Token token : tokens){
                if (!counter.containsKey(token.getType())){
                    counter.put(token.getType(),0);
                }
                counter.put(token.getType(),1+counter.get(token.getType()));
            }
            return true;
        } catch (Exception e){
            return false;
        }
    }

    private static class Result {
        int ok = 0;
        int ko = 0;

        @Override
        public String toString() {
            return "Ok "+ok+", Ko "+ko;
        }
    }

    private static Result tryDir(File dir, Map<String,Integer> counter){
        List<File> files = new LinkedList<>();
        collectFiles(dir,files);
        int ok = 0;
        int ko = 0;
        for (File f : files){
            if (f.getName().contains("sample")){
                //System.out.println("Working on "+f);
                boolean res = tryToTokenize(f, counter);
                if (res) ok++; else ko++;
                //System.out.println("  Done? "+ res);
            }
        }
        Result r = new Result();
        r.ok = ok;
        r.ko = ko;
        return r;
    }

    public static void main(String[] args){
        File[] dirs = new File[]{
                new File("/home/federico/temp/gist/YAML"),
                new File("/home/federico/temp/gist/Ruby"),
                new File("/home/federico/temp/gist/Python"),
                new File("/home/federico/temp/gist/Java"),
                new File("/home/federico/temp/gist/CSS"),
                new File("/home/federico/temp/gist/JavaScript"),
                new File("/home/federico/temp/gist/HTML"),
                new File("/home/federico/temp/gist/Shell")
        };
        for (File dir : dirs){
            Map<String,Integer> counter = new HashMap<>();
            Result r = tryDir(dir,counter);
            System.out.println("Dir "+dir+", result: "+r);
            for (String key : counter.keySet()){
                System.out.println("  "+key+": "+counter.get(key));
            }
        }
    }
}
