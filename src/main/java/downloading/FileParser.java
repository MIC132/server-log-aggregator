package downloading;

import util.Splitter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MIC on 2015-11-29.
 */
public class FileParser {

    public final Splitter splitter;

    public FileParser(Splitter splitter){
        this.splitter = splitter;
    }


    public List<List<String>> parseFile(File file, Source source, boolean continuation){
        List<List<String>> output = new ArrayList<>();
        int lineCounter;
        if(continuation){
            lineCounter = source.lastDownloadLine;
        }else{
            lineCounter = 0;
        }
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            if(continuation){
                for(int i = 0; i < lineCounter; i++){
                    reader.readLine();
                }
            }
            while ((line = reader.readLine()) != null){
                output.add(splitter.splitByGroups(line));
                lineCounter++;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        source.lastDownloadLine = lineCounter;
        return output;
    }
}
