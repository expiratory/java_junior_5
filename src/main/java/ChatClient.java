import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ChatClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;

    public static class ConsoleThread extends Thread {
        public final OutputStream outputStream;
        public final Socket clientSocket;

        public ConsoleThread(OutputStream outputStream, Socket clientSocket) {
            this.outputStream = outputStream;
            this.clientSocket = clientSocket;
        }

        public void run() {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String consoleMessage;

            try{
                while (true) {
                    consoleMessage = consoleReader.readLine();
                    outputStream.write((consoleMessage + "\n").getBytes());
                    outputStream.flush();

                    if (consoleMessage.equals("exit")) {
                        clientSocket.close();
                    }
                }
            } catch (IOException e) {
                System.out.println("Error occurred" + e.getMessage());
            }

        }
    }

    public static class ServerThread extends Thread {
        public final InputStream inputStream;

        public ServerThread(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public void run() {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader serverReader = new BufferedReader(inputStreamReader);
            String serverMessage;

            try{
                while (true) {
                    serverMessage = serverReader.readLine();

                    if (!serverMessage.isEmpty()) {
                        System.out.println("Received message from server: " + serverMessage);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error occurred" + e.getMessage());
            }

        }
    }

    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
            System.out.println("Chat client connected to server");

            OutputStream outputStream = clientSocket.getOutputStream();
            InputStream inputStream = clientSocket.getInputStream();

            new ServerThread(inputStream).start();
            new ConsoleThread(outputStream, clientSocket).start();

        } catch (IOException e) {
            System.out.println("Error occurred" + e.getMessage());
        }
    }
}