package ui;

import javax.swing.*;

import model.DataManager;
import model.Transaction;

import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel(cardLayout);

    private DashboardScreen dashboardScreen;
    private ReportScreen reportScreen;
    private TransactionScreen transactionScreen;
    private BudgetScreen budgetScreen;
    private GoalScreen goalScreen;
    private AlertsScreen alertsScreen;

    private String currentUserEmail;

    public MainFrame (String email) {
        this.currentUserEmail = email;
        setTitle("BudgetApp");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    setLayout(new BorderLayout());

        Transaction.allTransactions = DataManager.loadTransactions(currentUserEmail);

        dashboardScreen = new DashboardScreen(this);
        reportScreen    = new ReportScreen(this);
        transactionScreen = new TransactionScreen(this);
        budgetScreen = new BudgetScreen(this);

        alertsScreen = new AlertsScreen();
        goalScreen = new GoalScreen();

        cardPanel.add(transactionScreen,"transactions");
        cardPanel.add(budgetScreen,"budgets");
        cardPanel.add(alertsScreen,"alerts");
        cardPanel.add(goalScreen,"goals");

        cardPanel.add(dashboardScreen,"dashboard");
        cardPanel.add(reportScreen,"reports");

        add(buildSidebar(), BorderLayout.WEST);
        add(cardPanel,      BorderLayout.CENTER);

        showScreen("dashboard");
        System.out.println("Before dashboard refresh");
        dashboardScreen.refresh();
        System.out.println("After dashboard refresh");
        reportScreen.refresh();

        setVisible(true);
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }


    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setPreferredSize(new Dimension(160, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel appName = new JLabel("BudgetApp");
        appName.setFont(new Font("Arial", Font.BOLD, 16));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);
        appName.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        sidebar.add(appName);

        sidebar.add(navButton("Dashboard",    "dashboard"));
        sidebar.add(navButton("Transactions", "transactions"));
        sidebar.add(navButton("Budgets",      "budgets"));
        sidebar.add(navButton("Reports",      "reports"));
        sidebar.add(navButton("Goals",        "goals"));
        sidebar.add(navButton("Alerts",       "alerts"));
        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = navButton("Logout", "logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new model.MainWindow().show();
        });
        sidebar.add(logoutBtn);

        return sidebar;
    }

    private JButton navButton(String label, String screenKey) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(44, 62, 80));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 8));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> showScreen(screenKey));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(52, 73, 94));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(44, 62, 80));
            }
        });

        return btn;
    }

    public void showScreen(String key) {
        Transaction.allTransactions = new java.util.ArrayList<>(transactionScreen.transactions);
        cardLayout.show(cardPanel, key);
        if (key.equals("dashboard"))    dashboardScreen.refresh();
        if (key.equals("reports"))      reportScreen.refresh();
        if (key.equals("alerts"))       alertsScreen.loadAndRefreshBudgets();
        // if (key.equals("transactions")) {
        //         transactionScreen.transactions = DataManager.loadTransactions(); // ADD
        //         transactionScreen.refreshTable();                                // ADD
        // }
    }

    public void refreshScreens() {
        Transaction.allTransactions = new java.util.ArrayList<>(transactionScreen.transactions);
        dashboardScreen.refresh();
        reportScreen.refresh();
    }
}
