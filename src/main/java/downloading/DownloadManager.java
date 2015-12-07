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

import com.jcraft.jsch.JSchException;
import sun.plugin.dom.exception.InvalidStateException;
import util.NamePatternConverter;
import util.Splitter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MIC on 2015-11-29.
 */

/**
 * Class used for managing downloads from single Source
 */
public class DownloadManager {

    final Source source;
    final LogDownloader downloader;
    final DateTimeFormatter formatter;
    final FileParser parser;

    /**
     * Quite self explanatory.
     *
     * @param source Source from which this download manager will be downloading.
     * @throws IOException When there is problem with downloading.
     * @throws JSchException When there is problem with downloading from ssh.
     */
    public DownloadManager(Source source) throws IOException, JSchException {
        this.source = source;
        if(source.type == Source.Type.HTTP){
            downloader = new HttpLogDownloader();
        }else if(source.type == Source.Type.FTP){
            FtpSource ftpSource = (FtpSource)source;
            downloader = new FtpLogDownloader(ftpSource.address, 21, ftpSource.login, ftpSource.password);
        }else if(source.type == Source.Type.SSH){
            SshSource sshSource = (SshSource)source;
            downloader = new SshLogDownloader(sshSource.address, 22, sshSource.login, sshSource.password);
        }else {
            throw new InvalidStateException("Invalid source type: "+source.type);
        }
        formatter = NamePatternConverter.convertToFormatter(source.namePattern);
        this.parser = new FileParser(new Splitter(source.splitPattern));
    }

    /**
     * Downloads and parses logs from startTime till now. Continuation indicates whether position rememberd in file should be used.
     *
     * @param startTime Since when the logs should be downloaded
     * @param continuation Whether to use remembered position
     * @return List of log entries, each as List of Strings, according to the splitting regex.
     */
    private List<List<String>> downloadFromDate(LocalDateTime startTime, boolean continuation){
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime currentTime = startTime;
        List<List<String>> out = new ArrayList<>();
        while (currentTime.isBefore(endTime)){
            try {
                String path = source.path + currentTime.format(formatter);
                File target = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+currentTime.format(formatter));
                target.deleteOnExit();
                if (downloader.type == Source.Type.HTTP) {
                    HttpLogDownloader httpDownloader = (HttpLogDownloader) downloader;
                    httpDownloader.downloadToFile(source.address + path, target);
                } else if (downloader.type == Source.Type.FTP) {
                    FtpLogDownloader ftpDownloader = (FtpLogDownloader) downloader;
                    ftpDownloader.downloadToFile(path, target);
                } else if (downloader.type == Source.Type.SSH) {
                    SshLogDownloader sshDownloader = (SshLogDownloader)downloader;
                    sshDownloader.downloadToFile(path, target);
                } else {
                    throw new InvalidStateException("Invalid downloader type: " + downloader.type);
                }
                out.addAll(parser.parseFile(target, source, continuation));
                continuation = false;
                target.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            currentTime = currentTime.plus(source.stepAmount, source.stepUnit);
        }
        source.lastDownload = currentTime;
        return out;
    }

    /**
     * Performs initial download since the given startTime.
     *
     * @param startTime from when the download should be performed.
     * @return  List of log entries, each as List of Strings, according to the splitting regex.
     */
    public List<List<String>> initialDownload(LocalDateTime startTime){
        return downloadFromDate(startTime, false);
    }

    /**
     * Performs an updating download, that is since the last downloaded file.
     *
     * @return List of log entries, each as List of Strings, according to the splitting regex.
     */
    public List<List<String>> downloadSinceLast(){
        LocalDateTime startDate = source.lastDownload;
        return downloadFromDate(startDate, true);
    }

}
