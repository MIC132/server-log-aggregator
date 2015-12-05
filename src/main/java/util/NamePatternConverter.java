package util;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Created by MIC on 2015-12-05.
 */
public class NamePatternConverter {
    public static DateTimeFormatter convertToFormatter(String namePattern) throws IOException {
        List<String> parts = Arrays.asList(namePattern.split("\\*",-1));
        if(parts.size() % 2 == 0 || parts.size() == 1){
            throw new IOException("Invalid name pattern");
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i<parts.size(); i++){
            if(i % 2 == 1){
                builder.append(parts.get(i));
            }else{
                builder.append('\'');
                builder.append(parts.get(i).replace("\'", "\'\'"));
                builder.append('\'');
            }
        }
        return DateTimeFormatter.ofPattern(builder.toString());
    }
}
