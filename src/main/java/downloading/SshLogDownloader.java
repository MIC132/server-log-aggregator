/*
Copyright (c) 2015, AGH University of Science and Technology
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies,
either expressed or implied, of the FreeBSD Project.
*/

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
