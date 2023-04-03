package commands;

import exceptions.CommandInterruptionException;
import interfaces.Command;
import interfaces.CommandManagerCustom;
import service.InputService;
import src.models.Product;
import src.network.requests.AddRequest;
import src.network.requests.UpdateByIdRequest;
import src.network.responses.Response;
import src.network.responses.UpdateByIdResponse;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Class for updating the element by it`s ID
 */
public class UpdateByIdCommand extends CommandBase implements Command {
    private final InputService inputService;

    {
        inputService = commandManager.getInputService();
    }

    public UpdateByIdCommand(CommandManagerCustom commandManager) {
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        var commandMessageHandler = commandManager.getMessageHandler();
        try {
            long id = Long.parseLong(args[0]);
            if (id <= 0) {
                commandMessageHandler.displayToUser("ID must be an number greater than 0. Try typing this command again");
                return false;
            }
            var name = inputService.inputName();
            var coord = inputService.inputCoordinates();
            var price = inputService.inputPrice();
            var manufCost = inputService.inputManufactureCost();
            var unit = inputService.inputUnitOfMeasure();

            int yesOrNo = 0;
            for (; ; ) {
                try {
                    commandMessageHandler.displayToUser("enter a number: ");
                    commandMessageHandler.displayToUser("should we add organization? enter the number: 1 - Yes or 2 - No");
                    Scanner scanner = new Scanner(System.in);
                    yesOrNo = scanner.nextInt();
                    if (yesOrNo == 1 || yesOrNo == 2)
                        if (yesOrNo == 2)
                            commandMessageHandler.displayToUser("organization will not be defined");
                    break;
                } catch (InputMismatchException e) {
                    commandMessageHandler.displayToUser("enter a number: ");
                }
            }
            var prod = new Product(id, name, coord, price, manufCost,
                    unit, yesOrNo == 1 ? inputService.inputOrganization() : null);
            var request = new UpdateByIdRequest(prod);
            sendToServer(request);
            return true;
        } catch (NumberFormatException exception) {
            System.out.println("ID must be an number. Try typing this command again");
        } catch (CommandInterruptionException e) {
            commandMessageHandler.displayToUser("updating product was canceled by entered command");
            commandManager.executeCommand(e.getEnteredCommand());
        }
        return true;
    }

    @Override
    public void handleResponse(Response response) {
        var resp = (UpdateByIdResponse)response;
        var commandMessageHandler = commandManager.getMessageHandler();
        if(response.getError()==null)
            commandMessageHandler.displayToUser(resp.getMessageForClient());
        else
            commandMessageHandler.displayToUser(resp.getError());
    }
}
