package com.example.schatv2;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class controller implements Initializable {

    @FXML
    private TextArea textArea;
    @FXML
    private TextField message;

    private Stage stage;


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
        transfer.sendFile(path);
    }


    public void exit() {
        System.exit(0);
    }
}

