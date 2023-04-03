package commands;

import exceptions.CommandInterruptionException;
import exceptions.InterruptionCause;
import interfaces.Command;
import interfaces.CommandManagerCustom;
import org.slf4j.Logger;
import src.loggerUtils.LoggerManager;
import src.models.Product;
import src.network.requests.AddRequest;
import src.network.requests.Request;
import src.network.responses.AddResponse;
import src.network.responses.Response;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;


public class AddCommand extends CommandBase implements Command {

    private final Logger logger;

    public AddCommand(CommandManagerCustom commandManager) {
        super(commandManager);
        logger = LoggerManager.getLogger(AddCommand.class);
    }

    @Override
    public boolean execute(String[] args) {
        var commandMessageHandler = commandManager.getMessageHandler();
        try{
            var inputService = commandManager.getInputService();
            commandMessageHandler.displayToUser("adding product..");

            var name = inputService.inputName();
            var coord = inputService.inputCoordinates();
            var price = inputService.inputPrice();
            var manufCost = inputService.inputManufactureCost();
            var unit = inputService.inputUnitOfMeasure();

            int yesOrNo = 0;
            for( ; ; ) {
                try {
                    commandMessageHandler.displayToUser("enter a number: ");
                    commandMessageHandler.displayToUser("should we add organization? enter the number: 1 - Yes or 2 - No");
                    yesOrNo = commandManager.getInputService().getInt();
                    if(yesOrNo != 1 && yesOrNo != 2)
                        continue;
                    if(yesOrNo == 2)
                        commandMessageHandler.displayToUser("organization will not be defined");
                    break;
                } catch (InputMismatchException e) {
                    commandMessageHandler.displayToUser("enter a number: ");
                }
            }
            var prod = new Product(0L, name, coord, price, manufCost,
                    unit, yesOrNo == 1 ? inputService.inputOrganization() : null);
            var request = new AddRequest(prod);
            sendToServer(request);

        }

        catch (NoSuchElementException exception){
            commandMessageHandler.displayToUser("adding product was canceled");
        }
        catch (CommandInterruptionException e){
            if(e.getInterruptionCause() == InterruptionCause.EXIT)
                commandMessageHandler.displayToUser("adding product was successfully canceled");
            else{
                commandMessageHandler.displayToUser("adding product was canceled by entered command");
                commandManager.executeCommand(e.getEnteredCommand());
            }
        }
        return true;
    }

    @Override
    public void handleResponse(Response response) {
        var commandMessageHandler = commandManager.getMessageHandler();
        var resp = (AddResponse)response;
        commandMessageHandler.displayToUser("product with id: " + resp.newId + " added to the collection");
    }
}
