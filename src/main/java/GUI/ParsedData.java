package GUI;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Vulpes on 2015-12-06.
 */
public class ParsedData {
    private ObservableList<SimpleStringProperty> cellValue = FXCollections.observableArrayList();
    public int numberOfColumns = 0;

    public ParsedData(int howManyColumns) {
        numberOfColumns = howManyColumns;
        for(int i=0; i<howManyColumns; ++i)
            this.cellValue.add(new SimpleStringProperty("" + (new Random().nextInt(1000))));
    }

    public ParsedData(String... input){
        numberOfColumns = input.length;
        for(int i = 0; i < input.length; i++)
            this.cellValue.add(new SimpleStringProperty(input[i]));
    }

    public ParsedData(List<String> input){
        numberOfColumns = input.size();
        for(int i = 0; i < input.size(); i++)
            this.cellValue.add(new SimpleStringProperty(input.get(i)));
    }

    public SimpleStringProperty getCellValue(int whichOne) {
        return cellValue.get(whichOne);
    }

    public void setCellValue(String cellValue, int whichOne) {
        this.cellValue.set(whichOne, new SimpleStringProperty(cellValue));
    }
}