package commands;

import interfaces.Command;
import interfaces.CommandManagerCustom;
import src.network.requests.PrintUniqueUnitOfMeasureRequest;
import src.network.responses.Response;


public class PrintUniqueUnitOfMeasureCommand extends CommandBase implements Command {
    public PrintUniqueUnitOfMeasureCommand(CommandManagerCustom commandManager){
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        var commandMessageHandler = commandManager.getMessageHandler();
        var request = new PrintUniqueUnitOfMeasureRequest();
        sendToServer(request);
        return true;
    }

    @Override
    public void handleResponse(Response response) {
        handleResponseByDefault(response);
    }
}
