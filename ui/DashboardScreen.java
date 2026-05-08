package ui;

import model.Transaction;

import javax.swing.*;
import java.awt.*;

/**
 * A JPanel-based dashboard screen that provides a high-level financial overview
 * for the current user.
 *
 * <p>The dashboard displays three summary cards (total balance, total income,
 * and total expenses) and a list of the seven most recent transactions.
 * All monetary values are displayed in EGP.</p>
 *
 * <p>The screen is refreshed by calling {@link #refresh()}, which recalculates
 * totals and rebuilds the recent transactions list from
 * {@link Transaction#allTransactions}.</p>
 */
public class DashboardScreen extends JPanel {

    /** Reference to the parent {@link MainFrame} for screen navigation. */
    private MainFrame mainFrame;

    /** Background color for the Total Balance summary card. */
    private static final Color BLUE_CARD  = new Color(41, 128, 185);

    /** Background color for the Total Income summary card. */
    private static final Color GREEN_CARD = new Color(39, 174, 96);

    /** Background color for the Total Expenses summary card. */
    private static final Color RED_CARD   = new Color(231, 76, 60);

    /** Label displaying the calculated total balance (income minus expenses). */
    private JLabel balanceLabel;

    /** Label displaying the total income across all transactions. */
    private JLabel incomeLabel;

    /** Label displaying the total expenses across all transactions. */
    private JLabel expenseLabel;

    /** Panel that holds the recent transaction row labels. */
    private JPanel recentPanel;

    /**
     * Constructs a new {@code DashboardScreen} for the given parent frame.
     *
     * <p>Sets up the layout and builds all UI components.</p>
     *
     * @param mainFrame the parent {@link MainFrame} used for navigating to other screens
     */
    public DashboardScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buildUI();
    }

    /**
     * Builds and lays out all UI components of the dashboard.
     *
     * <p>The layout consists of:</p>
     * <ul>
     *   <li>A title label at the top</li>
     *   <li>Three colored summary cards (balance, income, expenses)</li>
     *   <li>A "Recent Transactions" section with a "See all →" navigation button</li>
     *   <li>An empty filler panel to prevent the content from stretching vertically</li>
     * </ul>
     */
    private void buildUI() {
        // Title 
        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // ── CENTER: 4-row grid (cards / heading / transactions / empty filler) ─
        JPanel center = new JPanel(new GridLayout(3, 1, 0, 10));
        center.setBackground(Color.WHITE);

        // Row 1: three summary cards
        JPanel summaryRow = new JPanel(new GridLayout(1, 3, 10, 0));
        summaryRow.setBackground(Color.WHITE);

        balanceLabel = makeValueLabel();
        incomeLabel  = makeValueLabel();
        expenseLabel = makeValueLabel();

        summaryRow.add(makeSummaryCard("Total Balance",  balanceLabel, BLUE_CARD));
        summaryRow.add(makeSummaryCard("Total Income",   incomeLabel,  GREEN_CARD));
        summaryRow.add(makeSummaryCard("Total Expenses", expenseLabel, RED_CARD));
        center.add(summaryRow);

        // Row 2: "Recent Transactions" heading label
        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setBackground(Color.WHITE);

        JLabel recentTitle = new JLabel("Recent Transactions");
        recentTitle.setFont(new Font("Arial", Font.BOLD, 16));

        JButton seeAll = new JButton("See all →");
        seeAll.setBorderPainted(false);
        seeAll.setContentAreaFilled(false);
        seeAll.setFocusPainted(false);
        seeAll.setForeground(BLUE_CARD);

        seeAll.addActionListener(e -> mainFrame.showScreen("transactions"));

        recentHeader.add(recentTitle, BorderLayout.WEST);
        recentHeader.add(seeAll,      BorderLayout.EAST);

        JPanel recentSection = new JPanel(new BorderLayout(0, 5));
        recentSection.setBackground(Color.WHITE);
        recentSection.add(recentHeader, BorderLayout.NORTH);

        recentPanel = new JPanel(new GridLayout(7, 1, 0, 4));
        // recentPanel.setLayout(new BoxLayout(recentPanel, BoxLayout.Y_AXIS));
        recentPanel.setBackground(Color.WHITE);
        recentSection.add(recentPanel, BorderLayout.CENTER);
        
        center.add(recentSection);

        // Row 3: empty filler so rows 1-3 don't stretch to fill the screen
        JPanel filler = new JPanel();
        filler.setBackground(Color.WHITE);
        center.add(filler);

        add(center, BorderLayout.CENTER);
    }

    /**
     * Refreshes the dashboard with the latest financial data.
     *
     * <p>This method recalculates total income, expenses, and balance, then
     * updates the summary card labels accordingly. It also rebuilds the recent
     * transactions list, displaying up to the 7 most recent entries in reverse
     * chronological order. Income rows are colored green; expense rows are red.</p>
     *
     * <p>If no transactions exist, a placeholder message is shown instead.</p>
     */
    public void refresh() {
        double income   = Transaction.getTotalIncome();
        double expenses = Transaction.getTotalExpenses();
        double balance  = income - expenses;

        incomeLabel.setText(String.format("%.2f EGP", income));
        expenseLabel.setText(String.format("%.2f EGP", expenses));
        balanceLabel.setText(String.format("%.2f EGP", balance));

        recentPanel.removeAll();
        int size  = Transaction.allTransactions.size();
        int start = Math.max(0, size - 7);

        if (size == 0) {
            JLabel noTransaction = new JLabel("No transactions yet.");
            noTransaction.setForeground(Color.GRAY);
            noTransaction.setFont(new Font("Arial", Font.ITALIC, 13));
            recentPanel.add(noTransaction);
        } else {
            for (int i = size - 1; i >= start; i--) {
                Transaction t = Transaction.allTransactions.get(i);
                boolean isInc = t.getType().equalsIgnoreCase("income");

                String text   = (isInc ? " + " : " - ") + String.format("%.2f", t.getAmount()) 
                        + " EGP  |  " + t.getCategory() + "  |  " + t.getDate() 
                        + "  |  " + t.getDescription();

                        
                JLabel row = new JLabel(text);
                
                row.setFont(new Font("Arial", Font.PLAIN, 16));
                row.setForeground(t.getType().equalsIgnoreCase("income") ? GREEN_CARD : RED_CARD);

                row.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

                recentPanel.add(row);
            }
        }

        recentPanel.revalidate();
        recentPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            revalidate();
            repaint();
        });
    }

    /**
     * Creates and returns a pre-styled value label for use inside a summary card.
     *
     * <p>The label is centered, has inner vertical padding, and defaults to
     * displaying {@code "0.00 EGP"}.</p>
     *
     * @return a new {@link JLabel} configured for displaying a monetary value
     */
    private JLabel makeValueLabel() {
        JLabel label = new JLabel("0.00 EGP", SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        return label;
    }

    /**
     * Creates and returns a colored summary card panel containing a title and
     * a value label.
     *
     * <p>The card uses the provided background color, white text, and a padded
     * border. The title is displayed above the value in a smaller plain font.</p>
     *
     * @param title      the heading text displayed at the top of the card
     * @param valueLabel the {@link JLabel} that will display the monetary value
     * @param bg         the background {@link Color} for the card
     * @return a styled {@link JPanel} representing the summary card
     */
    private JPanel makeSummaryCard(String title, JLabel valueLabel, Color bg) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bg);
        card.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        titleLabel.setForeground(Color.WHITE);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setForeground(Color.WHITE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

}
