import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
        notifications = new ArrayList<>();
        budgets = new ArrayList<>();
        notifications = DataManager.loadNotifications();
        nextNotifId = DataManager.generateNotificationId(notifications) ;
        initUI();
        loadAndRefreshBudgets();
    }

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

    // ---------- Load budgets from DataManager --------------
    public void loadAndRefreshBudgets() {
        budgets = DataManager.loadBudgets();
        refreshBudgetStatus();
        refreshNotifList();
        updateUnreadCount();
    }

    public void checkBudgetAfterExpense(String category, double amount) {
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
            // Budget is exceeded 
            double over = -remaining;
            String msg = "Budget Exceeded — " + category + "! You've exceeded your " +
                    String.format("EGP %.2f", matchedBudget.getLimitAmount()) +
                    " budget by EGP " + String.format("%.2f", over) + ".";
            notif = createNotification(nextNotifId++, 1, "BUDGET_EXCEEDED", msg);

        } else if (matchedBudget.isNearLimit()) {
            double pct = matchedBudget.getSpendingPercentage();
            String msg = "Budget Alert — " + category + ": You've used " +
                    String.format("%.1f", pct) + "% of your " + category + " budget.";
            notif = createNotification(nextNotifId++, 1, "BUDGET_NEAR_LIMIT", msg);
        }

        if (notif != null) {
            notifications.add(0, notif); // most recent first
            DataManager.saveNotifications(notifications); 
            displayNotification(notif, matchedBudget);
        }
        refreshBudgetStatus();
    }

    private Notification createNotification(int id, int userId, String type, String message) {
        return new Notification(id, userId, type, message);
    }

    private void displayNotification(Notification notif, Budget budget) {
        // First update the Notification History panel so it is visible immediately
        refreshNotifList();
        updateUnreadCount();
        String title;
        int msgType;

        if ("BUDGET_EXCEEDED".equals(notif.getType())) {
            title = "Budget Exceeded!";
            msgType = JOptionPane.WARNING_MESSAGE;
        } else {
            title = "Budget Near Limit";
            msgType = JOptionPane.INFORMATION_MESSAGE;
        }

        // Show the alert popup after the list has been updated
        JOptionPane.showMessageDialog(this, notif.getMessage(), title, msgType);
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

    // Build a progress bar row
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

        int pct = 0;
        if (budget.getLimitAmount() > 0) {
            pct = (int) Math.min((budget.getCurrentSpending() / budget.getLimitAmount()) * 100, 100);
        }
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(pct);
        bar.setStringPainted(true);
        bar.setString(String.format("EGP %.0f / EGP %.0f", budget.getCurrentSpending(), budget.getLimitAmount()));

        // Color: red if exceeded, orange if near limit, green if ok
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

    private JPanel buildNotifCard(Notification notif) {
        JPanel card = new JPanel(new BorderLayout(8, 0));

        // Unread = blue background, read = light gray
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

        // OK button and closes the notification window
        if (!notif.isRead()) {
            JButton readBtn = new JButton("OK");
            readBtn.setToolTipText("Mark as read and close");
            readBtn.setFont(new Font("Arial", Font.PLAIN, 11));
            readBtn.setFocusPainted(false);
            readBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            readBtn.addActionListener(e -> {
                notif.markAsRead(); 
                DataManager.saveNotifications(notifications); 
                refreshNotifList();
                updateUnreadCount();
                // Close the parent Window (
                Window parentWindow = SwingUtilities.getWindowAncestor(AlertsScreen.this);
                if (parentWindow != null) {
                    parentWindow.dispose();
                }
            });
            card.add(readBtn, BorderLayout.EAST);
        }

        return card;
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


