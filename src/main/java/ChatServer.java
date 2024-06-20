import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private static final List<Socket> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (
                ServerSocket serverSocket = new ServerSocket(8888)
        ) {
            System.out.println("Chat server started on port 8888");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Error occurred" + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                String message = new String(buffer, 0, bytesRead);
                System.out.println("Received message from " + clientSocket.getInetAddress() + ": " + message);
                broadcastMessage(message, clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Error occurred" + e.getMessage());
        } finally {
            clients.remove(clientSocket);
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error occurred" + e.getMessage());
            }
        }
    }

    private static void broadcastMessage(String message, Socket senderSocket) {
        for (Socket client : clients) {
            if (client != senderSocket) {
                try {
                    OutputStream outputStream = client.getOutputStream();
                    outputStream.write((message + "\n").getBytes());
                    outputStream.flush();
                } catch (IOException e) {
                    System.out.println("Error occurred" + e.getMessage());
                }
            }
        }
    }
}