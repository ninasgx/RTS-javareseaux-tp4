
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;

public class ConnectionThread extends Thread {

    private Socket clientSocket;
    private int clientId;

    public ConnectionThread(Socket clientSocket, int clientId) {
        this.clientSocket = clientSocket;
        this.clientId = clientId;
        this.setName("ClientHandler-" + clientId);  // helps debugging
    }

    @Override
    public void run() {
        String clientAddress = clientSocket.getInetAddress().getHostAddress();
        int clientPort = clientSocket.getPort();

        System.out.println("[" + LocalDateTime.now() + "] Client#"
                + clientId + " connected from " + clientAddress + ":" + clientPort);

        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream(), "UTF-8")); PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true)) {

            out.println("Welcome! You are client #" + clientId);

            String line;
            String prefix = "[client#" + clientId + " " + clientAddress + "] ";

            while ((line = in.readLine()) != null) {
                String trimmed = line.trim();
                String record = prefix + trimmed;

                // Print received message to server console
                System.out.println(record);

                // 1) Check for quit command
                if (trimmed.equalsIgnoreCase("quit") || trimmed.equalsIgnoreCase("/quit")) {
                    // Send goodbye message to client
                    out.println("Goodbye, client #" + clientId + "!");
                    // Jump out of the loop and close connection
                    break;
                }

                // 2) Normal message, echo back to client
                out.println(record);
            }

        } catch (IOException e) {
            System.err.println("Client#" + clientId + " error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket for client#" + clientId);
        }

        System.out.println("[" + LocalDateTime.now() + "] Client#"
                + clientId + " disconnected.");
    }
}
