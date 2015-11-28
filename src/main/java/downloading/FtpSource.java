package downloading;

/**
 * Created by MIC on 2015-11-28.
 */
public class FtpSource extends Source{
    final String login;
    final String password;

    public FtpSource(String name, String address, String path, String namePattern, String splitPattern, String login, String password) {
        super(Type.FTP, name, address, path, namePattern, splitPattern);
        this.login = login;
        this.password = password;
    }
}
