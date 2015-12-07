package GUI;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Created by Vulpes on 2015-12-06.
 */
public class ParsedData {
    private ObservableList<SimpleStringProperty> cellValue = FXCollections.observableArrayList();
    public int numberOfColumns = 0;

    public ParsedData(List<String> input){
        numberOfColumns = input.size();
        for(int i = 0; i < input.size(); i++)
            this.cellValue.add(new SimpleStringProperty(input.get(i)));
    }

    public SimpleStringProperty getCellValue(int whichOne) {
        return cellValue.get(whichOne);
    }
}