package downloading;

import java.time.temporal.ChronoUnit;

/**
 * Created by MIC on 2015-11-28.
 */
public class SshSource extends Source{
    final String login;
    final String password;

    public SshSource(String name, String address, String path, String namePattern, String splitPattern, ChronoUnit stepUnit, int stepAmount, String login, String password) {
        super(Type.SSH, name, address, path, namePattern, splitPattern, stepUnit, stepAmount);
        this.login = login;
        this.password = password;
    }
}
