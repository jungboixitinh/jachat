import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private String status;

    public Client(Socket socket, String clientUsername, String status) {
        this.username = clientUsername;
        this.status = status;
        initConnection(socket);
    }

    private void initConnection(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.write(status);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything();
        }
    }

    public void sendMessage() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                if (socket != null && socket.isConnected()) {
                    String clientMessage = scanner.nextLine();
                    bufferedWriter.write(clientMessage);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } else {
                    System.out.println("*ERROR* Can not connect to server.");
                    reconnect();
                }
            } catch (IOException e) {
                System.out.println("*ERROR* Lost connection. Reconnecting...");
                reconnect();
            }
        }
    }

    public void listenForMessages() {
        new Thread(() -> {
            while (true) {
                try {
                    if (socket != null && socket.isConnected()) {
                        String clientMessage = bufferedReader.readLine();
                        if (clientMessage == null) {
                            System.out.println("*ERROR* Lost connection. Reconnecting...");
                            reconnect();
                        } else {
                            System.out.println(clientMessage);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("*ERROR* Lost connection. Reconnecting...");
                    reconnect();
                }
            }
        }).start();
    }

    private void reconnect() {
        closeEverything();
        int attempts = 0;
        boolean connected = false;

        while (attempts < 5 && !connected) {
            try {
                attempts++;
                System.out.println("Connecting to server ... (" + attempts + "/5)");
                socket = new Socket("172.29.101.38", 8080);
                connected = true;
                System.out.println("Connected to server!");
            } catch (IOException e) {
                System.out.println("*ERROR* Cannot connect to server. Retry in 5s...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        if (!connected) {
            System.out.println("Failed to connect to server. Exiting...");
            System.exit(0);
        }
    }

    public void closeEverything() {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your status: ");
        String status = scanner.nextLine();

        Socket socket = null;
        int attempts = 0;
        boolean connected = false;

        while (attempts < 5 && !connected) {
            try {
                attempts++;
                System.out.println("Connecting to server ... (" + attempts + "/5)");
                socket = new Socket("172.29.101.38", 8080);
                connected = true;
                System.out.println("Connected to server!");
            } catch (IOException e) {
                System.out.println("*ERROR* Cannot connect to server. Retry in 5s...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        if (!connected) {
            System.out.println("Failed to connect to server. Exiting...");
            System.exit(0);
        }

        Client client = new Client(socket, username, status);
        client.listenForMessages();
        client.sendMessage();
    }

}
