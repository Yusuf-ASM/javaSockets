package com.example.schatv2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class view extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("init.fxml"));
        Scene scene = new Scene(loader.load(), 600, 500);
        stage.setTitle("Secure Chat");
        stage.setMinHeight(500);
        stage.setMinWidth(600);
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}