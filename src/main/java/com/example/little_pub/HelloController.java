package com.example.little_pub;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class HelloController {

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
            addBar.setText("Criou o Bar!");
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
        chairs.acquire();
        System.out.println("Cliente " + clientId + " entrou no bar e se sentou");
        mutex.acquire();
        countClient++;
        mutex.release();
    }

    public void leave(String clientId) throws InterruptedException {
        System.out.println("Cliente " + clientId + " saiu do bar e foi para casa.");
        mutex.acquire();
        countClient--;
        mutex.release();
        if (countClient == 0) {
            chairs.release(limitClients);
        }
    }
}

class Client extends Thread {
    private static int nextClientId = 1;
    private Bar bar;
    public String id;

    private int tb;

    private int tc;

    public String status;

    public Client(Bar bar, String id, int tb, int tc) {
        this.bar = bar;
        this.id = id;
        this.tb = tb;
        this.tc = tc;
    }

    private static void execute_task() {
        float Count = 0, s = 0;
        for (int j = 0; j < 10000000; j++) {
            for (int i = 0; i < 2000; i++) {
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

                while (System.currentTimeMillis() < timeLeaveBar) {
                    execute_task();
                }

                bar.leave(id);

                long timeLeaveHome = (System.currentTimeMillis() + tc * 1000L);


                while (System.currentTimeMillis() < timeLeaveHome) {
                    execute_task();
                }

                System.out.println("Cliente " + id + " foi para o bar.");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}