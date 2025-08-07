public interface Command {
        void execute(ClientHandler clientHandler,String args);
        String getDescription();
}
