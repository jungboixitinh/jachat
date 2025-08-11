public class CreateRoomCommand implements Command {
    @Override
    public void execute(ClientHandler client, String args) {
        String roomName = args.trim();
        if (roomName.isEmpty()) {
            client.sendPrivateMessage("Usage: /create room_name");
            return;
        }
        if (ClientHandler.chatRooms.containsKey(roomName)) {
            client.sendPrivateMessage("Room already exists.");
            return;
        }

        ChatRoom room = new ChatRoom(roomName);
        ClientHandler.chatRooms.put(roomName, room);
        room.addMember(client);
        client.setCurrentRoom(room);
        client.sendPrivateMessage("Room '" + roomName + "' created and joined.");
    }

    @Override
    public String getDescription() {
        return "Create a new private chat room. Use: /create <room_name>";
    }
}