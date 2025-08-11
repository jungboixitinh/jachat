import java.util.HashSet;
import java.util.Set;

public class ChatRoom {
    private String name;
    private Set<ClientHandler> members = new HashSet<>();
    private Set<String> invitedUsers = new HashSet<>();

    public ChatRoom(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<ClientHandler> getMembers() {
        return members;
    }

    public Set<String> getInvitedUsers() {
        return invitedUsers;
    }

    public void addMember(ClientHandler clientHandler) {
        members.add(clientHandler);
    }

    public void removeMember(ClientHandler clientHandler) {
        members.remove(clientHandler);

    }

    public void inviteUser(String username) {
        invitedUsers.add(username);
    }

    public boolean isUserInvited(String username) {
        return invitedUsers.contains(username);
    }


    public boolean isMember(ClientHandler client) {
        for (ClientHandler member : members) {
            if (member.getClientUsername().equals(client.getClientUsername())) {
                return true;
            }
        }
        return false;
    }
}
