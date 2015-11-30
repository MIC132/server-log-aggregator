package downloading;

import util.Splitter;

/**
 * Created by MIC on 2015-11-28.
 */
public abstract class Source {
    enum Type{
        FTP, HTTP, SSH
    }

    final Type type;
    final String name;
    final String address;
    final String path; //For http we will simply append address + path
    final String namePattern;
    final String datePattern;
    final String splitPattern;
    final Splitter splitter;

    public Source(Type type, String name, String address, String path, String namePattern, String datePattern, String splitPattern) {
        this.type = type;
        this.name = name;
        this.address = address;
        this.path = path;
        this.namePattern = namePattern;
        this.datePattern = datePattern;
        this.splitPattern = splitPattern;
        this.splitter = new Splitter(splitPattern);
    }
}
