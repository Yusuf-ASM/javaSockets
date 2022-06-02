package com.example.schatv2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class initController implements Initializable {

    @FXML
    private TextArea textArea;

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

    private Stage stage;
    private Scene Scene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        transfer.initializeClient("localhost",9090);
//        transfer.initializeServer(9090);
//        transfer.receiveTextThread(textArea);

    }


    @FXML
    private void checked(){
        if(checkBox.isSelected()){
            checkBox.setText(" Client ");
            ip.setDisable(false);
        }
        else {
            checkBox.setText(" Server ");
            ip.setDisable(true);
        }
    }
    @FXML
    private void done() throws IOException {
        String[] data = {name.getText(),ip.getText()};

        if (data[0].isEmpty()){
            error.setVisible(true);
            error.setText("Please enter a name in name field");
            return;
        }
        if(checkBox.isSelected()){
            if (data[1].isEmpty()){
                error.setVisible(true);
                error.setText("Please enter the ip for the server");
                return;
            }
            transfer.initializeClient(data[1],8888,data[0]);
        }
        else {
            checkBox.setText(" Server ");
            transfer.initializeServer(8888,data[0]);

        }
        FXMLLoader root = new FXMLLoader(getClass().getResource("ui.fxml"));
        Scene scene = new Scene(root.load(), 600, 500);
        stage = (Stage) done.getScene().getWindow();
        stage.setScene(scene);
        stage.show();



    }


}

