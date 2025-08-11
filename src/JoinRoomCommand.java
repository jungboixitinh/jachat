public class JoinRoomCommand implements Command {
    @Override
    public void execute(ClientHandler client, String args) {
        String roomName = args.trim();
        if (roomName.isEmpty()) {
            client.sendPrivateMessage("Usage: /join <room_name>");
            return;
        }

        ChatRoom room = ClientHandler.chatRooms.get(roomName);
        if (room == null) {
            client.sendPrivateMessage("Room '" + roomName + "' does not exist.");
            return;
        }

        if (room.isMember(client)) {
            client.sendPrivateMessage("You are already a member of the room: " + roomName);
            return;
        }

        if (!room.isUserInvited(client.getClientUsername())) {
            client.sendPrivateMessage("You are not invited to join the room: " + roomName);
            return;
        }

        room.addMember(client);
        client.setCurrentRoom(room);
        client.sendPrivateMessage("You have joined the room: " + roomName);
    }

    @Override
    public String getDescription() {
        return "Join an existing private chat room. Usage: /join <room_name>";
    }
}