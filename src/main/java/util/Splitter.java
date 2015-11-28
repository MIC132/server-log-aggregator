package util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MIC on 2015-11-22.
 */
public class Splitter {
    public static List<String> splitByGroups(String string, String regex){
        Matcher m = Pattern.compile(regex).matcher(string);
        List<String> out = new ArrayList<String>();
        if(m.find()){
            for(int i = 1; i <= m.groupCount(); i++){
                out.add(m.group(i));
            }
        }
        return out;
    }
}
