package downloading;

import sun.plugin.dom.exception.InvalidStateException;

/**
 * Created by MIC on 2015-11-29.
 */
public class DownloadManager {

    final Source source;
    //final LogDownloader downloader;

    public DownloadManager(Source source){
        this.source = source;

        if(source.type == Source.Type.HTTP){
            //downloader = new HttpLogDownloader();
        }else if(source.type == Source.Type.FTP){

        }else if(source.type == Source.Type.SSH){

        }else {
            throw new InvalidStateException("Invalid source type: "+source.type);
        }

    }

}
