package com.example.little_pub;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onConfigureBarCick() {
        System.out.println("Criou o bar");
    }

    @FXML
    protected void onAddClientButtonClick () {
        System.out.println("Deu bom");
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
        System.out.println("Client " + clientId + " entered the bar and occupied a chair.");
        mutex.acquire();
        countClient++;
        mutex.release();
    }

    public void leave(String clientId) throws InterruptedException {
        System.out.println("Client " + clientId + " left the bar and vacated a chair.");
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
                int timeLeftBar = (LocalTime.now().getSecond() + tb);
                if (timeLeftBar > 59) timeLeftBar = timeLeftBar - 60;

                while(LocalTime.now().getSecond() != timeLeftBar){

                }

                bar.leave(id);

                int timeLeftHome = (LocalTime.now().getSecond() + tc);
                if (timeLeftHome > 59) timeLeftHome = timeLeftHome - 60;

                System.out.println("Client " + id + " chegou em casa.");

                while(LocalTime.now().getSecond() != timeLeftHome){

                }

                System.out.println("Client " + id + " Foi pu bar.");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class BarSimulation {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numClients, numChairs;

        System.out.print("Digite o número total de clientes: ");
        numClients = scanner.nextInt();

        System.out.print("Digite o número de cadeiras no bar (N): ");
        numChairs = scanner.nextInt();

        Bar bar = new Bar(numChairs);

        ArrayList<String> nameList = new ArrayList<String>();
        ArrayList<Integer> tbList = new ArrayList<Integer>();
        ArrayList<Integer> tcList = new ArrayList<Integer>();

        for (int i = 0; i < numClients; i++) {
            String id;
            int tb, tc;
            System.out.print("Digite o nome do cliente: ");
            id = scanner.next();

            System.out.print("Digite o tempo no bar do cliente: ");
            tb = scanner.nextInt();

            System.out.print("Digite o tempo em casa do cliente: ");
            tc = scanner.nextInt();

            nameList.add(id);
            tbList.add(tb);
            tcList.add(tc);

        }

        for (int i = 0; i < numClients; i++) {
            Thread clientThread = new Client(bar, nameList.get(i), tbList.get(i), tcList.get(i));
            clientThread.start();
        }
    }
}