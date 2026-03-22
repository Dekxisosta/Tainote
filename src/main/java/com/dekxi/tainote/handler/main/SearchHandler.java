package com.dekxi.tainote.handler;

import com.dekxi.tainote.db.*;
import com.dekxi.tainote.model.*;
import javafx.scene.control.*;

import java.util.*;

public class SearchHandler {
    private ComboBox<String> filterComboBox;
    private DatabaseManager databaseManager;
    private TextField searchField;
    private ListView listView;

    public SearchHandler(
            ComboBox<String> filterComboBox,
            DatabaseManager databaseManager,
            TextField searchField,
            ListView listView
    ){
        this.filterComboBox = filterComboBox;
        this.databaseManager = databaseManager;
        this.searchField = searchField;
        this.listView = listView;

        setUpSearchField();
        setUpFilterComboBox();
    }

    private void setUpSearchField(){
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = filterComboBox.getValue();
            List<TainotePreview> results = switch (filter) {
                case "Title" -> databaseManager.searchByTitle(newVal);
                case "Content" -> databaseManager.searchByContent(newVal);
                case "Tag" -> databaseManager.searchByTag(newVal);
                case "Author" -> databaseManager.searchByAuthor(newVal);
                default -> databaseManager.searchAll(newVal);
            };
            System.out.println("Entered this");
            listView.getItems().setAll(results);
        });
    }
    private void setUpFilterComboBox(){
        filterComboBox.getItems().addAll("Title", "Content", "Tag", "Author");
        filterComboBox.getSelectionModel().select(0);
        filterComboBox.setOnAction(e -> {
            searchField.setText(searchField.getText());
        });
    }
}
