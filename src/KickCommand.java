public class KickCommand implements Command {
    @Override
    public void execute(ClientHandler clientHandler, String args) {
        // Simple admin check (you can enhance this)
        if (!clientHandler.isAdmin()) {
            clientHandler.sendPrivateMessage("You don't have permission to use this command.");
            return;
        }

        if (args.trim().isEmpty()) {
            clientHandler.sendPrivateMessage("Usage: /kick <username>");
            return;
        }

        String targetUsername = args.trim();

        // Find and kick user
        ClientHandler targetClient = null;
        for (ClientHandler client : ClientHandler.clientHandlers) {
            if (client.getClientUsername().equalsIgnoreCase(targetUsername)) {
                targetClient = client;
                break;
            }
        }

        if (targetClient == null) {
            clientHandler.sendPrivateMessage("User '" + targetUsername + "' not found.");
            return;
        }

        if (targetClient.getClientUsername().equalsIgnoreCase(clientHandler.getClientUsername())) {
            clientHandler.sendPrivateMessage("You cannot kick yourself!");
            return;
        }

        // Kick the user
        targetClient.sendPrivateMessage("You have been kicked by " + clientHandler.getClientUsername());
        targetClient.closeEverything(targetClient.socket, targetClient.bufferedReader, targetClient.bufferedWriter);

        clientHandler.broadcastServerMessage("SERVER: " + targetUsername + " was kicked by " + clientHandler.getClientUsername());
        clientHandler.sendPrivateMessage("User " + targetUsername + " has been kicked.");
    }

    @Override
    public String getDescription() {
        return "[Admin] Kick a user: /kick <username>";
    }
}