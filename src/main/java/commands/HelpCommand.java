package commands;

import interfaces.Command;
import interfaces.CommandManagerCustom;
import src.network.requests.HelpRequest;
import src.network.responses.Response;

public class HelpCommand extends CommandBase implements Command {

    public HelpCommand(CommandManagerCustom commandManager){
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        var commandMessageHandler = commandManager.getMessageHandler();
        var request = new HelpRequest();
        sendToServer(request);
        return true;
    }

    @Override
    public void handleResponse(Response response) {
        handleResponseByDefault(response);
    }
}
