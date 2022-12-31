import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Admin {
    public static boolean login() throws IOException {
        System.out.println("\nLOG IN AS ADMIN ROLE");
        // Create a scanner object to be ready to get input information (username & password) from users via keyboard.
        Scanner sc = new Scanner(System.in);
        String usernameAd;
        System.out.print("Please type your username: ");
        while (true) {
            usernameAd = sc.nextLine();
            if (checkAdminUsernameExisted(usernameAd)) break;
            else {
                System.out.println("This username is not existed. Please try with another one.");
            }
        }
        String passwordAd;
        System.out.print("Please type your password: ");
        while (true) {
            passwordAd = sc.nextLine();
            if (checkAdminPasswordExisted(passwordAd)) break;
            else {
                System.out.println("This password is not existed. Please try with another one.");
            }
        }
        // Create a scanner object to read from an admin text file.
        Scanner scannerAdmin = new Scanner(new File("./src/File/admin.txt"));
        // Continue to loop through each line of admin.txt file to find the username and password of admin.
        while (scannerAdmin.hasNextLine()) {
            String currentAdmin = scannerAdmin.nextLine();
            String[] currentAdminInfo = currentAdmin.split(",");
            String currentAdminUsername = currentAdminInfo[0];
            String currentAdminPassword = currentAdminInfo[1];
            // in case the users input are matched, completed this function.
            if (currentAdminUsername.equals(usernameAd) && currentAdminPassword.equals(passwordAd)) {
                // Prompt user a successful message
                System.out.println("LOGIN SUCCESSFULLY");
                System.out.println("-------------------");
                scannerAdmin.close();
                return true;
            }
        }
        // In case the users input are not matched, prompt user an unsuccessful message.
        System.out.println("Login unsuccessfully, please check your username and password and try again");
        scannerAdmin.close();
        return false;
    }
    public static boolean checkAdminUsernameExisted(String username) throws IOException {
        Scanner scannerAdmin = new Scanner(new File("./src/File/admin.txt"));

        while (scannerAdmin.hasNextLine()) {
            String currentAdmin = scannerAdmin.nextLine();
            String[] currentAdminInfo = currentAdmin.split(",");
            String currentAdminUsername = currentAdminInfo[0];

            if (username.equals(currentAdminUsername)) {
                return true;
            }
        }
        scannerAdmin.close();
        return false;
    }

    public static boolean checkAdminPasswordExisted(String password) throws IOException {
        Scanner scannerAdmin = new Scanner(new File("./src/File/admin.txt"));

        while (scannerAdmin.hasNextLine()) {
            String currentAdmin = scannerAdmin.nextLine();
            String[] currentAdminInfo = currentAdmin.split(",");
            String currentAdminPassword = currentAdminInfo[1];

            if (password.equals(currentAdminPassword)) {
                return true;
            }
        }
        scannerAdmin.close();
        return false;
    }
    public void viewProduct() throws IOException {
        // Scan items file
        Scanner sc = new Scanner(new File("./src/File/items.txt"));
        // Loop through items file
        while (sc.hasNextLine()) {
            String item = sc.nextLine();
            Product product = Product.generateProduct(item);
            product.getProductDetails();
        }
    }

    // This method is used to view orders for admin.
    public void viewOrders() throws IOException {
        // Create a scanner object to read from an item text file.
        Scanner sc = new Scanner(new File("./src/File/orders.txt"));
        System.out.println("\nVIEW ORDER");
        // A loop is used to display detailed information of each order.
        while (sc.hasNextLine()) {
            String orderInfo = sc.nextLine();
            Order order = Order.generateOrder(orderInfo);
            order.displayOrderInfo();
        }
        sc.close();
    }

    // Rewrite
    public void viewMembers() throws IOException {
        System.out.println("\nVIEW MEMBERS \n");
        // Create a scanner object to read from a member text file.
        Scanner sc = new Scanner(new File("./src/File/customers.txt"));
        // A loop is used to display detailed information of each member.
        while (sc.hasNextLine()) {
            String memberInfo = sc.nextLine();
            Customer member = Customer.generateCus(memberInfo);
            if (member.getMembership() != "Normal") {
                member.displayAccountInfo();
            }
        }
        sc.close();
    }

    public void addProduct() throws IOException{
        PrintWriter pw = new PrintWriter(new FileWriter("./src/File/items.txt", true));
        // Get new product information
        System.out.println("\nADDING NEW PRODUCT\n");
        Product newProduct = Product.createProduct();
        // Add new item line to file
        pw.printf("\n%s,%s,%.1f,%s", newProduct.getId(), newProduct.getName(), newProduct.getPrice(), newProduct.getCategory());
        pw.close();
    }

    public void removeProduct() throws IOException{
        Scanner sc = new Scanner(System.in);
        System.out.println("What is name of the product you want to delete ?");
        String productName = sc.next();
        boolean matchProduct = false;
        // Add new file
        File removeItem = new File("./src/File/removeItems.txt");
        File itemFile = new File("./src/File/items.txt");
        Scanner fileScanner = new Scanner(itemFile);
        PrintWriter pw = new PrintWriter(new FileWriter(removeItem));

        // Initiate line number
        int line = 0;

        // Boolean variable if remove item matches line 1
        boolean matchedLine1 = false;

        // Loop through items file
        while (fileScanner.hasNextLine()) {
            String item = fileScanner.nextLine();

            // Add 1 to line every loop
            line++;

            // Get product from item line
            Product product = Product.generateProduct(item);

            // Check if product id equal remove item id, and line number is 1
            if (product.getName().equals(productName) && line == 1) {
                matchedLine1 = true;
                continue;

                // Check if product id equal remove item id,
            } else if (product.getName().equals(productName)) {
                continue;
            }

            // Boolean variable if line number is 1 or 2 with remove item in line 1, avoid adding new line
            boolean lineCheck = (line == 1 || (line == 2 && matchedLine1));

            // Avoid adding new line at the start of the file if line check is true
            pw.printf((lineCheck ? "" : "\n") + item);
        }

        fileScanner.close();
        pw.close();

        // Delete current item file
        itemFile.delete();

        // Rename remove item file to item file
        removeItem.renameTo(itemFile);

        // Print error if product name doesn't match
        if (Product.checkProductExisted(productName)) {
            System.out.println("Product doesn't exist.Please try again");
        }
    }

    public void updateProductPrice() throws IOException{
        Scanner sc = new Scanner(System.in);
        System.out.println("What is the product you want to update");
        String productName = sc.next();

        // Add new file
        File updatePrice = new File("./src/File/update_items.txt");
        File itemFile = new File("./src/File/items.txt");
        Scanner fileScanner = new Scanner(itemFile);
        PrintWriter pw = new PrintWriter(new FileWriter(updatePrice, true));

        // Loop through items file
        while (fileScanner.hasNextLine()) {
            String item = fileScanner.nextLine();
            Product product = Product.generateProduct(item);

            if (product.getName().equals(productName)) {

                // Check new price input
                double newPrice = InputValidator.getDoubleInput("New price of the product: ", "Product price must be of integer or double value");
                product.setPrice(newPrice);

                // Update item info line from new product
                item = Product.generateItem(product);
            }

            // If the item reach last line, don't add new line
            pw.printf(item + (fileScanner.hasNextLine() ? "\n" : ""));
        }
        fileScanner.close();
        pw.close();

        // Delete the item file
        itemFile.delete();
        // Rename remove item file to item file
        updatePrice.renameTo(itemFile);

        // Print error if product name doesn't match
        if (!Product.checkProductExisted(productName)) {
            System.out.println("Product doesn't exist.Please try again");
        }
    }
    public void addCategory() throws IOException{
        Scanner sc = new Scanner(System.in);
        System.out.println("What category do you want to add ?");
        String newCategory = sc.next();

        // Check if new category is in category list
        if (!Product.getCategoryList().contains(newCategory)) {
            Product.addCategory(newCategory);
        } else {
            System.out.println("Category is already available. Please try again");
        }
    }

    public void removeCategory() throws IOException {
        // Check if category exists
        Scanner sc = new Scanner(System.in);
        System.out.println("What category do you want to remove ?");
        String removeCategory = sc.next();
        boolean matchCategory = false;
        ArrayList<String> categoryList = Product.getCategoryList();

        // Check if remove category is in category list
        if (categoryList.contains(removeCategory)) {
            matchCategory = true;
            categoryList.remove(removeCategory);
        }

        // Update category in item file to None
        File update = new File("./src/File/update.txt");
        File items = new File("./src/File/items.txt");
        Scanner fileScanner = new Scanner(items);
        PrintWriter pw = new PrintWriter(new FileWriter(update, true));

        // Loop through items file
        while (fileScanner.hasNextLine()) {
            String item = fileScanner.nextLine();

            // Get product from item line
            Product product = Product.generateProduct(item);

            // Check if product category equal remove category
            if (product.getCategory().equals(removeCategory)) {
                product.setCategory("None");
                // Update category to item line
                item = Product.generateItem(product);
            }

            // If the item reach last line, don't add new line
            pw.printf(item + (fileScanner.hasNextLine() ? "\n" : ""));
        }

        fileScanner.close();
        pw.close();

        // Delete the items file
        items.delete();
        // Rename remove item file to item file
        update.renameTo(items);
        // Print error if category doesn't exist
        if (!matchCategory) {
            System.out.println("Category doesn't exist. Please try again.");
        }
    }

    public void removeCategory2() throws IOException {
        System.out.println("\nREMOVE CATEGORY\n");
        Scanner sc = new Scanner(System.in);
        System.out.println("Which category do you want to remove ?");
        String category = sc.next();
        sc.close();
        boolean matchCategory = false;
        ArrayList<String> categoryList = Product.getCategoryList();
        if (categoryList.contains(category)) {
            matchCategory = true;
            categoryList.remove(category);
        }

        File updateCategory = new File("./src/File/category.txt");
        File items = new File("./src/File/items.txt");
        Scanner fileScanner = new Scanner(items);
        PrintWriter pw = new PrintWriter(new FileWriter(updateCategory, true));
        while (fileScanner.hasNextLine()) {
            String item = fileScanner.nextLine();
            Product product = Product.generateProduct(item);
            if (product.getCategory().equals(category)) {
                product.setCategory("None");
                item = Product.generateItem(product);
            }
            pw.printf(item + (fileScanner.hasNextLine() ? "\n" : ""));
        }

        fileScanner.close();
        pw.close();

        items.delete();
        updateCategory.renameTo(items);

    }

    public void removeCustomer() throws IOException{
        Scanner sc = new Scanner(System.in);
        System.out.println("What is the ID of the customer you want to remove?");
        String id = sc.next();

        // Boolean variable if customer ID exists
        boolean matchCus = false;
        // Add new file
        File removeCus = new File("./src/File/removeCustomers.txt");
        File customerFile = new File("./src/File/customers.txt");
        Scanner fileScanner = new Scanner(customerFile);
        PrintWriter pw = new PrintWriter(new FileWriter(removeCus, true));
        // Initiate line number
        int line = 0;
        // Boolean variable if remove customer match line 1
        boolean matchedLine1 = false;

        // Loop through customer file
        while (fileScanner.hasNextLine()) {
            line++;
            String customerInfo = fileScanner.nextLine();
            Customer customer = Customer.generateCus(customerInfo);

            // Check if product id equal remove item id, and line number is 1
            if (customer.getID().equals(id) && line == 1) {
                matchedLine1 = true;
                matchCus = true;
                continue;

                // Check if product id equal remove item id,
            } else if (customer.getID().equals(id)) {
                matchCus = true;
                continue;
            }

            // Boolean variable if line number is 1 or 2 with remove item in line 1, avoid adding new line
            boolean lineCheck = (line == 1 || (line == 2 && matchedLine1));

            // Avoid adding new line at the start of the file if line check is true
            pw.printf((lineCheck ? "" : "\n") + customerInfo);
        }
        fileScanner.close();
        pw.close();
        // Delete the item file
        customerFile.delete();
        // Rename remove item file to item file
        removeCus.renameTo(customerFile);

        // Print error if customer ID doesn't exist
        if (!matchCus) {
            System.out.println("Customer ID doesn't exist. Please try again");
        }
    }


    public void getOrderByCustomerID() throws IOException {
        System.out.println("\nGET ORDER BY CUSTOMER ID");
        // Create a scanner object to be ready to get input information (customer ID) from users via keyboard.
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the Customer ID: ");
        String customerID = sc.nextLine();
        // Create a scanner object to read from an order text file.
        Scanner scannerOrder = new Scanner(new File("./src/File/orders.txt"));
        // A boolean value to check the item's name existed or not.
        boolean checkCustomerIDExisted = false;

        // Continue to loop through each line of order.txt file to find the ID of customers.
        while (scannerOrder.hasNextLine()) {
            String orderInfo = scannerOrder.nextLine();
            Order order = Order.generateOrder(orderInfo);
            // In case the input ID is equivalent to the customerID from file, the following function would be executed.
            if (order.getCustomerID().equals(customerID)) {
                order.displayOrderInfo();
                checkCustomerIDExisted = true;
            }
        }
        // In case the customer's id is not existed, prompt user a message.
        if (!checkCustomerIDExisted) {
            System.out.println("\nThis customer's id cannot found. Please try with another one.");
        }
        scannerOrder.close();
    }

    public void changeOrderStatus() throws IOException {
        System.out.println("\nCHANGE STATUS OF THE ORDER");

        // Get order id
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the order ID: ");
        String orderID = sc.nextLine();

        // Boolean variable if orderID exists
        boolean matchOrderID = false;

        // Update order status
        File changeStatus = new File("./src/File/change.txt");
        File orders = new File("src/File/orders.txt");
        Scanner fileScanner = new Scanner(orders);
        PrintWriter pw = new PrintWriter(new FileWriter(changeStatus, true));

        // Loop through order file
        while (fileScanner.hasNextLine()) {
            String orderInfo = fileScanner.nextLine();

            // Generate order from order info line
            Order order = Order.generateOrder(orderInfo);

            if (order.getOrderID().equals(orderID)) {
                matchOrderID = true;

                // Check order status is paid or not
                if (order.getStatus().equals("paid")) {
                    System.out.println("The order status is already paid");
                } else {

                    // Update status order if not
                    order.setStatus("paid");
                }
            }

            // If the order line reach last line, don't add new line
            pw.printf(order.generateOrderLine() + (fileScanner.hasNextLine() ? "\n" : ""));
        }

        fileScanner.close();
        pw.close();

        orders.delete();
        changeStatus.renameTo(orders);

        // In case the order's id is not existed, prompt user a message.
        if (!matchOrderID) {
            System.out.println("\nThis order's id cannot found. Please try with another one.");
        }
    }

    public void getHighestBoughtProduct() throws IOException{
        System.out.println("-".repeat(17));
        System.out.println("GET THE HIGHEST BOUGHT PRODUCT OF A CUSTOMER");
        System.out.println("-".repeat(17));
        Scanner sc = new Scanner(System.in);
        System.out.println("What is the ID of the customer you want to view ?");
        String id = sc.next();
        // If the id of the customer
        boolean matchCustomer = false;

        Scanner fileScanner = new Scanner(new File("./src/File/customers.txt"));
        // Loop through customer file
        while(fileScanner.hasNextLine()) {
            String customerInfo = fileScanner.nextLine();
            Customer customer = Customer.generateCus(customerInfo);

            // Get the highest bought product(s) and the quantity
            if (customer.getID().equals(id)) {
                matchCustomer = true;
                System.out.println("-".repeat(17));
                System.out.println("The highest bought item(s) of customer " + customer.getID() + ":");
                System.out.println(customer.getHighestBoughtProduct());
                System.out.println("The total number bought is " + customer.getHighestBoughtQuantity());
                System.out.println("-".repeat(17));
            }
        }

        // Print error if customer ID not matched
        if (!matchCustomer) {
            System.out.println("Customer ID not valid. Please try again");
        }
    }

    public static boolean calculateTotalRevenue(String date) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Scanner scannerOrder = new Scanner(new File("./src/File/orders.txt"));
        try {
            formatter.parse(date);
            double revenue = 0;
            while (scannerOrder.hasNextLine()) {
                String orderInfo = scannerOrder.nextLine();

                // Generate order from order info line
                Order order = Order.generateOrder(orderInfo);
                if (order.getOrderDate().equals(date)) {
                    double orderTotal = order.getPrice();
                    revenue += orderTotal;
                }
            }
            System.out.printf("\nThe total revenue in %s is %.2f\n", date, revenue);
            scannerOrder.close();
        }
        catch (Exception e) {
            System.out.println("\nInvalid input, please try with another one.");
        }
        return true;
    }

    public void getTotalRevenue() throws IOException {
        System.out.println("\nCALCULATE THE STORE TOTAL REVENUE IN A PARTICULAR DAY");
        Scanner scannerOrder = new Scanner(new File("./src/File/orders.txt"));
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter date(yyyy-MM-dd) that you want to calculate the revenue: ");
        String date = sc.nextLine();
        calculateTotalRevenue(date);
        scannerOrder.close();
    }

    public void checkOrderInfoInADay() throws IOException {
        System.out.println("\nCHECK THE INFORMATION OF ALL ORDERS EXECUTED IN A PARTICULAR DAY");
        Scanner scannerOrder = new Scanner(new File("./src/File/orders.txt"));
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the formatted date (dd/MM/yyyy) that you want to check the information of all orders: ");
        String date = sc.nextLine();
        boolean checkOrderExisted = false;

        while (scannerOrder.hasNextLine()) {
            String orderInfo = scannerOrder.nextLine();
            Order order = Order.generateOrder(orderInfo);

            if (order.getOrderDate().equals(date)) {
                order.displayOrderInfo();
                checkOrderExisted = true;
            }
        }
        if (!checkOrderExisted) {
            System.out.printf("There isn't any orders in %s, please try with another date", date);
        }
    }
    public static void main(String[] args) throws IOException {
        Admin admin2 = new Admin();
//        admin2.removeCategory();
//        admin2.removeCustomer();
//        admin2.changeOrderStatus();
//        admin2.getOrderByCustomerID();
        admin2.checkOrderInfoInADay();
    }
}
