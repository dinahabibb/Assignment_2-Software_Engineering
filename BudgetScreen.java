import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BudgetScreen extends JFrame {

    private JComboBox<String> categoryComboBox;
    private JTextField limitAmountField;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextField alertThresholdField;
    private JButton saveButton;
    private JButton clearButton;
    private JButton deleteButton;
    private JTable budgetTable;
    private DefaultTableModel tableModel;
    private JProgressBar spendingBar;
    private JLabel statusLabel;
    private JLabel remainingLabel;
    private List<Budget> budgets;
    private List<Transaction> transactions;
    private int selectedBudgetIndex = -1;

    public BudgetScreen() {
        budgets = DataManager.loadBudgets();
        transactions = DataManager.loadTransactions();
        setTitle("Budget Screen");
        setSize(850, 700);
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

        JPanel middlePanel = new JPanel(new GridLayout(1, 2, 10, 10));
        middlePanel.add(buildFormPanel());
        middlePanel.add(buildStatusPanel());
        mainPanel.add(middlePanel, BorderLayout.CENTER);
        mainPanel.add(buildButtonPanel(), BorderLayout.SOUTH);

        JPanel fullPanel = new JPanel(new BorderLayout(10, 10));
        fullPanel.add(mainPanel, BorderLayout.NORTH);
        fullPanel.add(buildTablePanel(), BorderLayout.CENTER);
        add(fullPanel);
    }

    private JPanel buildTitlePanel() {
        JPanel panel = new JPanel();
        JLabel title = new JLabel("Budget Management");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(title);
        return panel;
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Budget Details"));

        panel.add(new JLabel("Category:"));
        categoryComboBox = new JComboBox<>();
        loadCategories();
        panel.add(categoryComboBox);

        panel.add(new JLabel("Budget Limit:"));
        limitAmountField = new JTextField();
        panel.add(limitAmountField);

        panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        startDateField = new JTextField();
        startDateField.setText(getFirstDayOfMonth());
        panel.add(startDateField);

        panel.add(new JLabel("End Date (YYYY-MM-DD):"));
        endDateField = new JTextField();
        endDateField.setText(getLastDayOfMonth());
        panel.add(endDateField);

        panel.add(new JLabel("Alert at (% of limit):"));
        alertThresholdField = new JTextField();
        alertThresholdField.setText("80");
        panel.add(alertThresholdField);

        return panel;
    }

    private JPanel buildStatusPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Budget Status"));

        statusLabel = new JLabel("Status: No budget selected");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(statusLabel);

        remainingLabel = new JLabel("Remaining: -");
        panel.add(remainingLabel);

        panel.add(new JLabel("Spending Progress:"));

        spendingBar = new JProgressBar(0, 100);
        spendingBar.setStringPainted(true);
        spendingBar.setString("0%");
        panel.add(spendingBar);

        JLabel instruction = new JLabel("Click a budget to see status");
        instruction.setForeground(Color.GRAY);
        panel.add(instruction);

        return panel;
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
        deleteButton.addActionListener(e -> deleteSelectedBudget());
        panel.add(deleteButton);

        saveButton = new JButton("Save Budget");
        saveButton.setPreferredSize(new Dimension(150, 35));
        saveButton.setBackground(new Color(0, 120, 215));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveBudget());
        panel.add(saveButton);

        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Your Budgets"));

        String[] columns = {"ID", "Category", "Limit", "Spent",
                            "Remaining", "Start Date", "End Date", "Status"};

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        budgetTable = new JTable(tableModel);
        budgetTable.setRowHeight(25);
        budgetTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        budgetTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = budgetTable.getSelectedRow();
                if (row >= 0 && row < budgets.size()) {
                    selectedBudgetIndex = row;
                    updateStatusPanel(budgets.get(row));
                    fillFormWithBudget(budgets.get(row));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(budgetTable);
        scrollPane.setPreferredSize(new Dimension(800, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void saveBudget() {
        String category = (String) categoryComboBox.getSelectedItem();
        String limitText = limitAmountField.getText().trim();
        String startDate = startDateField.getText().trim();
        String endDate = endDateField.getText().trim();
        String alertText = alertThresholdField.getText().trim();

        if (limitText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a budget limit!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (startDate.isEmpty() || endDate.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter start and end dates!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double limitAmount;
        try {
            limitAmount = Double.parseDouble(limitText);
            if (limitAmount <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Budget limit must be greater than 0!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid number for limit!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int alertThreshold;
        try {
            alertThreshold = Integer.parseInt(alertText);
            if (alertThreshold < 1 || alertThreshold > 100) {
                JOptionPane.showMessageDialog(this,
                        "Alert must be between 1 and 100!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid number for alert!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedBudgetIndex >= 0) {
            for (int i = 0; i < budgets.size(); i++) {
                if (i != selectedBudgetIndex &&
                        budgets.get(i).getCategory().equals(category)) {
                    JOptionPane.showMessageDialog(this,
                            "A budget for " + category + " already exists!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Budget existing = budgets.get(selectedBudgetIndex);
            double keepSpending = existing.getCurrentSpending();

            Budget updated = new Budget(existing.getId(), category,
                    limitAmount, startDate, endDate, alertThreshold,
                    keepSpending, existing.getStatus());

            budgets.set(selectedBudgetIndex, updated);

            JOptionPane.showMessageDialog(this,
                    "Budget updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Budget b : budgets) {
                if (b.getCategory().equals(category)) {
                    JOptionPane.showMessageDialog(this,
                            "A budget for " + category +
                            " already exists!\nPlease edit it instead.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            double currentSpending = calculateSpending(category);
            int newId = DataManager.generateBudgetId(budgets);

            Budget newBudget = new Budget(newId, category, limitAmount,
                    startDate, endDate, alertThreshold,
                    currentSpending, "On Track");

            if (newBudget.isExceeded()) {
                JOptionPane.showMessageDialog(this,
                        "Warning: spending already exceeds this limit!",
                        "Budget Exceeded", JOptionPane.WARNING_MESSAGE);
            } else if (newBudget.isNearLimit()) {
                JOptionPane.showMessageDialog(this,
                        "Warning: spending is near this limit!",
                        "Near Limit", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Budget created successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            budgets.add(newBudget);
        }

        DataManager.saveBudgets(budgets);
        refreshTable();
        clearForm();
        selectedBudgetIndex = -1;
    }

    private void deleteSelectedBudget() {
        if (selectedBudgetIndex < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a budget to delete!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this budget?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            budgets.remove(selectedBudgetIndex);
            DataManager.saveBudgets(budgets);
            refreshTable();
            clearForm();
            selectedBudgetIndex = -1;
            resetStatusPanel();
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Budget b : budgets) {
            tableModel.addRow(new Object[]{
                b.getId(), b.getCategory(), b.getLimitAmount(),
                b.getCurrentSpending(), b.getRemainingAmount(),
                b.getStartDate(), b.getEndDate(), b.getStatus()
            });
        }
    }

    private void updateStatusPanel(Budget budget) {
        int percentage = 0;
        if (budget.getLimitAmount() > 0) {
            percentage = (int) ((budget.getCurrentSpending() /
                    budget.getLimitAmount()) * 100);
        }
        if (percentage > 100) percentage = 100;

        spendingBar.setValue(percentage);
        spendingBar.setString(percentage + "%");

        if (budget.isExceeded()) {
            spendingBar.setForeground(new Color(220, 50, 50));
            statusLabel.setText("Status: EXCEEDED");
            statusLabel.setForeground(new Color(220, 50, 50));
        } else if (budget.isNearLimit()) {
            spendingBar.setForeground(new Color(255, 165, 0));
            statusLabel.setText("Status: Near Limit");
            statusLabel.setForeground(new Color(255, 165, 0));
        } else {
            spendingBar.setForeground(new Color(0, 180, 0));
            statusLabel.setText("Status: On Track");
            statusLabel.setForeground(new Color(0, 180, 0));
        }

        remainingLabel.setText("Remaining: " + budget.getRemainingAmount());
    }

    private void fillFormWithBudget(Budget budget) {
        categoryComboBox.setSelectedItem(budget.getCategory());
        limitAmountField.setText(String.valueOf(budget.getLimitAmount()));
        startDateField.setText(budget.getStartDate());
        endDateField.setText(budget.getEndDate());
        alertThresholdField.setText(String.valueOf(budget.getAlertThreshold()));
    }

    private void resetStatusPanel() {
        spendingBar.setValue(0);
        spendingBar.setString("0%");
        statusLabel.setText("Status: No budget selected");
        statusLabel.setForeground(Color.BLACK);
        remainingLabel.setText("Remaining: -");
    }

    private double calculateSpending(String category) {
        double total = 0;
        for (Transaction t : transactions) {
            if (t instanceof Expense && t.getCategory().equals(category)) {
                total += t.getAmount();
            }
        }
        return total;
    }

    private void clearForm() {
        limitAmountField.setText("");
        startDateField.setText(getFirstDayOfMonth());
        endDateField.setText(getLastDayOfMonth());
        alertThresholdField.setText("80");
        categoryComboBox.setSelectedIndex(0);
        selectedBudgetIndex = -1;
        resetStatusPanel();
        budgetTable.clearSelection();
    }

    private void loadCategories() {
        List<Category> categories = DataManager.loadCategories();
        for (Category c : categories) {
            categoryComboBox.addItem(c.getName());
        }
    }

    private String getFirstDayOfMonth() {
        java.time.LocalDate today = java.time.LocalDate.now();
        return today.withDayOfMonth(1).toString();
    }

    private String getLastDayOfMonth() {
        java.time.LocalDate today = java.time.LocalDate.now();
        return today.withDayOfMonth(today.lengthOfMonth()).toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BudgetScreen());
    }
}