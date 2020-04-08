import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public class MyCart {

    private String action;
    private Connection conn;
    private Scanner scanner;

    // Constructor
    public MyCart(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void cartMenu() {

        boolean status = true;

        while (status) {
            System.out.println("\n" + Ressources.username + "'s cart");
            System.out.println("-h or help for available commands");
            System.out.println("Enter command:");

            action = scanner.nextLine();

            // parse input
            List<String> argumentsList = Arrays.asList(action);
            ListIterator<String> iterator = argumentsList.listIterator();

            String argument = iterator.next();
            switch (argument) {
                case "-h":
                case "help":
                    displayAvailableCommands();
                    break;

                case "-v":
                case "viewCart":
                    listItems();
                    break;

                case "-m":
                case "modify":
                    String consumable_id = iterator.next();
                    String new_consumable_qty = iterator.next();

                    if (Integer.parseInt(new_consumable_qty) == 0) {
                        // Delete record from table
                        try(PreparedStatement preparedStmt = conn.prepareStatement(Ressources.deleteCartRecordSQL)) {

                            preparedStmt.setInt   (1, Integer.parseInt(consumable_id));

                            preparedStmt.executeUpdate();

                        } catch (SQLException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }

                    // Update record
                    else {
                        // Modify qty
                        try(PreparedStatement preparedStmt = conn.prepareStatement(Ressources.updateCartRecordSQL)) {

                            preparedStmt.setInt   (1, Integer.parseInt(new_consumable_qty));
                            preparedStmt.setString(2, consumable_id);

                            preparedStmt.executeUpdate();

                        } catch (SQLException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }

                case "-p":
                case "purchase":
                    purchaseCart();
                    break;

                case "back":
                    //Relinquish control back to Menu
                    System.out.println("\n" + Ressources.username + "'s menu");
                    status = false;
                    break;

                default:
                    System.out.println("ERROR COMMAND INVALID\t please try again.\n");
                    break;
            }
        }
    }

    //TODO clear all items in cart and decrement item counts
    //TODO should add a constraint to remove consumable qty 0 from cart
    private void purchaseCart() {
    }

    // Displays all items in cart along with their quantity
    private void listItems() {

        int rowCount = 1;

        try (PreparedStatement pst = conn.prepareStatement(Ressources.retrieveCartContentSQL)) {

            pst.setString(1, Ressources.username);

            ResultSet rs = pst.executeQuery();

            System.out.println("Item\t\tConsumable_id\t\tConsumable_qty");

            while (rs.next()) {

                // Fetch all columns
                System.out.println(rowCount + "." + "\t\t" + rs.getString(1) + "\t\t\t\t\t" + rs.getInt(2));
                rowCount++;
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // User help
    private void displayAvailableCommands() {
        System.out.println("\nAvailable actions in Cart:");
        System.out.println("viewCart(-v)\tmodifyCart(-m itemId qty)\tpurchase(-p)\tback");
    }
}
