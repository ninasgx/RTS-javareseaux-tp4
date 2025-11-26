import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class MultithreadedTCPServer {

    private int port;
    private static AtomicInteger clientCounter = new AtomicInteger(0);

    public MultithreadedTCPServer(int port) {
        this.port = port;
    }

    public void launch() {
        System.out.println("Starting multithreaded TCP server on port " + port + "...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server running.");

            while (true) {
                Socket clientSocket = serverSocket.accept();

                int clientId = clientCounter.incrementAndGet();

                ConnectionThread handler = new ConnectionThread(clientSocket, clientId);
                handler.start();

                // Optional debug
                System.out.println("Active threads: " + (Thread.activeCount() - 1));
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = 8080;

        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }

        new MultithreadedTCPServer(port).launch();
    }
}
