import java.util.ArrayList;

public class ListUsersCommand implements Command {

    @Override
    public void execute(ClientHandler clientHandler,String args) {
        ArrayList<ClientHandler> clients = ClientHandler.clientHandlers;
        StringBuilder userList = new StringBuilder("=== ONLINE USERS ===\n");

        for (ClientHandler client : clients) {
            userList.append("• ")
                    .append(client.getClientUsername())
                    .append(" (")
                    .append(client.getStatus())
                    .append(")\n");
        }

        if (clients.size() == 1) {
            userList.append("You are the only user online.\n");
        } else {
            userList.append("Total users: ").append(clients.size());
        }
        clientHandler.sendPrivateMessage(userList.toString());
    }

    @Override
    public String getDescription() {
        return "List all online users with their statuses.";
    }
}
