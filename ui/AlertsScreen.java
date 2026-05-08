package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import model.Budget;
import model.DataManager;
import model.Notification;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Alerts screen responsible for displaying:
 * - Budget status monitoring
 * - Notifications history
 * - Real-time budget alerts when expenses occur
 *
 * Acts as a UI + logic bridge between DataManager and user interface.
 */
public class AlertsScreen extends JPanel {

    /** List of all notifications */
    private List<Notification> notifications;

    /** List of all budgets */
    private List<Budget> budgets;

    /** Next notification ID generator */
    private int nextNotifId = 1;

    /** Panel displaying notification cards */
    private JPanel notifListPanel;

    /** Scroll container for notifications */
    private JScrollPane scrollPane;

    /** Panel displaying budget progress bars */
    private JPanel budgetStatusPanel;

    /** Label showing unread notification count */
    private JLabel unreadCountLabel;

    /**
     * Creates AlertsScreen and loads data from storage.
     */
    public AlertsScreen() {
        notifications = new ArrayList<>();
        budgets = new ArrayList<>();

        notifications = DataManager.loadNotifications();
        nextNotifId = DataManager.generateNotificationId(notifications);

        initUI();
        loadAndRefreshBudgets();
    }

    /**
     * Initializes the UI layout and components.
     */
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);

        JLabel title = new JLabel("Notifications & Alerts");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(33, 150, 243));
        topBar.add(title, BorderLayout.WEST);

        unreadCountLabel = new JLabel("0 unread");
        unreadCountLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        unreadCountLabel.setForeground(Color.GRAY);
        topBar.add(unreadCountLabel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(Color.WHITE);

        centerPanel.add(buildBudgetStatusPanel(), BorderLayout.NORTH);
        centerPanel.add(buildNotificationsPanel(), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Builds the budget status section UI.
     *
     * @return panel containing budget status
     */
    private JPanel buildBudgetStatusPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Budget Status",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 13)));

        budgetStatusPanel = new JPanel();
        budgetStatusPanel.setLayout(new BoxLayout(budgetStatusPanel, BoxLayout.Y_AXIS));
        budgetStatusPanel.setBackground(Color.WHITE);

        JLabel placeholder = new JLabel("No budgets to show.");
        placeholder.setFont(new Font("Arial", Font.ITALIC, 12));
        placeholder.setForeground(Color.GRAY);
        budgetStatusPanel.add(placeholder);

        wrapper.add(budgetStatusPanel);
        return wrapper;
    }

    /**
     * Builds notifications section UI.
     *
     * @return panel containing notifications list
     */
    private JPanel buildNotificationsPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Notification History",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 13)));

        notifListPanel = new JPanel();
        notifListPanel.setLayout(new BoxLayout(notifListPanel, BoxLayout.Y_AXIS));
        notifListPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(notifListPanel);
        scrollPane.setBorder(null);

        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Loads budgets and refreshes UI components.
     */
    public void loadAndRefreshBudgets() {
        budgets = DataManager.loadBudgets();
        refreshBudgetStatus();
        refreshNotifList();
        updateUnreadCount();
    }

    /**
     * Checks budget status after an expense is added
     * and generates notifications if needed.
     *
     * @param category expense category
     * @param amount expense amount
     */
    public void checkBudgetAfterExpense(String category, double amount) {
        budgets = DataManager.loadBudgets();

        Budget matchedBudget = null;
        for (Budget b : budgets) {
            if (b.getCategory().equalsIgnoreCase(category)) {
                matchedBudget = b;
                break;
            }
        }

        if (matchedBudget == null) return;

        Notification notif = null;
        double remaining = matchedBudget.getRemainingAmount();

        if (matchedBudget.isExceeded()) {
            double over = -remaining;
            String msg = "Budget Exceeded — " + category + "! You exceeded by " +
                    String.format("EGP %.2f", over) + ".";
            notif = new Notification(nextNotifId++, 1, "BUDGET_EXCEEDED", msg);

        } else if (matchedBudget.isNearLimit()) {
            String msg = "Budget Alert — " + category + ": You used " +
                    String.format("%.1f", matchedBudget.getSpendingPercentage()) + "%.";
            notif = new Notification(nextNotifId++, 1, "BUDGET_NEAR_LIMIT", msg);
        }

        if (notif != null) {
            notifications.add(0, notif);
            DataManager.saveNotifications(notifications);
            displayNotification(notif, matchedBudget);
        }

        refreshBudgetStatus();
    }

    /**
     * Displays notification popup and refreshes UI.
     *
     * @param notif notification to display
     * @param budget related budget
     */
    private void displayNotification(Notification notif, Budget budget) {
        refreshNotifList();
        updateUnreadCount();

        String title = "BUDGET_EXCEEDED".equals(notif.getType())
                ? "Budget Exceeded!"
                : "Budget Near Limit";

        int type = "BUDGET_EXCEEDED".equals(notif.getType())
                ? JOptionPane.WARNING_MESSAGE
                : JOptionPane.INFORMATION_MESSAGE;

        JOptionPane.showMessageDialog(this, notif.getMessage(), title, type);
    }

    /**
     * Updates budget status UI with progress bars.
     */
    private void refreshBudgetStatus() {
        budgetStatusPanel.removeAll();

        if (budgets.isEmpty()) {
            budgetStatusPanel.add(new JLabel("No budgets to show."));
        } else {
            for (Budget b : budgets) {
                budgetStatusPanel.add(buildBudgetRow(b));
                budgetStatusPanel.add(Box.createVerticalStrut(5));
            }
        }

        budgetStatusPanel.revalidate();
        budgetStatusPanel.repaint();
    }

    /**
     * Builds a single budget progress row.
     *
     * @param budget budget to display
     * @return UI row component
     */
    private JPanel buildBudgetRow(Budget budget) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(Color.WHITE);

        JLabel catLabel = new JLabel(budget.getCategory());

        JProgressBar bar = new JProgressBar(0, 100);
        int pct = (int) Math.min(
                (budget.getCurrentSpending() / budget.getLimitAmount()) * 100, 100);

        bar.setValue(pct);

        if (budget.isExceeded()) {
            bar.setForeground(new Color(244, 67, 54));
        } else if (budget.isNearLimit()) {
            bar.setForeground(new Color(255, 152, 0));
        } else {
            bar.setForeground(new Color(76, 175, 80));
        }

        row.add(catLabel, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);

        return row;
    }

    /**
     * Refreshes notification list UI.
     */
    private void refreshNotifList() {
        notifListPanel.removeAll();

        for (Notification n : notifications) {
            notifListPanel.add(new JLabel(n.getMessage()));
        }

        notifListPanel.revalidate();
        notifListPanel.repaint();
    }

    /**
     * Updates unread notification counter.
     */
    private void updateUnreadCount() {
        long unread = notifications.stream().filter(n -> !n.isRead()).count();
        unreadCountLabel.setText(unread + " unread");
    }

    /**
     * Adds a budget to the screen.
     *
     * @param budget budget to add
     */
    public void addBudget(Budget budget) {
        budgets.removeIf(b -> b.getCategory().equalsIgnoreCase(budget.getCategory()));
        budgets.add(budget);
        refreshBudgetStatus();
    }

    /**
     * @return list of budgets
     */
    public List<Budget> getBudgets() {
        return budgets;
    }
}
