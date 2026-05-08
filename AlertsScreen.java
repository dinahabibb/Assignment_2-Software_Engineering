import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Alerts Screen - shows budget over-limit alerts and all notifications.
 * Implements US5: Budget Over-Limit Alert
 * Based on Sequence Diagram #5 in SDS
 * Person 3
 */
public class AlertsScreen extends JPanel {

    // Data
    private List<Notification> notifications;
    private List<Budget> budgets;
    private int nextNotifId = 1;

    // UI components
    private JPanel notifListPanel;
    private JScrollPane scrollPane;
    private JPanel budgetStatusPanel;
    private JLabel unreadCountLabel;

    public AlertsScreen() {
        notifications = DataManager.loadNotifications();
        budgets = new ArrayList<>();
        initUI();
        loadAndRefreshBudgets();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        // Top bar: title + unread count
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

        // Center: split into budget status (top) + notifications list (bottom)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(Color.WHITE);

        centerPanel.add(buildBudgetStatusPanel(), BorderLayout.NORTH);
        centerPanel.add(buildNotificationsPanel(), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom: mark all as read button
        JButton markAllBtn = new JButton("Mark All as Read");
        markAllBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        markAllBtn.setBackground(new Color(100, 160, 220));
        markAllBtn.setForeground(Color.WHITE);
        markAllBtn.setFocusPainted(false);
        markAllBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        markAllBtn.addActionListener(this::handleMarkAllRead);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.add(markAllBtn);
        add(bottomBar, BorderLayout.SOUTH);
    }

    // ---------- Budget Status Panel ----------
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
        placeholder.setBorder(new EmptyBorder(5, 5, 5, 5));
        budgetStatusPanel.add(placeholder);

        wrapper.add(budgetStatusPanel);
        return wrapper;
    }

    // ---------- Notifications Panel ----------
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

    // ---------- Load budgets from DataManager (Person 2's DataManager) ----------
    /**
     * Loads saved budgets from DataManager and refreshes the status panel.
     * Called on screen open and after returning from BudgetScreen.
     */
    public void loadAndRefreshBudgets() {
        budgets = DataManager.loadBudgets();
        refreshBudgetStatus();
        refreshNotifList();
        updateUnreadCount();
    }

    // ---------- Called from TransactionScreen when expense is added ----------
    /**
     * This is the main method called when user adds an expense.
     * Matches the sequence diagram flow for US5 Budget Over-Limit Alert:
     * 1. updateSpending(amount) called on Budget
     * 2. getRemainingAmount() checked
     * 3. isExceeded() / isNearLimit() evaluated
     * 4. createNotification() if needed
     * 5. displayNotification() - UI updated
     */
    public void checkBudgetAfterExpense(String category, double amount) {
        // Reload budgets fresh from file
        budgets = DataManager.loadBudgets();

        // Find matching budget
        Budget matchedBudget = null;
        for (Budget b : budgets) {
            if (b.getCategory().equalsIgnoreCase(category)) {
                matchedBudget = b;
                break;
            }
        }

        if (matchedBudget == null) return; // no budget for this category

        Notification notif = null;
        double remaining = matchedBudget.getRemainingAmount();

        if (matchedBudget.isExceeded()) {
            // Budget is exceeded branch from sequence diagram
            double over = -remaining;
            String msg = "Budget Exceeded — " + category + "! You've exceeded your " +
                    String.format("EGP %.2f", matchedBudget.getLimitAmount()) +
                    " budget by EGP " + String.format("%.2f", over) + ".";

            // createNotification("Budget Exceeded") - from sequence diagram
            notif = createNotification(nextNotifId++, 1, "BUDGET_EXCEEDED", msg);

        } else if (matchedBudget.isNearLimit()) {
            // Budget near limit branch from sequence diagram
            double pct = matchedBudget.getSpendingPercentage();
            String msg = "Budget Alert — " + category + ": You've used " +
                    String.format("%.1f", pct) + "% of your " + category + " budget.";

            // createNotification("Budget near limit") - from sequence diagram
            notif = createNotification(nextNotifId++, 1, "BUDGET_NEAR_LIMIT", msg);
        }

        if (notif != null) {
            notifications.add(0, notif); // most recent first
            // displayNotification() - from sequence diagram
            DataManager.saveNotifications(notifications); // save to file
            displayNotification(notif, matchedBudget);
        }

        // Always refresh budget status
        refreshBudgetStatus();
    }

    // createNotification() from sequence diagram
    private Notification createNotification(int id, int userId, String type, String message) {
        return new Notification(id, userId, type, message);
    }

    // displayNotification() - from sequence diagram - shows popup
    private void displayNotification(Notification notif, Budget budget) {
        String title;
        int msgType;

        if ("BUDGET_EXCEEDED".equals(notif.getType())) {
            title = "Budget Exceeded!";
            msgType = JOptionPane.WARNING_MESSAGE;
        } else {
            title = "Budget Near Limit";
            msgType = JOptionPane.INFORMATION_MESSAGE;
        }

        JOptionPane.showMessageDialog(this, notif.getMessage(), title, msgType);
        refreshNotifList();
        updateUnreadCount();
    }

    // ---------- Refresh Budget Status with progress bars ----------
    private void refreshBudgetStatus() {
        budgetStatusPanel.removeAll();

        if (budgets.isEmpty()) {
            JLabel empty = new JLabel("No budgets to show.");
            empty.setFont(new Font("Arial", Font.ITALIC, 12));
            empty.setForeground(Color.GRAY);
            empty.setBorder(new EmptyBorder(5, 5, 5, 5));
            budgetStatusPanel.add(empty);
        } else {
            for (Budget b : budgets) {
                budgetStatusPanel.add(buildBudgetRow(b));
                budgetStatusPanel.add(Box.createVerticalStrut(5));
            }
        }

        budgetStatusPanel.revalidate();
        budgetStatusPanel.repaint();
    }

