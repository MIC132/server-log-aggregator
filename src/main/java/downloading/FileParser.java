/*
Copyright (c) 2015, AGH University of Science and Technology
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies,
either expressed or implied, of the FreeBSD Project.
*/

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
