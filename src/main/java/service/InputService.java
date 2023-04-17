package service;

import container.CommandsContainer;
import exceptions.CommandInterruptionException;
import src.models.*;
import src.models.Product;
import src.utils.Commands;

import java.util.Collection;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class InputService {

    private final MessageHandler messageHandler;

    public InputService(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    private Scanner scanner = null;

    {
        scanner = new Scanner(System.in);
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public String inputName() throws NoSuchElementException, CommandInterruptionException {
        for (; ; ) {
            try {
                messageHandler.displayToUser("Do not enter a very long name, some parts of it may get lost");
                messageHandler.displayToUser("Enter a name: ");
                String name = scanner.nextLine().trim();
                if (CommandsContainer.contains(name))
                    throw new CommandInterruptionException(name);
                if (name.equals("")) {
                    messageHandler.displayToUser("This value cannot be empty. Try again");
                    continue;
                }
                return name;
            } catch (InputMismatchException inputMismatchException) {
                messageHandler.displayToUser("This value must be non-empty string.");
            }
        }
    }

    public OrganizationType inputOrganizationType() throws NoSuchElementException, CommandInterruptionException {
        for (; ; ) {
            try {
                messageHandler.displayToUser("Choose OrganizationType. Enter the number corresponding to the desired option. ");
                inputEnum(OrganizationType.class);
                var orgType = readEnum(OrganizationType.class);
                return (OrganizationType) orgType;
            } catch (InputMismatchException inputMismatchException) {
                messageHandler.displayToUser("This value must be a number. Try again. ");
                scanner.next();
            }
        }
    }

    public Integer getInt() throws NoSuchElementException, CommandInterruptionException {
        for (; ; ) {
            try {
                var str = scanner.nextLine();
                if (str.equals("")) {
                    messageHandler.displayToUser("This value cannot be empty. Try again");
                    continue;
                }
                if (CommandsContainer.contains(str))
                    throw new CommandInterruptionException(str);
                return Integer.parseInt(str);
            } catch (InputMismatchException | NumberFormatException inputMismatchException) {
                messageHandler.displayToUser("This value must be a number. Try again. ");
            }
        }
    }

    public String validateScriptName(String scriptCommand) {
        for (; ; ) {
            try {
                var split = scriptCommand.split(" ");
                if(!scriptCommand.contains(Commands.EXECUTE_SCRIPT) || split.length != 2)
                    return null;
                if(split[1].split("\\.").length == 2 && split[1].split("\\.")[1].equals("txt"))
                    return split[1];
                return null;
            } catch (Exception e) {
                messageHandler.displayToUser("please, provide the name of the script file with .txt extension");
            }
        }
    }

    public Product inputProduct(String enteredCommand) throws CommandInterruptionException {
        messageHandler.displayToUser("adding product..");

        var name = inputName();
        var coord = inputCoordinates();
        var price = inputPrice();
        var manufCost = inputManufactureCost();
        var unit = inputUnitOfMeasure();

        int yesOrNo = 0;
        for( ; ; ) {
            try {
                messageHandler.displayToUser("enter a number: ");
                messageHandler.displayToUser("should we add organization? enter the number: 1 - Yes or 2 - No");
                yesOrNo = getInt();
                if(yesOrNo != 1 && yesOrNo != 2)
                    continue;
                if(yesOrNo == 2)
                    messageHandler.displayToUser("organization will not be defined");
                break;
            } catch (InputMismatchException e) {
                messageHandler.displayToUser("enter a number: ");
            }
        }
        var prod = new Product(enteredCommand.split(" ").length == 2
                ? Long.parseLong(enteredCommand.split(" ")[1]) : 0L, name, coord, price, manufCost,
                unit, yesOrNo == 1 ? inputOrganization() : null);
        return prod;
    }

    public Long getID(String enteredCommand) throws NoSuchElementException, CommandInterruptionException {
        for (; ; ) {
            try {
                String str;
                if(enteredCommand != null && enteredCommand.split(" ").length == 2){
                    str = enteredCommand;
                }
                str = scanner.nextLine();
                if (str.equals("")) {
                    messageHandler.displayToUser("This value cannot be empty. Try again");
                    continue;
                }
                if (CommandsContainer.contains(str))
                    throw new CommandInterruptionException(str);
                var val = Long.parseLong(str);
                if(val < 1){
                    messageHandler.displayToUser("This value must be > 0. Try again");
                    continue;
                }
                return val;
            } catch (InputMismatchException | NumberFormatException inputMismatchException) {
                messageHandler.displayToUser("This value must be a number. Try again. ");
            }
        }
    }

    public String inputString() throws CommandInterruptionException {
        for(;;){
            var str = scanner.nextLine();
            if (str.equals("")) {
                messageHandler.displayToUser("This value cannot be empty. Try again");
                continue;
            }
            if (CommandsContainer.contains(str))
                throw new CommandInterruptionException(str);
            return str;
        }

    }

    /**
     * method for combining X and Y inputs
     */
    public Coordinates inputCoordinates() throws NoSuchElementException, CommandInterruptionException {
        messageHandler.displayToUser("adding coordinates..");
        var coor = new Coordinates(inputXLocation(), inputYLocation());
        messageHandler.displayToUser("done with coordinates..");
        return coor;
    }

    /**
     * Method for receiving x-coordinate of location of element
     *
     * @return Double xLocation
     */
    public Double inputXLocation() throws NoSuchElementException, CommandInterruptionException {
        for (; ; ) {
            try {
                messageHandler.displayToUser("Enter X coordinate of location: ");
                var str = scanner.nextLine();
                if (str.equals("")) {
                    messageHandler.displayToUser("This value cannot be empty. Try again");
                    continue;
                }
                if (CommandsContainer.contains(str))
                    throw new CommandInterruptionException(str);
                var val = Double.parseDouble(str);
                if (Double.isInfinite(val))
                    throw new InputMismatchException();
                return val;
            } catch (InputMismatchException | NumberFormatException inputMismatchException) {
                messageHandler.displayToUser("This value must be a double-type number. Try again. ");
            }
        }
    }

    /**
     * Method for receiving y-coordinate of element
     *
     * @return float yLocation
     */
    public float inputYLocation() throws NoSuchElementException, CommandInterruptionException {
        for (; ; ) {
            try {
                messageHandler.displayToUser("Enter Y coordinate of location: ");
                var str = scanner.nextLine();
                if (str.equals("")) {
                    messageHandler.displayToUser("This value cannot be empty. Try again");
                    continue;
                }
                if (CommandsContainer.contains(str))
                    throw new CommandInterruptionException(str);
                var val = Float.parseFloat(str);
                if (Float.isInfinite(val))
                    throw new InputMismatchException();
                if (val <= -264) {
                    messageHandler.displayToUser("This value must greater than -264. Try again. ");
                    continue;
                }
                return val;
            } catch (InputMismatchException | NumberFormatException inputMismatchException) {
                messageHandler.displayToUser("This value must be a float-type number. Try again. ");
            }
        }
    }

    public float inputPrice() throws NoSuchElementException, CommandInterruptionException{
        return inputPrice(null);
    }
    /**
     * method for taking price input
     */
    public float inputPrice(String command) throws NoSuchElementException, CommandInterruptionException {
        for (; ; ) {
            try {
                String str;
                messageHandler.displayToUser("Enter the price of the product: ");
                if(command != null && command.split(" ").length == 2){
                    str = command;
                }
                else
                    str = scanner.nextLine().trim();
                if (str.equals("")) {
                    messageHandler.displayToUser("This value cannot be empty. Try again");
                    continue;
                }
                if (CommandsContainer.contains(str))
                    throw new CommandInterruptionException(str);
                var price = Float.parseFloat(str);
                if (Float.isInfinite(price))
                    throw new InputMismatchException();
                if (price < 0)
                    throw new InputMismatchException();
                return price;
            } catch (InputMismatchException | NumberFormatException inputMismatchException) {
                messageHandler.displayToUser("This value must be a float-type positive number. Try again. ");
            }
        }
    }

    public Double inputManufactureCost() throws NoSuchElementException, CommandInterruptionException {
        return inputManufactureCost(null);
    }
    /**
     * method for taking price input
     */
    public Double inputManufactureCost(String command) throws NoSuchElementException, CommandInterruptionException {
        for (; ; ) {
            try {
                messageHandler.displayToUser("Enter manufacture cost: ");
                String str;
                if(command != null && command.split(" ").length == 2){
                    str = command;
                }
                else
                    str = scanner.nextLine().trim();
                if (str.equals("")) {
                    messageHandler.displayToUser("This value cannot be empty. Try again");
                    continue;
                }
                if (CommandsContainer.contains(str))
                    throw new CommandInterruptionException(str);
                var inp = Double.parseDouble(str);
                if (Double.isInfinite(inp))
                    throw new InputMismatchException();
                if (inp < 1)
                    throw new InputMismatchException();
                return inp;
            } catch (InputMismatchException | NumberFormatException inputMismatchException) {
                messageHandler.displayToUser("This value must be a Double-type number. Try again. ");
            }
        }
    }

    public <T extends Enum<T>> void inputEnum(Class<T> enumClass) throws NoSuchElementException, CommandInterruptionException {
        var enums = enumClass.getEnumConstants();
        for (int i = 1; i <= enums.length; i++)
            messageHandler.displayToUser(enums[i - 1] + " - " + i);
    }

    public <T extends Enum<T>> Enum<T> readEnum(Class<T> enumClass) throws NoSuchElementException, CommandInterruptionException {
        var enums = enumClass.getEnumConstants();
        while (true) {
            try {
                var str = scanner.nextLine();
                if (str.equals("")) {
                    messageHandler.displayToUser("This value cannot be empty. Try again");
                    continue;
                }
                if (CommandsContainer.contains(str))
                    throw new CommandInterruptionException(str);
                var index = Integer.parseInt(str);
                if (1 > index || index > enums.length) {
                    messageHandler.displayToUser(String.format("You should enter a number from %s to %s. Try again. ", 1, enums.length));
                    continue;
                }
                return enums[index - 1];
            } catch (InputMismatchException | NumberFormatException inputMismatchException) {
                scanner.next();
            }

        }
    }

    public UnitOfMeasure inputUnitOfMeasure() throws NoSuchElementException, CommandInterruptionException {
        for (; ; ) {
            try {
                messageHandler.displayToUser("Choose UnitOfMeasure. Enter the number corresponding to the desired option. ");
                inputEnum(UnitOfMeasure.class);
                var unitOfMeasure = readEnum(UnitOfMeasure.class);
                return (UnitOfMeasure) unitOfMeasure;

            } catch (InputMismatchException inputMismatchException) {
                messageHandler.displayToUser("This value must be a number. Try again. ");
                scanner.next();
            }
        }
    }

    public Organization inputOrganization() throws NoSuchElementException, CommandInterruptionException {
        messageHandler.displayToUser("adding organization..");
        var org = new Organization(0L, inputName(), inputAnnualTurnover(), inputOrganizationType());
        messageHandler.displayToUser("done with organization..");
        return org;
    }

    public Integer inputAnnualTurnover() throws NoSuchElementException, CommandInterruptionException {
        for (; ; ) {
            try {
                messageHandler.displayToUser(String.format("Enter annual turnover. Note that value can only be from %s to %s: ", 1, Integer.MAX_VALUE));
                var str = scanner.nextLine();
                if (str.equals("")) {
                    messageHandler.displayToUser("This value cannot be empty. Try again");
                    continue;
                }
                if (CommandsContainer.contains(str))
                    throw new CommandInterruptionException(str);
                var inp = Integer.parseInt(str);
                if (inp < 1)
                    throw new InputMismatchException();
                return inp;
            } catch (InputMismatchException | NumberFormatException inputMismatchException) {
                messageHandler.displayToUser("This value must be a double-type number. Try again. ");
            }
        }
    }

}
