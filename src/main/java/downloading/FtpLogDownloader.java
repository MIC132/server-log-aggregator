package downloading;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;

/**
 * Created by MIC on 2015-11-28.
 */
public class FtpLogDownloader extends LogDownloader implements AutoCloseable{
    final FTPClient ftpClient;
    final String server;
    final int port;
    final String user;
    final String password;

    public FtpLogDownloader(String address, int port, String user, String password) throws IOException {
        this.server = address;
        this.port = port;
        this.user = user;
        this.password = password;

        ftpClient = new FTPClient();
        ftpClient.connect(server, port);
        ftpClient.login(user,password);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
    }

    public void downloadToFile(String path, File file) throws IOException {
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
        boolean success = ftpClient.retrieveFile(path, outputStream);
        outputStream.close();
    }

    @Override
    public void close() throws Exception {
        if (ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
    }
}
