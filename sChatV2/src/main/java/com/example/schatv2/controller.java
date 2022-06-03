package com.example.schatv2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class controller implements Initializable {
    @FXML
    private Button sendm;
    @FXML
    private Button sendf;
    @FXML
    private TextArea textArea;
    @FXML
    private TextField message;

    private Stage stage;
    private Scene Scene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        transfer.receiveThread(textArea);

    }

    @FXML
    private void sendMessage() {
        String content = message.getText();
        if (content.isEmpty()) {
            return;
        }
        textArea.appendText(transfer.Sender + ": " + content + "\n");
        message.clear();
        transfer.sendText(content);
    }


    @FXML
    private void sendFile() throws IOException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        String path = fileChooser.showOpenDialog(stage).getAbsolutePath();
//        System.out.println(path);
        transfer.sendFile(path);

//        FXMLLoader root = new FXMLLoader(getClass().getResource("init.fxml"));
//        Scene scene = new Scene(root.load(), 600, 500);
//        stage = (Stage) sendf.getScene().getWindow();
//        stage.setScene(scene);
//        stage.show();
    }


}

