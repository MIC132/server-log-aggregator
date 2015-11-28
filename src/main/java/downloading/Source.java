package downloading;

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
    final String splitPattern;

    public Source(Type type, String name, String address, String path, String namePattern, String splitPattern) {
        this.type = type;
        this.name = name;
        this.address = address;
        this.path = path;
        this.namePattern = namePattern;
        this.splitPattern = splitPattern;
    }
}
