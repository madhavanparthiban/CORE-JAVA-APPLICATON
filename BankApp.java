// Account.java
import java.util.*;


class BankApp {
    private static Bank bank = new Bank();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        bank.registerUser("user1", "John Doe", "password123");
        bank.registerUser("user2", "Jane Smith", "password456");

        boolean running = true;
        while (running) {
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    loginUser();
                    break;
                case 2:
                    registerUser();
                    break;
                case 3:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void loginUser() {
        System.out.print("Enter user ID: ");
        String userId = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = bank.loginUser(userId, password);
        if (user != null) {
            boolean userRunning = true;
            while (userRunning) {
                System.out.println("1. View Accounts");
                System.out.println("2. Create Account");
                System.out.println("3. Withdraw");
                System.out.println("4. Deposit");
                System.out.println("5. View Transactions");
                System.out.println("6. Logout");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        user.printAccounts();
                        break;
                    case 2:
                        createAccount(user);
                        break;
                    case 3:
                        performTransaction(user, "withdraw");
                        break;
                    case 4:
                        performTransaction(user, "deposit");
                        break;
                    case 5:
                        user.printAccountStatements();
                        break;
                    case 6:
                        userRunning = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        }
    }

    private static void registerUser() {
        System.out.print("Enter new user ID: ");
        String userId = scanner.nextLine();
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        bank.registerUser(userId, name, password);
    }

    private static void createAccount(User user) {
        System.out.print("Enter account type (savings/checking): ");
        String type = scanner.nextLine();
        System.out.print("Enter initial balance: ");
        double balance = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        String accountNumber = "ACC" + System.currentTimeMillis(); // Generate a unique account number
        Account account = null;

        if (type.equalsIgnoreCase("savings")) {
            account = new SavingsAccount(accountNumber, balance, user.getName());
        } else if (type.equalsIgnoreCase("checking")) {
            account = new CheckingAccount(accountNumber, balance, user.getName());
        } else {
            System.out.println("Invalid account type.");
            return;
        }

        user.addAccount(account);
        System.out.println("Account created successfully.");
    }

    private static void performTransaction(User user, String transactionType) {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        Account account = user.getAccount(accountNumber);

        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        switch (transactionType.toLowerCase()) {
            case "withdraw":
                account.withdraw(amount);
                break;
            case "deposit":
                account.deposit(amount);
                break;
            default:
                System.out.println("Invalid transaction type.");
        }
    }
}

abstract class Account {
    private String accountNumber;
    private double balance;
    private String ownerName;
    private List<String> transactionHistory;

    public Account(String accountNumber, double balance, String ownerName) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.ownerName = ownerName;
        this.transactionHistory = new ArrayList<>();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            transactionHistory.add("Deposited $" + amount);
        }
    }

    public abstract void withdraw(double amount);

    public String getOwnerName() {
        return ownerName;
    }

    public List<String> getTransactionHistory() {
        return transactionHistory;
    }

    public void printTransactionHistory() {
        System.out.println("Transaction History for account " + accountNumber + ":");
        for (String transaction : transactionHistory) {
            System.out.println(transaction);
        }
    }

    @Override
    public String toString() {
        return "Account Number: " + accountNumber + ", Balance: $" + balance + ", Owner: " + ownerName;
    }
}

// SavingsAccount
class SavingsAccount extends Account {
    private static final double MIN_BALANCE = 500.0;

    public SavingsAccount(String accountNumber, double balance, String ownerName) {
        super(accountNumber, balance, ownerName);
    }

    @Override
    public void withdraw(double amount) {
        if (getBalance() - amount >= MIN_BALANCE) {
            deposit(-amount);
            getTransactionHistory().add("Withdrew $" + amount);
        } else {
            System.out.println("Insufficient funds or below minimum balance.");
        }
    }
}

// CheckingAccount
class CheckingAccount extends Account {
    private static final double OVERDRAFT_LIMIT = 1000.0;

    public CheckingAccount(String accountNumber, double balance, String ownerName) {
        super(accountNumber, balance, ownerName);
    }

    @Override
    public void withdraw(double amount) {
        if (getBalance() - amount >= -OVERDRAFT_LIMIT) {
            deposit(-amount);
            getTransactionHistory().add("Withdrew $" + amount);
        } else {
            System.out.println("Overdraft limit exceeded.");
        }
    }
}








// User

class User {
    private String userId;
    private String name;
    private String password;
    private Map<String, Account> accounts;

    public User(String userId, String name, String password) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.accounts = new HashMap<>();
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }

    public void addAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public void removeAccount(String accountNumber) {
        accounts.remove(accountNumber);
    }

    public void printAccounts() {
        for (Account account : accounts.values()) {
            System.out.println(account);
        }
    }

    public void printAccountStatements() {
        for (Account account : accounts.values()) {
            System.out.println("Statement for " + account.getAccountNumber() + ":");
            account.printTransactionHistory();
        }
    }
}




// Bank

 class Bank {
    private Map<String, User> users = new HashMap<>();

    public void registerUser(String userId, String name, String password) {
        if (!users.containsKey(userId)) {
            users.put(userId, new User(userId, name, password));
            System.out.println("User registered successfully.");
        } else {
            System.out.println("User already exists.");
        }
    }

    public User loginUser(String userId, String password) {
        User user = users.get(userId);
        if (user != null && user.validatePassword(password)) {
            return user;
        } else {
            System.out.println("Invalid user ID or password.");
            return null;
        }
    }

    public void printAllUsers() {
        for (User user : users.values()) {
            System.out.println("User ID: " + user.getUserId() + ", Name: " + user.getName());
        }
    }
}


