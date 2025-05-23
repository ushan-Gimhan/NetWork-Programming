package com.service.project.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    public TextField time;
    public DatePicker dgetDate;
    @FXML
    private Button btnSend;

    @FXML
    private TextArea txtChat;

    @FXML
    private TextField txtInput;

    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    @FXML
    void btnSendOnClick(ActionEvent event) throws IOException {
        String message = "Client: " + txtInput.getText();
        LocalDate date = dgetDate.getValue();
        txtChat.appendText(message + "\n");
        dataOutputStream.writeUTF(message);
        dataOutputStream.flush();
        txtInput.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 5000);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    String header = dataInputStream.readUTF();
                    if (header.equals("MESSAGE")) {
                        String message = header;
                        Platform.runLater(() -> txtChat.appendText(message + "\n"));
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void getgate(ActionEvent event) {

    }
}
