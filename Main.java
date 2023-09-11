// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
import java.util.concurrent.Semaphore;
import java.util.Scanner;

class Bar {
    private Semaphore chairs;

    public Bar(int numChairs) {
        chairs = new Semaphore(numChairs);
    }

    public void enter(int customerId, int diningTime, int waitingTime) throws InterruptedException {
        System.out.println("Cliente " + customerId + " chegou ao bar.");

        if (chairs.tryAcquire()) {
            System.out.println("Cliente " + customerId + " encontrou uma cadeira vazia e está se sentando.");
            Thread.sleep(diningTime);
            System.out.println("Cliente " + customerId + " terminou de jantar e está saindo do bar.");
            chairs.release();
        } else {
            System.out.println("Cliente " + customerId + " não encontrou cadeira vazia e está esperando.");
            Thread.sleep(waitingTime);
            System.out.println("Cliente " + customerId + " finalmente encontrou uma cadeira vazia e está se sentando.");
            chairs.acquire();
            System.out.println("Cliente " + customerId + " terminou de jantar e está saindo do bar.");
            chairs.release();
        }
    }
}

class Cliente implements Runnable {
    private int id;
    private int diningTime;
    private int waitingTime;
    private Bar bar;

    public Cliente(int id, int diningTime, int waitingTime, Bar bar) {
        this.id = id;
        this.diningTime = diningTime;
        this.waitingTime = waitingTime;
        this.bar = bar;
    }

    @Override
    public void run() {
        try {
            bar.enter(id, diningTime, waitingTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite o número total de clientes: ");
        int numClientes = scanner.nextInt();

        System.out.print("Digite o número de cadeiras no bar (N): ");
        int numChairs = scanner.nextInt();

        System.out.print("Digite o tempo de permanência no jantar (em milissegundos): ");
        int tempoJantar = scanner.nextInt();

        Bar bar = new Bar(numChairs);

        // Crie e inicie várias threads de clientes
        for (int i = 1; i <= 100; i++) {
            int waitingTime = (int) (Math.random() * 3000); // Tempo de espera aleatório entre 0 e 3 segundos
            Thread clienteThread = new Thread(new Cliente(i, tempoJantar, waitingTime, bar));
            clienteThread.start();
        }
    }
}
