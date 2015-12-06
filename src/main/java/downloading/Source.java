package downloading;

import util.Splitter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by MIC on 2015-11-28.
 */
public abstract class Source implements Serializable{
    enum Type{
        FTP, HTTP, SSH
    }

    final Type type;
    final String name;
    final String address;
    final String path; //For http we will simply append address + path
    final String namePattern;
    final String splitPattern;
    final Splitter splitter;
    LocalDateTime lastDownload;
    int lastDownloadLine = 0;
    final ChronoUnit stepUnit;
    final int stepAmount;

    public Source(Type type, String name, String address, String path, String namePattern, String splitPattern, ChronoUnit stepUnit, int stepAmount) {
        this.type = type;
        this.name = name;
        this.address = address;
        this.path = path;
        this.namePattern = namePattern;
        this.splitPattern = splitPattern;
        this.splitter = new Splitter(splitPattern);
        this.stepUnit = stepUnit;
        this.stepAmount = stepAmount;
    }
}
