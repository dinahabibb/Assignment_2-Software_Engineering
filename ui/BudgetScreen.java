package ui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import model.Budget;
import model.Category;
import model.DataManager;
import model.Expense;
import model.Transaction;

import java.awt.*;
import java.util.List;

/**
 * A JPanel-based screen for managing budgets within the application.
 *
 * <p>This screen allows users to create, view, update, and delete budgets
 * associated with expense categories. It displays a form for entering budget
 * details, a status panel showing spending progress, and a table listing
 * all existing budgets. Budget data is persisted via {@link DataManager}.</p>
 *
 * <p>Features include:</p>
 * <ul>
 *   <li>Creating new budgets per category with a spending limit and date range</li>
 *   <li>Editing existing budgets by selecting them from the table</li>
 *   <li>Deleting selected budgets with confirmation</li>
 *   <li>Visual spending progress bar with color-coded status</li>
 *   <li>Alert threshold configuration to warn when nearing the budget limit</li>
 * </ul>
 */
public class BudgetScreen extends JPanel  {

    /** Dropdown for selecting the expense category for a budget. */
    private JComboBox<String> categoryComboBox;

    /** Input field for the maximum spending limit of the budget. */
    private JTextField limitAmountField;

    /** Input field for the budget start date in YYYY-MM-DD format. */
    private JTextField startDateField;

    /** Input field for the budget end date in YYYY-MM-DD format. */
    private JTextField endDateField;

    /** Input field for the percentage threshold at which an alert is triggered. */
    private JTextField alertThresholdField;

    /** Button to save (create or update) the current budget. */
    private JButton saveButton;

    /** Button to clear all form fields and deselect the current budget. */
    private JButton clearButton;

    /** Button to delete the currently selected budget. */
    private JButton deleteButton;

    /** Table displaying all saved budgets. */
    private JTable budgetTable;

    /** Table model backing the {@link #budgetTable}. */
    private DefaultTableModel tableModel;

    /** Progress bar indicating the percentage of the budget that has been spent. */
    private JProgressBar spendingBar;

    /** Label showing the current status (e.g., On Track, Near Limit, Exceeded). */
    private JLabel statusLabel;

    /** Label showing the remaining budget amount. */
    private JLabel remainingLabel;

    /** List of all budgets loaded from persistent storage. */
    private List<Budget> budgets;

    /** List of all transactions for the current user, used to calculate spending. */
    private List<Transaction> transactions;

    /** Reference to the parent {@link MainFrame} for accessing shared application state. */
    private MainFrame mainFrame;

    /**
     * The index of the currently selected budget in {@link #budgets}, or {@code -1}
     * if no budget is selected.
     */
    private int selectedBudgetIndex = -1;

