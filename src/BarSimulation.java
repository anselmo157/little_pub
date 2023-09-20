import java.util.concurrent.Semaphore;
import java.util.Scanner;

class Bar {
    private final Semaphore chairs;
    final private Semaphore mutex = new Semaphore(1);
    private int countClient = 0;

    private final int limitClients;

    public Bar(int numChairs) {
        chairs = new Semaphore(numChairs, true);
        limitClients = numChairs;
    }

    public void enter(int clientId) throws InterruptedException {
        chairs.acquire();
        System.out.println("Client " + clientId + " entered the bar and occupied a chair.");
        mutex.acquire();
        countClient++;
        mutex.release();
    }

    public void leave(int clientId) throws InterruptedException {
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
    private int clientId;

    public Client(Bar bar) {
        this.bar = bar;
        this.clientId = nextClientId++;
    }

    @Override
    public void run() {
        while (true) {
            try {
                bar.enter(clientId);
                Thread.sleep(4000);
                bar.leave(clientId);
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

        for (int i = 0; i < numClients; i++) {
            Thread clientThread = new Client(bar);
            clientThread.start();
        }
    }
}