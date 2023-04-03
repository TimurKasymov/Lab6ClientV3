package commands;

import interfaces.Command;
import interfaces.CommandManagerCustom;
import src.network.responses.Response;

public class RemoveFirstCommand extends CommandBase implements Command {

    public RemoveFirstCommand(CommandManagerCustom commandManager) {
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        var removeCommand = "remove_by_id 1";
        commandManager.executeCommand(removeCommand);
        return true;
    }

    @Override
    public void handleResponse(Response response) {
        handleResponseByDefault(response);
    }

}