    /**
     * Constructs a new {@code BudgetScreen} for the given parent frame.
     *
     * <p>Loads existing budgets and transactions from persistent storage,
     * then builds and displays all UI components.</p>
     *
     * @param mainFrame the parent {@link MainFrame} providing shared application context,
     *                  including the current user's email
     */
    public BudgetScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        budgets = DataManager.loadBudgets();
        transactions = DataManager.loadTransactions(mainFrame.getCurrentUserEmail());
        setLayout(new BorderLayout(10, 10));
        buildScreen();
        refreshTable();
    }

    /**
     * Builds and assembles the overall screen layout, including the title,
     * form, status panel, buttons, and budget table.
     */
    private void buildScreen() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.add(buildTitlePanel(), BorderLayout.NORTH);

        JPanel middlePanel = new JPanel(new GridLayout(1, 2, 10, 10));
        middlePanel.add(buildFormPanel());
        middlePanel.add(buildStatusPanel());
        mainPanel.add(middlePanel, BorderLayout.CENTER);
        mainPanel.add(buildButtonPanel(), BorderLayout.SOUTH);

        // add(buildNavBar(), BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buildTablePanel(), BorderLayout.SOUTH);
    }

    /**
     * Creates and returns the title panel displaying the screen heading.
     *
     * @return a {@link JPanel} containing the "Budget Management" title label
     */
    private JPanel buildTitlePanel() {
        JPanel panel = new JPanel();
        JLabel title = new JLabel("Budget Management");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(title);
        return panel;
    }

    /**
     * Creates and returns the form panel for entering budget details.
     *
     * <p>The form includes fields for category, budget limit, start date,
     * end date, and alert threshold. Start and end dates default to the
     * first and last days of the current month respectively.</p>
     *
     * @return a {@link JPanel} containing all budget input fields
     */
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

    /**
     * Creates and returns the status panel that displays spending progress
     * for the currently selected budget.
     *
     * <p>The panel contains a status label, a remaining amount label,
     * and a color-coded progress bar.</p>
     *
     * @return a {@link JPanel} containing the budget status display components
     */
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

    /**
     * Creates and returns the button panel with Save, Clear, and Delete actions.
     *
     * @return a {@link JPanel} containing the action buttons aligned to the right
     */
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

    /**
     * Creates and returns the table panel listing all existing budgets.
     *
     * <p>The table displays the ID, category, limit, amount spent, remaining amount,
     * date range, and status for each budget. Selecting a row populates the form
     * and updates the status panel.</p>
     *
     * @return a {@link JPanel} containing the budget table with a scroll pane
     */
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

    /**
     * Validates input and saves the budget — either creating a new one or
     * updating the currently selected one.
     *
     * <p>Validation checks include:</p>
     * <ul>
     *   <li>Non-empty limit, start date, and end date fields</li>
     *   <li>Limit must be a positive number</li>
     *   <li>Alert threshold must be an integer between 1 and 100</li>
     *   <li>No duplicate category budgets (unless editing the same budget)</li>
     * </ul>
     *
     * <p>On success, the budget list is persisted via {@link DataManager},
     * the table is refreshed, and the form is cleared.</p>
     */
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

    /**
     * Prompts the user for confirmation and deletes the currently selected budget.
     *
     * <p>If no budget is selected, an error dialog is shown. On confirmation,
     * the budget is removed from the list, the data is persisted, the table is
     * refreshed, and the form and status panel are reset.</p>
     */
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

    /**
     * Clears and repopulates the budget table with the current contents of
     * the {@link #budgets} list.
     */
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

    /**
     * Updates the status panel to reflect the spending state of the given budget.
     *
     * <p>The progress bar and labels are updated to show the percentage spent,
     * the remaining amount, and a color-coded status:</p>
     * <ul>
     *   <li>Red — budget exceeded</li>
     *   <li>Orange — near the alert threshold</li>
     *   <li>Green — on track</li>
     * </ul>
     *
     * @param budget the {@link Budget} whose status should be displayed
     */
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

    /**
     * Populates the form fields with the data from the given budget.
     *
     * <p>Used when a row is selected in the budget table to allow the user
     * to edit the selected budget.</p>
     *
     * @param budget the {@link Budget} whose data should be loaded into the form
     */
    private void fillFormWithBudget(Budget budget) {
        categoryComboBox.setSelectedItem(budget.getCategory());
        limitAmountField.setText(String.valueOf(budget.getLimitAmount()));
        startDateField.setText(budget.getStartDate());
        endDateField.setText(budget.getEndDate());
        alertThresholdField.setText(String.valueOf(budget.getAlertThreshold()));
    }

    /**
     * Resets the status panel to its default "no budget selected" state.
     *
     * <p>Clears the progress bar and resets all status labels.</p>
     */
    private void resetStatusPanel() {
        spendingBar.setValue(0);
        spendingBar.setString("0%");
        statusLabel.setText("Status: No budget selected");
        statusLabel.setForeground(Color.BLACK);
        remainingLabel.setText("Remaining: -");
    }

    /**
     * Calculates the total amount spent in the given category by summing all
     * {@link Expense} transactions that match the category name.
     *
     * @param category the category name to filter transactions by
     * @return the total amount spent in the specified category
     */
    private double calculateSpending(String category) {
        double total = 0;
        for (Transaction t : transactions) {
            if (t instanceof Expense && t.getCategory().equals(category)) {
                total += t.getAmount();
            }
        }
        return total;
    }

    /**
     * Resets the form to its default state: clears all input fields, resets
     * the category selection, deselects any budget in the table, and resets
     * the status panel.
     */
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

    /**
     * Loads all available expense categories from {@link DataManager} and
     * populates the {@link #categoryComboBox} with their names.
     */
    private void loadCategories() {
        List<Category> categories = DataManager.loadCategories();
        for (Category c : categories) {
            categoryComboBox.addItem(c.getName());
        }
    }

    /**
     * Returns the first day of the current month formatted as {@code YYYY-MM-DD}.
     *
     * @return a string representing the first day of the current month
     */
    private String getFirstDayOfMonth() {
        java.time.LocalDate today = java.time.LocalDate.now();
        return today.withDayOfMonth(1).toString();
    }

    /**
     * Returns the last day of the current month formatted as {@code YYYY-MM-DD}.
     *
     * @return a string representing the last day of the current month
     */
    private String getLastDayOfMonth() {
        java.time.LocalDate today = java.time.LocalDate.now();
        return today.withDayOfMonth(today.lengthOfMonth()).toString();
    }
}
