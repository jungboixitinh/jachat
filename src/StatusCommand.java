public class StatusCommand implements Command {
    @Override
    public void execute(ClientHandler clientHandler, String args) {
        if (args.trim().isEmpty()) {
            clientHandler.sendPrivateMessage("Current status: " + clientHandler.getStatus());
            clientHandler.sendPrivateMessage("Usage: /status <new_status>");
            return;
        }

        String oldStatus = clientHandler.getStatus();
        clientHandler.setStatus(args.trim());

        // Broadcast status change
        String statusMessage = "SERVER: " + clientHandler.getClientUsername() +
                " changed status from '" + oldStatus + "' to '" + clientHandler.getStatus() + "'";
        clientHandler.broadcastServerMessage(statusMessage);

        clientHandler.sendPrivateMessage("Status updated to: " + clientHandler.getStatus());
    }

    @Override
    public String getDescription() {
        return "Change your status: /status <new_status>";
    }
}