import java.util.concurrent.Semaphore;

class Bar {
    private Semaphore chairs;
    private Semaphore allChairsEmpty;
    private int clientCount = 0;

    public Bar(int numChairs) {
        chairs = new Semaphore(numChairs, true);
        allChairsEmpty = new Semaphore(0, true);
    }

    public void enter(int clientId) throws InterruptedException {
        if (clientCount < chairs.availablePermits()) {
            chairs.acquire();
            clientCount++;
            System.out.println("Client " + clientId + " entered the bar and occupied a chair. (Total clients: " + clientCount + ")");
        } else {
            allChairsEmpty.acquire(); // Wait until all chairs are empty
            chairs.acquire();
            clientCount++;
            System.out.println("Client " + clientId + " entered the bar and occupied a chair. (Total clients: " + clientCount + ")");
        }
    }

    public void leave(int clientId) {
        chairs.release();
        clientCount--;
        System.out.println("Client " + clientId + " left the bar and vacated a chair. (Total clients: " + clientCount + ")");
        if (clientCount == 0) {
            allChairsEmpty.release(chairs.availablePermits()); // Signal all chairs are empty
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
        try {
            bar.enter(clientId);
            // Simulate some time in the bar
            Thread.sleep(2000);
            bar.leave(clientId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class BarSimulation {
    public static void main(String[] args) {
        int numClients, numChairs;

        // Get user input for the number of clients and chairs
        // You can use Scanner to get user input.
        numClients = 5; // Change this to the desired number of clients
        numChairs = 3;  // Change this to the desired number of chairs

        Bar bar = new Bar(numChairs);

        for (int i = 0; i < numClients; i++) {
            Thread clientThread = new Client(bar);
            clientThread.start();
        }
    }
}
