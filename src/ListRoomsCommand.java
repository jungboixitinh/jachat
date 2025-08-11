import java.util.Map;

public class ListRoomsCommand implements Command {

    @Override
    public void execute(ClientHandler clientHandler, String args) {
        Map<String, ChatRoom> chatRooms = ClientHandler.chatRooms;
        StringBuilder roomList = new StringBuilder("=== AVAILABLE ROOMS ===\n");

        if (chatRooms.isEmpty()) {
            roomList.append("No rooms available.\n");
        } else {
            for (Map.Entry<String, ChatRoom> entry : chatRooms.entrySet()) {
                roomList.append("• ")
                        .append(entry.getKey())
                        .append(" (")
                        .append(entry.getValue().getMembers().size())
                        .append(" members)\n");
            }
        }

        clientHandler.sendPrivateMessage(roomList.toString());
    }

    @Override
    public String getDescription() {
        return "List all chat rooms: /listroom";
    }
}