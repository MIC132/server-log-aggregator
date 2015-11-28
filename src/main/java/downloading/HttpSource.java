package downloading;

/**
 * Created by MIC on 2015-11-28.
 */
public class HttpSource extends Source{
    public HttpSource(String name, String address, String path, String namePattern, String splitPattern) {
        super(Type.HTTP, name, address, path, namePattern, splitPattern);
    }
}
