public class LeaveRoomCommand implements Command {
    @Override
    public void execute(ClientHandler client, String args) {
        if (client.getCurrentRoom() == null) {
            client.sendPrivateMessage("You are not in any room.");
            return;
        }

        String roomName = client.getCurrentRoom().getName();
        ChatRoom room = client.getCurrentRoom();
        room.removeMember(client);
        client.setCurrentRoom(null);

        // Notify other members in the room
        for (ClientHandler member : room.getMembers()) {
            if (!member.equals(client)) {
                member.sendPrivateMessage(client.getClientUsername() + " has left the room.");
            }
        }

        client.sendPrivateMessage("You have left the room: " + roomName);
    }

    @Override
    public String getDescription() {
        return "Leave the current chat room. Use: /leave";
    }
}
