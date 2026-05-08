package UI;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Model.Budget;
import Model.Category;
import Model.DataManager;
import Model.Expense;
import Model.Income;
import Model.Notification;
import Model.Transaction;

import java.awt.*;
import java.util.List;

public class TransactionScreen extends JFrame {

    private JTextField amountField;
    private JTextField dateField;
    private JTextField descriptionField;
    private JTextField sourceNotesField;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> paymentComboBox;
    private JLabel sourceNotesLabel;
    private JButton saveButton;
    private JButton clearButton;
    private JButton deleteButton;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private List<Transaction> transactions;
    private int selectedTransactionIndex = -1;

    public TransactionScreen() {
        transactions = DataManager.loadTransactions();
        setTitle("Transaction Screen");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildScreen();
        refreshTable();
        setVisible(true);
    }

    private void buildScreen() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.add(buildTitlePanel(), BorderLayout.NORTH);
        mainPanel.add(buildFormPanel(), BorderLayout.CENTER);
        mainPanel.add(buildButtonPanel(), BorderLayout.SOUTH);

        JPanel fullPanel = new JPanel(new BorderLayout(10, 10));
        fullPanel.add(buildNavBar(), BorderLayout.NORTH);
        fullPanel.add(mainPanel, BorderLayout.CENTER);
        fullPanel.add(buildTablePanel(), BorderLayout.SOUTH);
        add(fullPanel);
    }

    // Navigation bar linking all 4 screens (Person 3 integration)
    private JPanel buildNavBar() {
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        nav.setBackground(new Color(230, 240, 255));
        nav.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JLabel appLabel = new JLabel("  BudgetApp: ");
        appLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nav.add(appLabel);

        JButton btnTransactions = new JButton("Transactions");
        btnTransactions.setBackground(new Color(0, 120, 215));
        btnTransactions.setForeground(Color.WHITE);
        btnTransactions.setFocusPainted(false);
        btnTransactions.setEnabled(false); 
        nav.add(btnTransactions);

        JButton btnBudget = new JButton("Budgets");
        btnBudget.setFocusPainted(false);
        btnBudget.addActionListener(e -> {
            new BudgetScreen();
            dispose();
        });
        nav.add(btnBudget);

        JButton btnAlerts = new JButton("Alerts");
        btnAlerts.setFocusPainted(false);
        btnAlerts.addActionListener(e -> openAlertsWindow());
        nav.add(btnAlerts);

        JButton btnGoals = new JButton("Goals");
        btnGoals.setFocusPainted(false);
        btnGoals.addActionListener(e -> openGoalsWindow());
        nav.add(btnGoals);

        return nav;
    }

    private void openAlertsWindow() {
        JFrame frame = new JFrame("Alerts & Notifications");
        frame.setSize(700, 550);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        AlertsScreen alertsScreen = new AlertsScreen();
        frame.add(alertsScreen);
        frame.setVisible(true);
    }

    private void openGoalsWindow() {
        JFrame frame = new JFrame("Financial Goals");
        frame.setSize(800, 550);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GoalScreen goalScreen = new GoalScreen();
        frame.add(goalScreen);
        frame.setVisible(true);
    }

    private JPanel buildTitlePanel() {
        JPanel panel = new JPanel();
        JLabel title = new JLabel("Add New Transaction");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(title);
        return panel;
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Transaction Details"));

        panel.add(new JLabel("Type:"));
        typeComboBox = new JComboBox<>();
        typeComboBox.addItem("Income");
        typeComboBox.addItem("Expense");
        typeComboBox.addActionListener(e -> updateSourceNotesLabel());
        panel.add(typeComboBox);

        panel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        panel.add(amountField);

        panel.add(new JLabel("Category:"));
        categoryComboBox = new JComboBox<>();
        loadCategories();
        panel.add(categoryComboBox);

        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        dateField.setText(getTodayDate());
        panel.add(dateField);

        panel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        panel.add(descriptionField);

        panel.add(new JLabel("Payment Method:"));
        paymentComboBox = new JComboBox<>();
        paymentComboBox.addItem("Cash");
        paymentComboBox.addItem("Credit Card");
        paymentComboBox.addItem("Bank Transfer");
        paymentComboBox.addItem("Debit Card");
        panel.add(paymentComboBox);

        sourceNotesLabel = new JLabel("Source:");
        panel.add(sourceNotesLabel);
        sourceNotesField = new JTextField();
        panel.add(sourceNotesField);

        return panel;
    }

    private void updateSourceNotesLabel() {
        String type = (String) typeComboBox.getSelectedItem();
        if (type.equals("Income")) {
            sourceNotesLabel.setText("Source:");
        } else {
            sourceNotesLabel.setText("Notes:");
        }
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        clearButton = new JButton("Clear");
        clearButton.setPreferredSize(new Dimension(100, 35));
        clearButton.addActionListener(e -> clearForm());
        panel.add(clearButton);

        deleteButton = new JButton("Delete Selected");
        deleteButton.setPreferredSize(new Dimension(150, 35));
        deleteButton.setBackground(new Color(220, 50, 50));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> deleteTransaction());
        panel.add(deleteButton);

        saveButton = new JButton("Save Transaction");
        saveButton.setPreferredSize(new Dimension(150, 35));
        saveButton.setBackground(new Color(0, 120, 215));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveTransaction());
        panel.add(saveButton);

        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Transaction History"));

        String[] columns = {"ID", "Type", "Amount", "Category",
                            "Date", "Description", "Payment", "Source/Notes"};

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(tableModel);
        transactionTable.setRowHeight(25);
        transactionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        transactionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = transactionTable.getSelectedRow();
                if (row >= 0 && row < transactions.size()) {
                    selectedTransactionIndex = row;
                    fillFormWithTransaction(transactions.get(row));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setPreferredSize(new Dimension(800, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void fillFormWithTransaction(Transaction t) {
        amountField.setText(String.valueOf(t.getAmount()));
        dateField.setText(t.getDate());
        descriptionField.setText(t.getDescription());
        categoryComboBox.setSelectedItem(t.getCategory());
        typeComboBox.setSelectedItem(t.getType());
        paymentComboBox.setSelectedItem(t.getPaymentMethod());

        if (t instanceof Income) {
            sourceNotesField.setText(((Income) t).getSource());
        } else if (t instanceof Expense) {
            sourceNotesField.setText(((Expense) t).getNotes());
        }
        updateSourceNotesLabel();
    }

    private void saveTransaction() {
        String type = (String) typeComboBox.getSelectedItem();
        String amountText = amountField.getText().trim();
        String category = (String) categoryComboBox.getSelectedItem();
        String date = dateField.getText().trim();
        String description = descriptionField.getText().trim();
        String paymentMethod = (String) paymentComboBox.getSelectedItem();
        String sourceNotes = sourceNotesField.getText().trim();

        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an amount!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a date!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Amount must be greater than 0!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid number for amount!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedTransactionIndex >= 0) {
            Transaction existing = transactions.get(selectedTransactionIndex);
            int keepId = existing.getID();

            Transaction updated;
            if (type.equals("Income")) {
                updated = new Income(keepId, amount, date,
                        description, category, type, paymentMethod, sourceNotes);
            } else {
                updated = new Expense(keepId, amount, date,
                        description, category, type, paymentMethod, sourceNotes);
            }

            transactions.set(selectedTransactionIndex, updated);
            selectedTransactionIndex = -1;

            JOptionPane.showMessageDialog(this,
                    "Transaction updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int newId = DataManager.generateId(transactions);

            Transaction transaction;
            if (type.equals("Income")) {
                transaction = new Income(newId, amount, date,
                        description, category, type, paymentMethod, sourceNotes);
            } else {
                transaction = new Expense(newId, amount, date,
                        description, category, type, paymentMethod, sourceNotes);
            }

            transactions.add(transaction);

            JOptionPane.showMessageDialog(this,
                    "Transaction saved successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }

        DataManager.saveTransactions(transactions);
        updateBudgetsAfterTransaction();
        refreshTable();
        clearForm();
    }

    private void deleteTransaction() {
        if (selectedTransactionIndex < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a transaction to delete!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this transaction?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            transactions.remove(selectedTransactionIndex);
            DataManager.saveTransactions(transactions);
            updateBudgetsAfterTransaction();
            refreshTable();
            clearForm();
            selectedTransactionIndex = -1;
        }
    }

    private void updateBudgetsAfterTransaction() {
        List<Budget> budgets = DataManager.loadBudgets();

        // Recalculate spending for every budget category from all transactions
        for (int i = 0; i < budgets.size(); i++) {
            Budget b = budgets.get(i);
            double totalSpending = 0;
            for (Transaction t : transactions) {
                if (t instanceof Expense && t.getCategory().equals(b.getCategory())) {
                    totalSpending += t.getAmount();
                }
            }
            budgets.set(i, new Budget(b.getId(), b.getCategory(),
                    b.getLimitAmount(), b.getStartDate(), b.getEndDate(),
                    b.getAlertThreshold(), totalSpending, b.getStatus()));
        }
        DataManager.saveBudgets(budgets);

       
        Transaction last = transactions.isEmpty() ? null : transactions.get(transactions.size() - 1);
        if (last instanceof Expense) {
            checkAndNotify(last.getCategory());
        }
    }

 
    private void checkAndNotify(String category) {
        List<Budget> budgets = DataManager.loadBudgets();
        Budget matched = null;
        for (Budget b : budgets) {
            if (b.getCategory().equalsIgnoreCase(category)) { matched = b; break; }
        }
        if (matched == null) return;

        List<Notification> notifications = DataManager.loadNotifications();
        int newId = DataManager.generateNotificationId(notifications);
        Notification notif = null;

        if (matched.isExceeded()) {
            double over = -matched.getRemainingAmount();
            String msg = "Budget Exceeded — " + category + "! You've exceeded your " +
                    String.format("EGP %.2f", matched.getLimitAmount()) +
                    " budget by EGP " + String.format("%.2f", over) + ".";
            notif = new Notification(newId, 1, "BUDGET_EXCEEDED", msg);
            JOptionPane.showMessageDialog(this, msg, "Budget Exceeded!", JOptionPane.WARNING_MESSAGE);

        } else if (matched.isNearLimit()) {
            double pct = matched.getSpendingPercentage();
            String msg = "Budget Alert — " + category + ": You've used " +
                    String.format("%.1f", pct) + "% of your " + category + " budget.";
            notif = new Notification(newId, 1, "BUDGET_NEAR_LIMIT", msg);
            JOptionPane.showMessageDialog(this, msg, "Budget Near Limit", JOptionPane.INFORMATION_MESSAGE);
        }

        if (notif != null) {
            notifications.add(0, notif); 
            DataManager.saveNotifications(notifications); 
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Transaction t : transactions) {
            String sourceNotes = "";
            if (t instanceof Income) {
                sourceNotes = ((Income) t).getSource();
            } else if (t instanceof Expense) {
                sourceNotes = ((Expense) t).getNotes();
            }
            tableModel.addRow(new Object[]{
                t.getID(), t.getType(), t.getAmount(),
                t.getCategory(), t.getDate(), t.getDescription(),
                t.getPaymentMethod(), sourceNotes
            });
        }
    }

    private void clearForm() {
        amountField.setText("");
        descriptionField.setText("");
        sourceNotesField.setText("");
        dateField.setText(getTodayDate());
        typeComboBox.setSelectedIndex(0);
        categoryComboBox.setSelectedIndex(0);
        paymentComboBox.setSelectedIndex(0);
        updateSourceNotesLabel();
        selectedTransactionIndex = -1;
        transactionTable.clearSelection();
    }

    private void loadCategories() {
        List<Category> categories = DataManager.loadCategories();
        for (Category c : categories) {
            categoryComboBox.addItem(c.getName());
        }
    }

    private String getTodayDate() {
        java.time.LocalDate today = java.time.LocalDate.now();
        return today.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TransactionScreen());
    }
}