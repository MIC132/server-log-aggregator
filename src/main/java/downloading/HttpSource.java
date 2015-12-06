package downloading;

import java.io.Serializable;
import java.time.temporal.ChronoUnit;

/**
 * Created by MIC on 2015-11-28.
 */
public class HttpSource extends Source implements Serializable{
    public HttpSource(String name, String address, String path, String namePattern, String splitPattern, ChronoUnit stepUnit, int stepAmount) {
        super(Type.HTTP, name, address, path, namePattern, splitPattern, stepUnit, stepAmount);
    }
}
