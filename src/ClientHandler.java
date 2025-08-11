import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    public static Map<String, ChatRoom> chatRooms = new HashMap<>();
    private ChatRoom currentRoom = null;
    Socket socket;
    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;
    private String clientUsername;
    private String status;
    private boolean isAdmin;
    private CommandHandler commandHandler;

    public static ClientHandler getClientByUsername(String username) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.clientUsername != null && clientHandler.clientUsername.equalsIgnoreCase(username)) {
                return clientHandler;
            }
        }
        return null;
    }

    public ChatRoom getCurrentRoom() {
        return currentRoom;
    }

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

            if (!validateUsername()) {
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
                    continue;
                }
                if (message.isEmpty()) {
                    continue;
                }
                broadcastRegularMessage(message);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public boolean validateUsername() throws IOException {

        for (int i = 0; i < 3; i++) {
            String tempUsername = bufferedReader.readLine();

            tempUsername = tempUsername.trim();

            if (tempUsername.isEmpty()) {
                sendPrivateMessage("Username cannot be empty.");
                continue;
            }

            if (tempUsername.length() < 3 || tempUsername.length() > 15) {
                sendPrivateMessage("Username must be between 3 and 15 characters.");
                continue;
            }

            if (isUsernameTaken(tempUsername)) {
                sendPrivateMessage("Username '" + tempUsername + "' is already taken. Please choose another.");
                continue;
            }

            this.clientUsername = tempUsername;
            sendPrivateMessage("Username set to: " + clientUsername);
            return true;
        }

        sendPrivateMessage("Too many invalid attempts. Please try again later.");
        return false;
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
        if (currentRoom != null) {
            for (ClientHandler clientHandler : currentRoom.getMembers()) {
                try {
                    if (!clientHandler.clientUsername.equals(clientUsername)) {
                        String formattedMessage = "[" + currentRoom.getName() + "] " + clientUsername + " (" + status + "): " + message;
                        clientHandler.sendPrivateMessage(formattedMessage);
                    }

                } catch (Exception e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        } else {
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

    public void removeClientHandler() {
            clientHandlers.remove(this);
            broadcastServerMessage("SERVER: " + clientUsername + " has left the chat!");
    }

    public void setCurrentRoom(ChatRoom room) {
        if (room != null) {
            this.currentRoom = room;
            room.addMember(this);
        } else {
            if (this.currentRoom != null) {
                this.currentRoom.removeMember(this);
            }
            this.currentRoom = null;
        }
    }
}
