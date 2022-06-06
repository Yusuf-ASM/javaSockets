package com.example.schatv2;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class initController {


    @FXML
    private TextField ip;
    @FXML
    private CheckBox checkBox;
    @FXML
    private TextField name;
    @FXML
    private Button done;

    @FXML
    private Text error;
    @FXML
    private ProgressBar bar;

    private Stage stage;
    private Scene Scene;
    private int Type;
    private int processing = 0;



    @FXML
    private void checked() {
        if (checkBox.isSelected()) {
            checkBox.setText(" Client ");
            ip.setDisable(false);
        } else {
            checkBox.setText(" Server ");
            ip.setDisable(true);
        }
    }

    @FXML
    private void done() {
        if (processing == 1) {
            error.setVisible(true);

            if (Type == 1) {
                error.setText("please wait the server");

            } else {
                error.setText("please wait the client ");
            }
            return;
        }

        String[] data = {name.getText(), ip.getText()};

        Type = 0;
        if (data[0].isEmpty()) {
            error.setVisible(true);
            error.setText("Please enter a name in name field");
            return;
        } else if (checkBox.isSelected()) {
            if (data[1].isEmpty()) {
                error.setVisible(true);
                error.setText("Please enter the ip for the server");
                return;
            }
            Type = 1;
        }


        Task task = new Task<Boolean>() {
            @Override
            public Boolean call() {
                checkBox.setDisable(true);
                name.setDisable(true);
                if (Type == 1) {
                    ip.setDisable(true);
                    return transfer.initializeClient(data[1], 8888, data[0]);
                } else {
                    return transfer.initializeServer(8888, data[0]);
                }

            }
        };

        task.setOnSucceeded(workerStateEvent -> {
            boolean res = (boolean) task.getValue();
            if (res) {
                FXMLLoader root = new FXMLLoader(getClass().getResource("ui.fxml"));
                try {
                    Scene scene = new Scene(root.load(), 600, 500);
                    stage = (Stage) done.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else {
                processing = 0;
                error.setVisible(true);
                checkBox.setDisable(false);
                name.setDisable(false);
                bar.setVisible(false);

                if (Type == 1) {
                    error.setText("no such sever check server ip");
                    ip.setDisable(false);
                } else {
                    error.setText("error try again");
                }
            }
        });

        bar.setVisible(true);
        Thread connecting = new Thread(task);
        connecting.setDaemon(true);
        connecting.start();
        processing = 1;


    }
}

