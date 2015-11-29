package parsing;

import util.Splitter;

import java.io.*;
import java.util.*;

/**
 * Created by MIC on 2015-11-29.
 */
public class FileParser {

    public final Splitter splitter;

    public FileParser(Splitter splitter){
        this.splitter = splitter;
    }


    public List<List<String>> parseFile(File file){
        List<List<String>> output = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            while ((line = reader.readLine()) != null){
                output.add(splitter.splitByGroups(line));
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }
}
