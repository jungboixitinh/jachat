import java.util.Map;

public class HelpCommand implements Command {
    @Override
    public void execute(ClientHandler clientHandler, String args) {
        CommandHandler processor = clientHandler.getCommandHandler();
        StringBuilder help = new StringBuilder("=== AVAILABLE COMMANDS ===\n");

        for (Map.Entry<String, Command> entry : processor.getCommands().entrySet()) {
            help.append("/").append(entry.getKey()).append(" - ")
                    .append(entry.getValue().getDescription()).append("\n");
        }

        clientHandler.sendPrivateMessage(help.toString());
    }

    @Override
    public String getDescription() {
        return "Shows this help message";
    }
}