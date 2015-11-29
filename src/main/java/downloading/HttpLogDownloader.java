package downloading;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by MIC on 2015-11-22.
 */
public class HttpLogDownloader extends LogDownloader{

    public HttpLogDownloader(){
        super(Source.Type.HTTP);
    }

    public void downloadToFile(String address, File file) throws IOException {
        downloadCommonsIO(address, file);
    }

    private void downloadCommonsIO(String address, File file) throws IOException{
        URL url = new URL(address);
        FileUtils.copyURLToFile(url, file);
    }

    private void downloadStandard(String address, File file) throws IOException{
        URL website = new URL(address);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }

}
