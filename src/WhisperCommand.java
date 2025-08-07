public class WhisperCommand implements Command {
    @Override
    public void execute(ClientHandler clientHandler, String args) {
        if (args.trim().isEmpty()) {
            clientHandler.sendPrivateMessage("Usage: /whisper <username> <message>");
            return;
        }

        String[] parts = args.split(" ", 2);
        if (parts.length < 2) {
            clientHandler.sendPrivateMessage("Usage: /whisper <username> <message>");
            return;
        }

        String targetUsername = parts[0];
        String message = parts[1];

        // Find target user
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

        if (targetClient == clientHandler) {
            clientHandler.sendPrivateMessage("You cannot whisper to yourself!");
            return;
        }

        // Send whisper
        String whisperMessage = "[WHISPER from " + clientHandler.getClientUsername() + "]: " + message;
        targetClient.sendPrivateMessage(whisperMessage);

        // Confirm to sender
        clientHandler.sendPrivateMessage("[WHISPER to " + targetUsername + "]: " + message);
    }

    @Override
    public String getDescription() {
        return "Send private message to a user: /whisper <username> <message>";
    }
}