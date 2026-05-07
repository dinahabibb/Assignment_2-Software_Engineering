import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
        fullPanel.add(mainPanel, BorderLayout.NORTH);
        fullPanel.add(buildTablePanel(), BorderLayout.CENTER);
        add(fullPanel);
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

        for (int i = 0; i < budgets.size(); i++) {
            Budget b = budgets.get(i);
            String category = b.getCategory();

            double totalSpending = 0;
            for (Transaction t : transactions) {
                if (t instanceof Expense && t.getCategory().equals(category)) {
                    totalSpending += t.getAmount();
                }
            }

            Budget updated = new Budget(b.getId(), b.getCategory(),
                    b.getLimitAmount(), b.getStartDate(), b.getEndDate(),
                    b.getAlertThreshold(), totalSpending, b.getStatus());

            budgets.set(i, updated);

            if (updated.isExceeded()) {
                JOptionPane.showMessageDialog(this,
                        "Warning: You have exceeded your " + category + " budget!",
                        "Budget Exceeded", JOptionPane.WARNING_MESSAGE);
            } else if (updated.isNearLimit()) {
                JOptionPane.showMessageDialog(this,
                        "Warning: You are near your " + category + " budget limit!",
                        "Near Limit", JOptionPane.WARNING_MESSAGE);
            }
        }

        DataManager.saveBudgets(budgets);
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