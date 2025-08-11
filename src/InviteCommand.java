public class InviteCommand implements Command {
    @Override
    public void execute(ClientHandler client, String args) {
        String[] parts = args.split(" ", 2);
        if (parts.length < 2) {
            client.sendPrivateMessage("Usage: /invite <username> <room_name>");
            return;
        }

        String username = parts[0].trim();
        String roomName = parts[1].trim();

        ClientHandler targetClient = ClientHandler.getClientByUsername(username);
        if (targetClient == null) {
            client.sendPrivateMessage("User '" + username + "' not found.");
            return;
        }

        ChatRoom room = ClientHandler.chatRooms.get(roomName);
        if (room == null) {
            client.sendPrivateMessage("Room '" + roomName + "' does not exist.");
            return;
        }

        if (room.isMember(client)) {
            room.inviteUser(username);
            targetClient.sendPrivateMessage("You have been invited to join the room: " + roomName);
            client.sendPrivateMessage("Invitation sent to " + username + " for room: " + roomName);
        } else {
            client.sendPrivateMessage("You are not a member of the room: " + roomName);
        }
    }

    @Override
    public String getDescription() {
        return "Invite a user to a private chat room. Usage: /invite <username> <room_name>";
    }
}