package downloading;

/**
 * Created by MIC on 2015-11-22.
 */
public abstract class LogDownloader {
    final Source.Type type;

    public LogDownloader(Source.Type type){
        this.type = type;
    }
}
