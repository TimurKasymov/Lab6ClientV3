package commands;


import interfaces.Command;
import interfaces.CommandManagerCustom;
import src.network.requests.HistoryRequest;
import src.network.responses.Response;

public class HistoryCommand extends CommandBase implements Command {

    public HistoryCommand(CommandManagerCustom commandManager){
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        var commandMessageHandler = commandManager.getMessageHandler();
        commandMessageHandler.displayToUser("9 last used commands:");
        var request = new HistoryRequest();
        sendToServer(request);
        return true;
    }

    @Override
    public void handleResponse(Response response) {
        handleResponseByDefault(response);
    }
}
