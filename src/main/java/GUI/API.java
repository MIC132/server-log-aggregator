package GUI;
import classes.H2DatabaseAccessor;
import com.jcraft.jsch.JSchException;
import downloading.DownloadManager;
import downloading.Source;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by Vulpes on 2015-12-06.
 */
public class API {
    private static API api = new API();
    private API() {}
    public static API getInstance(){return api;}
    H2DatabaseAccessor databaseAccessor = null;

    public int connectToDatabase(String url, String login, String password){
            //TODO: Login to fucking database
        databaseAccessor = new H2DatabaseAccessor(login, password, url);
        return 0;
    }

    public int connectToSource(Source source, LocalDate since){
        DownloadManager downloadManager = null;
        try {
            downloadManager = new DownloadManager(source);
            List<List<String>> data = downloadManager.initialDownload(LocalDateTime.of(since, LocalTime.MIDNIGHT));
            int howManyColumns = data.get(0).size();
            List<String> colNames = new ArrayList<>();
            for(int i = 0; i < howManyColumns; i++)colNames.add("col_" + (i+1));

            databaseAccessor.addTable("ThisProjectSucks", colNames);
            databaseAccessor.addRowsToTable("ThisProjectSucks", colNames, data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    ObservableList<ParsedData> parse(List<List<String>> input){
        ObservableList<ParsedData> data = FXCollections.observableArrayList();
        if(input == null) return data;

        for(int i = 0; i < input.size(); i++)
            data.add(new ParsedData(input.get(i)));

        return data;
    }

    public ObservableList<ParsedData> download(int amount, int offset) {
        List<List<String>> dataFromBase = null;
        try {
            dataFromBase = databaseAccessor.selectValuesFromTable("ThisProjectSucks", asList("*"),null, amount, offset );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ObservableList<ParsedData> data = parse(dataFromBase);

        return data;
    }
}