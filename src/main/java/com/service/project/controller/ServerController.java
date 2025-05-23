package com.service.project.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ServerController implements Initializable {

    @FXML
    private Button btnSend;

    @FXML
    private TextArea txtChat;

    @FXML
    private TextField txtInput;

    private ServerSocket serverSocket;
    private final List<Client> clientList = new ArrayList<>();

    @FXML
    void btnSendOnClick(javafx.event.ActionEvent event) {
        String message = "Server: " + txtInput.getText();
        txtChat.appendText(message + "\n");


        for (Client client : clientList) {
            try {
                client.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        txtInput.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5000);
                Platform.runLater(() -> txtChat.appendText("Server started...\n"));
                System.out.println("Server started...");

                while (!txtChat.getText().equals("BYE")) {
                    Socket clientSocket = serverSocket.accept();
                    Client client = new Client(clientSocket);
                    clientList.add(client);
                    new Thread(client).start();

                    Platform.runLater(() -> txtChat.appendText("New client connected\n"));
                    System.out.println("New client connected");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void AddClientOncliked(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/client.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    class Client implements Runnable {
        private final Socket socket;
        private final DataInputStream inStream;
        private final DataOutputStream outStream;

        public Client(Socket socket) throws IOException {
            this.socket = socket;
            this.inStream= new DataInputStream(socket.getInputStream());
            this.outStream = new DataOutputStream(socket.getOutputStream());
        }

        public void sendMessage(String message) throws IOException {
            outStream.writeUTF(message);
            outStream.flush();
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String header = inStream.readUTF();
                        String finalMessage = header;
                        Platform.runLater(() -> txtChat.appendText(finalMessage + "\n"));

                        for (Client client : clientList) {
                            if (client != this) {
                                client.sendMessage(finalMessage);
                            }
                        }
                    }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