    // Build a progress bar row for one budget - uses Person 2's Budget API
    private JPanel buildBudgetRow(Budget budget) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(3, 5, 3, 5));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        // Category label
        JLabel catLabel = new JLabel(budget.getCategory());
        catLabel.setFont(new Font("Arial", Font.BOLD, 12));
        catLabel.setPreferredSize(new Dimension(100, 20));
        row.add(catLabel, BorderLayout.WEST);

        // Progress bar - updateProgressBar from sequence diagram
        // Use Person 2's getLimitAmount() and getCurrentSpending()
        int pct = 0;
        if (budget.getLimitAmount() > 0) {
            pct = (int) Math.min((budget.getCurrentSpending() / budget.getLimitAmount()) * 100, 100);
        }
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(pct);
        bar.setStringPainted(true);
        bar.setString(String.format("EGP %.0f / EGP %.0f", budget.getCurrentSpending(), budget.getLimitAmount()));

        // Color: red if exceeded, orange if near limit, green if ok
        // updateProgressBar("red") / updateProgressBar("orange") from sequence diagram
        if (budget.isExceeded()) {
            bar.setForeground(new Color(244, 67, 54)); // red
        } else if (budget.isNearLimit()) {
            bar.setForeground(new Color(255, 152, 0)); // orange
        } else {
            bar.setForeground(new Color(76, 175, 80)); // green
        }

        row.add(bar, BorderLayout.CENTER);

        // Status label
        JLabel statusLabel = new JLabel(budget.getStatus());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusLabel.setPreferredSize(new Dimension(80, 20));

        if (budget.isExceeded()) statusLabel.setForeground(new Color(244, 67, 54));
        else if (budget.isNearLimit()) statusLabel.setForeground(new Color(255, 152, 0));
        else statusLabel.setForeground(new Color(76, 175, 80));

        row.add(statusLabel, BorderLayout.EAST);
        return row;
    }

    // ---------- Refresh Notifications List ----------
    private void refreshNotifList() {
        notifListPanel.removeAll();

        if (notifications.isEmpty()) {
            JLabel empty = new JLabel("No new notifications.");
            empty.setFont(new Font("Arial", Font.ITALIC, 12));
            empty.setForeground(Color.GRAY);
            empty.setBorder(new EmptyBorder(10, 10, 10, 10));
            notifListPanel.add(empty);
        } else {
            for (Notification n : notifications) {
                notifListPanel.add(buildNotifCard(n));
                notifListPanel.add(Box.createVerticalStrut(5));
            }
        }

        notifListPanel.revalidate();
        notifListPanel.repaint();
    }

    // Build one notification card
    private JPanel buildNotifCard(Notification notif) {
        JPanel card = new JPanel(new BorderLayout(8, 0));

        // Unread = slightly blue background, read = light gray
        if (!notif.isRead()) {
            card.setBackground(new Color(232, 244, 253));
        } else {
            card.setBackground(new Color(245, 245, 245));
        }

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(8, 10, 8, 10)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Icon based on type
        String icon;
        if ("BUDGET_EXCEEDED".equals(notif.getType())) {
            icon = "[!!]";
        } else if ("BUDGET_NEAR_LIMIT".equals(notif.getType())) {
            icon = "[!]";
        } else {
            icon = "[*]";
        }

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.BOLD, 14));
        card.add(iconLabel, BorderLayout.WEST);

        // Message and time
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(card.getBackground());

        JLabel msgLabel = new JLabel("<html><body style='width:350px'>" + notif.getMessage() + "</body></html>");
        msgLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        if (!notif.isRead()) {
            msgLabel.setFont(new Font("Arial", Font.BOLD, 12));
        }
        center.add(msgLabel);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        JLabel timeLabel = new JLabel(sdf.format(notif.getTimestamp()));
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        timeLabel.setForeground(Color.GRAY);
        center.add(timeLabel);

        card.add(center, BorderLayout.CENTER);

        // Mark as read button (only if unread)
        if (!notif.isRead()) {
            JButton readBtn = new JButton("Read");
            readBtn.setToolTipText("Mark as read");
            readBtn.setFont(new Font("Arial", Font.PLAIN, 11));
            readBtn.setFocusPainted(false);
            readBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            readBtn.addActionListener(e -> {
                notif.markAsRead(); // markAsRead():void from sequence diagram
                refreshNotifList();
                updateUnreadCount();
            });
            card.add(readBtn, BorderLayout.EAST);
        }

        return card;
    }

    // ---------- Mark all as read ----------
    private void handleMarkAllRead(ActionEvent e) {
        for (Notification n : notifications) {
            n.markAsRead();
            DataManager.saveNotifications(notifications);
        }
        refreshNotifList();
        updateUnreadCount();
    }

    private void updateUnreadCount() {
        long unread = notifications.stream().filter(n -> !n.isRead()).count();
        unreadCountLabel.setText(unread + " unread");
    }

    // ---------- Public method for other screens to add budgets ----------
    public void addBudget(Budget budget) {
        // Remove existing budget for same category if any
        budgets.removeIf(b -> b.getCategory().equalsIgnoreCase(budget.getCategory()));
        budgets.add(budget);
        refreshBudgetStatus();
    }

    public List<Budget> getBudgets() {
        return budgets;
    }
}


