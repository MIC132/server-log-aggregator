package downloading;

import com.jcraft.jsch.*;

import java.io.*;

/**
 * Created by MIC on 2015-11-28.
 */
public class SshLogDownloader extends LogDownloader implements AutoCloseable{
    final int port;
    final String address;
    final String user;
    final String password;

    final Session session;
    final Channel channel;
    final ChannelSftp channelSftp;

    public SshLogDownloader(String address, int port, String user, String password) throws JSchException {
        super(Source.Type.SSH);
        this.address = address;
        this.port = port;
        this.user = user;
        this.password = password;
        JSch jSch = new JSch();
        session = jSch.getSession(user, address, port);
        session.setPassword(password);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        channel = session.openChannel("sftp");
        channel.connect();
        channelSftp = (ChannelSftp)channel;
    }

    public void downloadToFile(String path, File file) throws SftpException, IOException {
        byte[] buffer = new byte[1024];
        BufferedInputStream bis = new BufferedInputStream(channelSftp.get(path));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        int readCount;
        while( (readCount = bis.read(buffer)) > 0) {
            bos.write(buffer, 0, readCount);
        }
        bis.close();
        bos.close();
    }


    @Override
    public void close() throws Exception {
        session.disconnect();
        channel.disconnect();
    }
}
