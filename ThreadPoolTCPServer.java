import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolTCPServer {

    private int port;
    private ExecutorService threadPool;
    private static AtomicInteger clientCounter = new AtomicInteger(0);

    public ThreadPoolTCPServer(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10); // fixed pool of 10 threads
    }

    public void launch() {
        System.out.println("Thread Pool Server started on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                int clientId = clientCounter.incrementAndGet();

                // submit task to thread pool
                threadPool.execute(() -> {
                    ConnectionThread handler =
                        new ConnectionThread(clientSocket, clientId);

                    handler.run(); 
                    // run() is called directly because we’re already inside a worker thread
                });

                System.out.println("Active pool threads: " + Thread.activeCount());
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public void shutdown() {
        threadPool.shutdown();
        System.out.println("Server shutdown initiated");
    }

    public static void main(String[] args) {
        int port = 8080;

        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }

        ThreadPoolTCPServer server = new ThreadPoolTCPServer(port);
        server.launch();
    }
}
