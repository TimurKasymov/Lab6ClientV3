package commands;

import interfaces.Command;
import interfaces.CommandManagerCustom;
import src.network.requests.RemoveByIdRequest;
import src.network.responses.Response;

public class RemoveByIdCommand extends CommandBase implements Command {

    public RemoveByIdCommand(CommandManagerCustom commandManager){
        super(commandManager);
    }
    @Override
    public boolean execute(String[] args) {
        var commandMessageHandler = commandManager.getMessageHandler();

        try {
            var id = Long.parseLong(args[0]);
            if(id < 1) throw new NumberFormatException();
            var request = new RemoveByIdRequest(id);
            sendToServer(request);
            return true;
        }
        catch (NumberFormatException exception){
            commandMessageHandler.displayToUser("ID must be an number > 0. Try typing this command again");
                return false;
        }
    }

    @Override
    public void handleResponse(Response response) {
        handleResponseByDefault(response);
    }
}
