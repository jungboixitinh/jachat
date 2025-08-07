import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    Socket socket;
    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;
    private String clientUsername;
    private String status;
    private boolean isAdmin;
    private CommandHandler commandHandler;

    public String getClientUsername() {
        return clientUsername;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            if (!setupUsername()) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                return;
            }

            this.status = bufferedReader.readLine();
            if (this.status == null || this.status.trim().isEmpty()) {
                this.status = "online";
            }

            this.isAdmin = clientHandlers.isEmpty();

            this.commandHandler = new CommandHandler(this);

            clientHandlers.add(this);

            sendPrivateMessage("=== WELCOME TO THE CHAT ===");
            sendPrivateMessage("Your username is: " + clientUsername);
            if (isAdmin) {
                sendPrivateMessage("You are the admin of this chat.");
            } else {
                sendPrivateMessage("You can type /help for a list of commands.");
            }

            broadcastServerMessage("SERVER: " + clientUsername + " has entered the chat!");

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String message;
        while (socket.isConnected()) {
            try {
                message = bufferedReader.readLine();
                if (message == null) {
                    break;
                }
                if (commandHandler.processCommand(message)) {
                    continue; //
                }

                broadcastRegularMessage(message);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

//    public void broadcastMessage(String message, boolean isServerMessage) {
//        if (isServerMessage) {
//            for (ClientHandler clientHandler : clientHandlers) {
//                try {
//                    clientHandler.sendPrivateMessage(message);
//                } catch (Exception e) {
//                    closeEverything(socket, bufferedReader, bufferedWriter);
//                }
//            }
//        } else {
//            broadcastMessage(message, false);
//        }
//    }

    public boolean setupUsername() throws IOException {
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;
        while (attempts < MAX_ATTEMPTS) {
            String proposedUsername = bufferedReader.readLine();

            if (proposedUsername == null || proposedUsername.trim().isEmpty()) {
                return false;
            }
            proposedUsername = proposedUsername.trim();

            String validationError = validateUsername(proposedUsername);
            if (validationError != null) {
                sendPrivateMessage("Invalid username: " + validationError);
                attempts++;
                if (attempts >= MAX_ATTEMPTS) {
                    sendPrivateMessage("Too many invalid attempts. Please try again later.");
                    return false;
            }
                continue;
        }
            if (isUsernameTaken(proposedUsername)) {
                sendPrivateMessage("Username '" + proposedUsername + "' is already taken. Please choose another.");
                attempts++;
                if (attempts >= MAX_ATTEMPTS) {
                    sendPrivateMessage("Too many invalid attempts. Please try again later.");
                    return false;
                }
                continue;
            } else {
                this.clientUsername = proposedUsername;
                sendPrivateMessage("Username set to: " + clientUsername);
                return true;
            }
        }
        return false;
    }

    private String validateUsername(String username) throws IOException {
        if (username == null || username.trim().isEmpty()) {
            return  "Username cannot be empty.";
        }
        if (username.length() < 3 || username.length() > 15) {
            return "Username must be between 3 and 15 characters.";
        }
        return null;
    }

    private boolean isUsernameTaken(String username) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.clientUsername != null && clientHandler.clientUsername.equalsIgnoreCase(username)) {
                    return true;
                }
            }
        return false;
    }

    public void broadcastRegularMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    String formattedMessage = clientUsername + " (" + status + "): " + message;
                    clientHandler.sendPrivateMessage(formattedMessage);
                }
            } catch (Exception e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void broadcastServerMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                clientHandler.sendPrivateMessage(message);
            } catch (Exception e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void sendPrivateMessage(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void removeClientHandler() {
        synchronized(clientHandlers) {
            clientHandlers.remove(this);
            broadcastServerMessage("SERVER: " + clientUsername + " has left the chat!");
        }
    }

    public boolean isAdmin() {
        return  isAdmin;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
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

}
