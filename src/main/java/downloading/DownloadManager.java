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
public class DownloadManager {

    final Source source;
    final LogDownloader downloader;
    final DateTimeFormatter formatter;
    final FileParser parser;

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

    public List<List<String>> initialDownload(LocalDateTime startTime){
        return downloadFromDate(startTime, false);
    }

    public List<List<String>> downloadSinceLast(){
        LocalDateTime startDate = source.lastDownload;
        return downloadFromDate(startDate, true);
    }

}
