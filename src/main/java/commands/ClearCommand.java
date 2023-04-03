package commands;

import interfaces.Command;
import interfaces.CommandManagerCustom;
import src.network.requests.ClearRequest;
import src.network.responses.Response;

public class ClearCommand extends CommandBase implements Command {

    public ClearCommand(CommandManagerCustom commandManager) {
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        var request = new ClearRequest();
        sendToServer(request);
        return true;
    }

    @Override
    public void handleResponse(Response response) {
        var commandMessageHandler = commandManager.getMessageHandler();
        commandMessageHandler.displayToUser("clearing done");
    }

}
