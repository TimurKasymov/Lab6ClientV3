package commands;

import interfaces.Command;
import interfaces.CommandManagerCustom;
import src.network.requests.InfoRequest;
import src.network.responses.Response;

public class InfoCommand extends CommandBase implements Command {
    public InfoCommand(CommandManagerCustom commandManager){
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        var request = new InfoRequest();
        sendToServer(request);
        return true;
    }

    @Override
    public void handleResponse(Response response) {
        handleResponseByDefault(response);
    }
}