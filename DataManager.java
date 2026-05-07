import java.io.*;
import java.util.*;

public class DataManager {

    private static final String TRANSACTIONS_FILE = "transactions.csv";
    private static final String BUDGETS_FILE = "budgets.csv";
    private static final String CATEGORIES_FILE = "categories.csv";

    public static void saveTransactions(List<Transaction> transactions) {
        try {
            FileWriter writer = new FileWriter(TRANSACTIONS_FILE);
            for (Transaction t : transactions) {
                if (t instanceof Income) {
                    Income inc = (Income) t;
                    writer.write(t.getID() + "," + t.getAmount() + "," +
                            t.getDate() + "," + t.getDescription() + "," +
                            t.getCategory() + "," + t.getType() + "," +
                            t.getPaymentMethod() + "," + inc.getSource() + "\n");
                } else if (t instanceof Expense) {
                    Expense exp = (Expense) t;
                    writer.write(t.getID() + "," + t.getAmount() + "," +
                            t.getDate() + "," + t.getDescription() + "," +
                            t.getCategory() + "," + t.getType() + "," +
                            t.getPaymentMethod() + "," + exp.getNotes() + "\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    public static List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try {
            File file = new File(TRANSACTIONS_FILE);
            if (!file.exists()) return transactions;

            BufferedReader reader = new BufferedReader(
                    new FileReader(TRANSACTIONS_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 8) continue;

                int id = Integer.parseInt(parts[0]);
                double amount = Double.parseDouble(parts[1]);
                String date = parts[2];
                String description = parts[3];
                String category = parts[4];
                String type = parts[5];
                String paymentMethod = parts[6];
                String extra = parts[7];

                if (type.equals("Income")) {
                    transactions.add(new Income(id, amount, date,
                            description, category, type, paymentMethod, extra));
                } else {
                    transactions.add(new Expense(id, amount, date,
                            description, category, type, paymentMethod, extra));
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
        return transactions;
    }

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

    public static List<Budget> loadBudgets() {
        List<Budget> budgets = new ArrayList<>();
        try {
            File file = new File(BUDGETS_FILE);
            if (!file.exists()) return budgets;

            BufferedReader reader = new BufferedReader(
                    new FileReader(BUDGETS_FILE));
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

                Budget b = new Budget(id, category, limitAmount,
                        startDate, endDate, alertThreshold,
                        currentSpending, status);
                budgets.add(b);
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading budgets: " + e.getMessage());
        }
        return budgets;
    }

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

            BufferedReader reader = new BufferedReader(
                    new FileReader(CATEGORIES_FILE));
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

    public static int generateId(List<Transaction> transactions) {
        if (transactions.isEmpty()) return 1;
        int maxId = 0;
        for (Transaction t : transactions) {
            if (t.getID() > maxId) maxId = t.getID();
        }
        return maxId + 1;
    }

    public static int generateBudgetId(List<Budget> budgets) {
        if (budgets.isEmpty()) return 1;
        int maxId = 0;
        for (Budget b : budgets) {
            if (b.getId() > maxId) maxId = b.getId();
        }
        return maxId + 1;
    }
}