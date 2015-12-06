package GUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by Vulpes on 2015-12-06.
 */
public class API {
    private static API api = new API();
    private API() {}
    public static API getInstance(){return api;}

    protected static int connect(){
        return 0;
    }

    public ObservableList<ParsedData> download(int amount, int offset) {
        ObservableList<ParsedData> data = FXCollections.observableArrayList();

        for(Integer i = offset; i < offset + amount; i++){
            data.add(new ParsedData(4));
        }

        return data;
    }
}