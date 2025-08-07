import java.util.Map;
import java.util.HashMap;

public class CommandHandler {
    private Map<String, Command> commands;
    private ClientHandler clientHandler;

    public CommandHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.commands = new HashMap<>();
        registerCommands();
    }

    private void registerCommands() {
        commands.put("help", new HelpCommand());
        commands.put("whisper", new WhisperCommand());
        commands.put("kick", new KickCommand());
        commands.put("list", new ListUsersCommand());
        commands.put("status", new StatusCommand());
    }

    public boolean processCommand(String input) {
        if (!input.startsWith("/")) {
            return false; // Not a command
        }
        String[] parts = input.substring(1).split(" ", 2);
        String commandName = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        Command command = commands.get(commandName);
        if (command != null) {
            command.execute(clientHandler, args);
            return  true;
        } else {
            clientHandler.sendPrivateMessage("Unknown command: " + commandName + ". Type /help for a list of commands.");
            return true; // Command not found
        }
    }

    public Map<String, Command> getCommands() {
        return commands;
    }
}
