package model;

import java.io.*;
import java.util.*;

/**
 * Handles all file operations for the application.
 * Responsible for saving and loading:
 * transactions, budgets, categories, notifications, and users.
 */
public class DataManager {

    private static final String TRANSACTIONS_FILE = "transactions.csv";
    private static final String BUDGETS_FILE = "budgets.csv";
    private static final String CATEGORIES_FILE = "categories.csv";
    private static final String NOTIFICATIONS_FILE = "notifications.csv";
    private static final String USERS_FILE = "users.csv";

    /**
     * Saves all transactions to file.
     *
     * @param transactions list of transactions to save
     */
    public static void saveTransactions(List<Transaction> transactions) {
        try {
            FileWriter writer = new FileWriter(TRANSACTIONS_FILE);
            for (Transaction t : transactions) {
                if (t instanceof Income) {
                    Income inc = (Income) t;
                    writer.write(t.getID() + "," + t.getAmount() + "," +
                            t.getDate() + "," + t.getDescription() + "," +
                            t.getCategory() + "," + t.getType() + "," +
                            t.getPaymentMethod() + "," + inc.getSource() + "," +
                            t.getUserEmail() + "\n");
                } else if (t instanceof Expense) {
                    Expense exp = (Expense) t;
                    writer.write(t.getID() + "," + t.getAmount() + "," +
                            t.getDate() + "," + t.getDescription() + "," +
                            t.getCategory() + "," + t.getType() + "," +
                            t.getPaymentMethod() + "," + exp.getNotes() + "," +
                            t.getUserEmail() + "\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    /**
     * Loads transactions for a specific user.
     *
     * @param email user email filter
     * @return list of user's transactions
     */
    public static List<Transaction> loadTransactions(String email) {
        List<Transaction> transactions = new ArrayList<>();
        try {
            File file = new File(TRANSACTIONS_FILE);
            if (!file.exists()) return transactions;

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 9) continue;

                int id = Integer.parseInt(parts[0]);
                double amount = Double.parseDouble(parts[1]);
                String date = parts[2];
                String description = parts[3];
                String category = parts[4];
                String type = parts[5];
                String paymentMethod = parts[6];
                String extra = parts[7];
                String userEmail = parts[8];

                if (!userEmail.equals(email)) continue;

                if (type.equals("Income")) {
                    transactions.add(new Income(id, amount, date,
                            description, category, type, paymentMethod, extra, userEmail));
                } else {
                    transactions.add(new Expense(id, amount, date,
                            description, category, type, paymentMethod, extra, userEmail));
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
        return transactions;
    }

    /**
     * Saves all budgets to file.
     *
     * @param budgets list of budgets
     */
    public static void saveBudgets(List<Budget> budgets) {
        try {
            FileWriter writer = new FileWriter(BUDGETS_FILE);
            for (Budget b : budgets) {
                writer.write(b.getId() + "," + b.getCategory() + "," +
                        b.getLimitAmount() + "," + b.getStartDate() + "," +
                        b.getEndDate() + "," + b.getAlertThreshold() + "," +
                        b.getCurrentSpending() + "," + b.getStatus() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving budgets: " + e.getMessage());
        }
    }

    /**
     * Loads all budgets from file.
     *
     * @return list of budgets
     */
    public static List<Budget> loadBudgets() {
        List<Budget> budgets = new ArrayList<>();
        try {
            File file = new File(BUDGETS_FILE);
            if (!file.exists()) return budgets;

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 8) continue;

                int id = Integer.parseInt(parts[0]);
                String category = parts[1];
                double limitAmount = Double.parseDouble(parts[2]);
                String startDate = parts[3];
                String endDate = parts[4];
                int alertThreshold = Integer.parseInt(parts[5]);
                double currentSpending = Double.parseDouble(parts[6]);
                String status = parts[7];

                budgets.add(new Budget(id, category, limitAmount,
                        startDate, endDate, alertThreshold,
                        currentSpending, status));
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading budgets: " + e.getMessage());
        }
        return budgets;
    }

    /**
     * Saves all categories to file.
     *
     * @param categories list of categories
     */
    public static void saveCategories(List<Category> categories) {
        try {
            FileWriter writer = new FileWriter(CATEGORIES_FILE);
            for (Category c : categories) {
                writer.write(c.getID() + "," + c.getName() + "," +
                        c.isCustom() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving categories: " + e.getMessage());
        }
    }

    /**
     * Loads categories from file.
     * If file doesn't exist, default categories are created.
     *
     * @return list of categories
     */
    public static List<Category> loadCategories() {
        List<Category> categories = new ArrayList<>();
        try {
            File file = new File(CATEGORIES_FILE);
            if (!file.exists()) {
                categories.add(new Category(1, "Food", false));
                categories.add(new Category(2, "Transport", false));
                categories.add(new Category(3, "Entertainment", false));
                categories.add(new Category(4, "Bills", false));
                categories.add(new Category(5, "Health", false));
                return categories;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) continue;

                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                boolean isCustom = Boolean.parseBoolean(parts[2]);

                categories.add(new Category(id, name, isCustom));
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading categories: " + e.getMessage());
        }
        return categories;
    }

    /**
     * Generates a new unique transaction ID.
     */
    public static int generateId(List<Transaction> transactions) {
        int maxId = 0;
        for (Transaction t : transactions) {
            if (t.getID() > maxId) maxId = t.getID();
        }
        return maxId + 1;
    }

    /**
     * Generates a new unique budget ID.
     */
    public static int generateBudgetId(List<Budget> budgets) {
        int maxId = 0;
        for (Budget b : budgets) {
            if (b.getId() > maxId) maxId = b.getId();
        }
        return maxId + 1;
    }

    // ---------------- Notifications ----------------

    /** Saves notifications to file */
    public static void saveNotifications(List<Notification> notifications) {
        try {
            FileWriter writer = new FileWriter(NOTIFICATIONS_FILE);
            for (Notification n : notifications) {
                writer.write(n.getId() + "," + n.getUserId() + "," +
                        n.getType() + "," + n.isRead() + "," +
                        n.getTimestamp().getTime() + "," +
                        n.getMessage() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving notifications: " + e.getMessage());
        }
    }

    /** Loads notifications from file */
    public static List<Notification> loadNotifications() {
        List<Notification> notifications = new ArrayList<>();
        try {
            File file = new File(NOTIFICATIONS_FILE);
            if (!file.exists()) return notifications;

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 6);
                if (parts.length < 6) continue;

                int id = Integer.parseInt(parts[0]);
                int userId = Integer.parseInt(parts[1]);
                String type = parts[2];
                boolean read = Boolean.parseBoolean(parts[3]);
                long ts = Long.parseLong(parts[4]);
                String msg = parts[5];

                Notification n = new Notification(id, userId, type, msg);
                n.setRead(read);
                n.setTimestamp(new Date(ts));

                notifications.add(n);
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading notifications: " + e.getMessage());
        }
        return notifications;
    }

    /** Generates a new notification ID */
    public static int generateNotificationId(List<Notification> notifications) {
        int maxId = 0;
        for (Notification n : notifications) {
            if (n.getId() > maxId) maxId = n.getId();
        }
        return maxId + 1;
    }

    // ---------------- Users ----------------

    /** Saves a new user to file */
    public static void saveUser(User u) {
        try {
            FileWriter writer = new FileWriter(USERS_FILE, true);
            writer.write(u.getEmail() + "," +
                    u.getPassword() + "," +
                    u.getFirstName() + "," +
                    u.getLastName() + "\n");
            writer.close();
        } catch (Exception e) {
            System.out.println("Error saving user: " + e.getMessage());
        }
    }

    /** Validates login credentials */
    public static boolean validateLogin(String email, String password) {
        try {
            File file = new File(USERS_FILE);
            if (!file.exists()) return false;

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length >= 2) {
                    if (parts[0].equals(email) && parts[1].equals(password)) {
                        br.close();
                        return true;
                    }
                }
            }

            br.close();
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return false;
    }
}
