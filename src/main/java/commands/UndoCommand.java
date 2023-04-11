package commands;

import exceptions.CommandInterruptionException;
import exceptions.InterruptionCause;
import interfaces.Command;
import interfaces.CommandManagerCustom;
import src.network.requests.UndoRequest;
import src.network.responses.Response;

public class UndoCommand extends CommandBase implements Command {


    public UndoCommand(CommandManagerCustom commandManager) {
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        var console = commandManager.getMessageHandler();
        try {
            var num = Integer.parseInt(args[0]);
            if(num < 1)
                throw new NumberFormatException();
            var request = new UndoRequest(num);
            sendToServer(request);
        }
        catch (NumberFormatException e){
            var commandMessageHandler = commandManager.getMessageHandler();
            commandMessageHandler.displayToUser("number of commands to undo must be a number > 0");
        }
        return true;
    }

    @Override
    public void handleResponse(Response response) {
        handleResponseByDefault(response);
    }

}
