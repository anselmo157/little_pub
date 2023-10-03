package com.example.little_pub;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class Controller implements Initializable {

    @FXML
    private Button addBar;

    @FXML
    private Button addClient;

    @FXML
    private TextField chairsField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField barTimeField;

    @FXML
    private TextField homeTimeField;

    @FXML
    public ListView<String> log;

    @FXML
    public ListView<String> home;

    @FXML
    public ListView<String> pub;

    @FXML
    public ListView<String> waiting;

    int chairs;

    Bar bar;

    @FXML
    protected void onConfigureBarClick() {
        try {
            chairs = Integer.parseInt(chairsField.getText());
            System.out.println(chairs);
            chairsField.setEditable(false);
            chairsField.setMouseTransparent(true);
            chairsField.setFocusTraversable(false);
            log.getItems().add("Criou o bar do Laion!");
            addBar.setText("Criou o Bar!");
            addClient.setText("Adicionar cliente");
            addBar.setDisable(true);
            bar = new Bar(chairs);
        } catch (NumberFormatException e) {
            addBar.setText("Entre com um valor numérico");
            System.out.println(bar);
        } catch (Exception e) {
            addBar.setText(e.getMessage());
        }
    }

    @FXML
    protected void onAddClientClick() {
        if (bar != null) {
            Thread clientThread = new Client(bar, nameField.getText(), Integer.parseInt(barTimeField.getText()), Integer.parseInt(homeTimeField.getText()));
            clientThread.start();
            nameField.setText("");
            barTimeField.setText("");
            homeTimeField.setText("");
        } else {
            addClient.setText("Crie um Bar");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.getItems().add("Aplicação inicializada!");
    }

    class Bar {
        private final Semaphore chairs;
        final private Semaphore mutex = new Semaphore(1);
        private int countClient = 0;

        private final int limitClients;

        public Bar(int numChairs) {
            chairs = new Semaphore(numChairs, true);
            limitClients = numChairs;
        }

        public void enter(String clientId) throws InterruptedException {
            if(chairs.availablePermits() == 0) {
                log.getItems().add("Cliente " + clientId + " foi para o bar e está esperando na fila.");
                log.refresh();
                waiting.getItems().add(clientId);
                waiting.refresh();
            }
            chairs.acquire();
            log.getItems().addAll("Cliente " + clientId + " entrou no bar e se sentou.");
            log.refresh();
            mutex.acquire();
            waiting.getItems().remove(clientId);
            waiting.refresh();
            countClient++;
            mutex.release();
            pub.getItems().add(clientId);
            pub.refresh();
        }

        public void leave(String clientId) throws InterruptedException {
            log.getItems().addAll("Cliente " + clientId + " saiu do bar e foi para casa.");
            log.refresh();
            mutex.acquire();
            pub.getItems().remove(clientId);
            pub.refresh();
            countClient--;
            mutex.release();
            home.getItems().add(clientId);
            home.refresh();
            if (countClient == 0) {
                chairs.release(limitClients);
            }
        }
    }

    class Client extends Thread {
        private Bar bar;
        public String id;

        private int tb;

        private int tc;

        public int time;

        public Client(Bar bar, String id, int tb, int tc) {
            this.bar = bar;
            this.id = id;
            this.tb = tb;
            this.tc = tc;

            this.time = tb;
        }

        private static void execute_task() {
            float Count = 0, s = 0;
            for (int j = 0; j < 100; j++) {
                for (int i = 0; i < 200; i++) {
                    Count++;
                    s = s + Count;
                }
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    bar.enter(id);

                    long timeLeaveBar = (System.currentTimeMillis() + tb * 1000L);

                    while (System.currentTimeMillis() < timeLeaveBar){
                        execute_task();
                    }

                    bar.leave(id);

                    long timeLeaveHome = (System.currentTimeMillis() + tc * 1000L);


                    while (System.currentTimeMillis() < timeLeaveHome) {
                        execute_task();
                    }

                    log.getItems().add("Cliente " + id + " foi para o bar.");
                    home.getItems().remove(id);
                    home.refresh();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


