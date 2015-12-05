package downloading;

import java.time.temporal.ChronoUnit;

/**
 * Created by MIC on 2015-11-28.
 */
public class FtpSource extends Source{
    final String login;
    final String password;

    public FtpSource(String name, String address, String path, String namePattern, String splitPattern, ChronoUnit stepUnit, int stepAmount, String login, String password) {
        super(Type.FTP, name, address, path, namePattern, splitPattern, stepUnit, stepAmount);
        this.login = login;
        this.password = password;
    }
}
