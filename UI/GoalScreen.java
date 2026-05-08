package UI;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import Model.Goal;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class GoalScreen extends JPanel {

    // Data
    private List<Goal> goals;
    private int nextId = 1;

    // create goal form
    private JTextField nameField;
    private JTextField targetAmountField;
    private JTextField initialAmountField;
    private JTextField deadlineField;  // format: dd/MM/yyyy
    private JButton createGoalButton;
    private JLabel feedbackLabel;

    // goals list
    private JPanel goalsListPanel;
    private JScrollPane scrollPane;

    public GoalScreen() {
        goals = new ArrayList<>();
        initUI();
        // sample goal 
        addSampleData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        // Title
        JLabel title = new JLabel("Financial Goals");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(33, 150, 243));
        add(title, BorderLayout.NORTH);

        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(320);
        splitPane.setResizeWeight(0.4);
        splitPane.setBorder(null);

        splitPane.setLeftComponent(buildFormPanel());
        splitPane.setRightComponent(buildGoalsListPanel());

        add(splitPane, BorderLayout.CENTER);
    }

    // ----------  Create Goal Form ----------
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(33, 150, 243)),
                "Add New Goal",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 13),
                new Color(33, 150, 243)));
        panel.setBackground(Color.WHITE);

        // Goal Name
        panel.add(makeLabel("Goal Name:"));
        nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(8));

        // Target Amount
        panel.add(makeLabel("Target Amount (EGP):"));
        targetAmountField = new JTextField();
        targetAmountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(targetAmountField);
        panel.add(Box.createVerticalStrut(8));

        // Initial Saved Amount (optional)
        panel.add(makeLabel("Already Saved (optional):"));
        initialAmountField = new JTextField("0");
        initialAmountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(initialAmountField);
        panel.add(Box.createVerticalStrut(8));

        // Deadline
        panel.add(makeLabel("Deadline (dd/MM/yyyy):"));
        deadlineField = new JTextField();
        deadlineField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(deadlineField);
        panel.add(Box.createVerticalStrut(15));

        // Create Goal Button
        createGoalButton = new JButton("Create Goal");
        createGoalButton.setBackground(new Color(33, 150, 243));
        createGoalButton.setForeground(Color.WHITE);
        createGoalButton.setFont(new Font("Arial", Font.BOLD, 13));
        createGoalButton.setFocusPainted(false);
        createGoalButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        createGoalButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createGoalButton.addActionListener(this::handleCreateGoal);
        panel.add(createGoalButton);
        panel.add(Box.createVerticalStrut(8));

        // Feedback label
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        feedbackLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(feedbackLabel);

        return panel;
    }

    // ----------  Goals List ----------
    private JPanel buildGoalsListPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "My Goals",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 13)));

        goalsListPanel = new JPanel();
        goalsListPanel.setLayout(new BoxLayout(goalsListPanel, BoxLayout.Y_AXIS));
        goalsListPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(goalsListPanel);
        scrollPane.setBorder(null);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    // ---------- Create Goal ----------
    private void handleCreateGoal(ActionEvent e) {
        // Step 1: Read inputs
        String name = nameField.getText().trim();
        String targetStr = targetAmountField.getText().trim();
        String initialStr = initialAmountField.getText().trim();
        String deadlineStr = deadlineField.getText().trim();

        // Step 2: Parse numbers
        double targetAmount;
        double initialAmount = 0;
        Date deadline;

        try {
            targetAmount = Double.parseDouble(targetStr);
        } catch (NumberFormatException ex) {
            showError("Please enter a valid target amount.");
            return;
        }

        try {
            if (!initialStr.isEmpty()) {
                initialAmount = Double.parseDouble(initialStr);
            }
        } catch (NumberFormatException ex) {
            showError("Please enter a valid initial saved amount.");
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            deadline = sdf.parse(deadlineStr);
        } catch (Exception ex) {
            showError("Please enter deadline in format dd/MM/yyyy.");
            return;
        }

        // Step 3: Validate ( validate: targetAmount > 0 and deadline is future date)
        String validationError = Goal.validate(name, targetAmount, deadline);
        if (validationError != null) {
            showError(validationError);
            return;
        }

        // Step 4: Create and save goal 
        Goal goal = new Goal(nextId++, name, targetAmount, initialAmount, deadline);
        goals.add(goal);

        // Step 5: Show monthly savings needed 
        double monthly = goal.getMonthlySavingsNeeded();
        showSuccess("Goal created! Save EGP " + String.format("%.2f", monthly) + "/month to reach it.");

        // Step 6: Refresh list (displayGoalsScreen + progressBar )
        refreshGoalsList();
        clearForm();
    }

    // ---------- Add Contribution ----------
    private void handleAddContribution(Goal goal, JProgressBar progressBar, JLabel statusLabel, JLabel savedLabel) {
        // Ask user for contribution amount
        String input = JOptionPane.showInputDialog(this,
                "Add contribution to \"" + goal.getName() + "\":\nAlready saved: EGP " +
                        String.format("%.2f", goal.getCurrentAmount()),
                "Add Contribution",
                JOptionPane.PLAIN_MESSAGE);

        if (input == null || input.trim().isEmpty()) return;

        double amount;
        try {
            amount = Double.parseDouble(input.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = goal.addContribution(amount);
        if (!success) {
            JOptionPane.showMessageDialog(this, "Amount must be greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        
        int progress = (int) goal.getProgress();

        // updateProgressBar
        progressBar.setValue(progress);
        progressBar.setString(progress + "%");
        savedLabel.setText(String.format("Saved: EGP %.2f / EGP %.2f", goal.getCurrentAmount(), goal.getTargetAmount()));

        if (goal.isAchieved()) {
            statusLabel.setText("Completed!");
            statusLabel.setForeground(new Color(76, 175, 80));
            progressBar.setForeground(new Color(76, 175, 80));
            JOptionPane.showMessageDialog(this, "Congratulations! You reached your goal: " + goal.getName(),
                    "Goal Achieved!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            statusLabel.setText("In Progress");
            statusLabel.setForeground(new Color(33, 150, 243));
        }
    }

    // ---------- Refresh Goals List ----------
    private void refreshGoalsList() {
        goalsListPanel.removeAll();

        if (goals.isEmpty()) {
            JLabel empty = new JLabel("No goals yet. Add one!");
            empty.setFont(new Font("Arial", Font.ITALIC, 13));
            empty.setForeground(Color.GRAY);
            empty.setBorder(new EmptyBorder(20, 20, 20, 20));
            goalsListPanel.add(empty);
        } else {
            for (Goal goal : goals) {
                goalsListPanel.add(buildGoalCard(goal));
                goalsListPanel.add(Box.createVerticalStrut(8));
            }
        }

        goalsListPanel.revalidate();
        goalsListPanel.repaint();
    }

    // ---------- Build one Goal Card ----------
    private JPanel buildGoalCard(Goal goal) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(245, 250, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 240)),
                new EmptyBorder(10, 12, 10, 12)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // Goal name
        JLabel nameLabel = new JLabel("[Goal] " + goal.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(5));

        // Saved amount
        JLabel savedLabel = new JLabel(String.format("Saved: EGP %.2f / EGP %.2f",
                goal.getCurrentAmount(), goal.getTargetAmount()));
        savedLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        savedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(savedLabel);

        // Deadline
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        JLabel deadlineLabel = new JLabel("Deadline: " + sdf.format(goal.getDeadline()));
        deadlineLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        deadlineLabel.setForeground(Color.GRAY);
        deadlineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(deadlineLabel);
        card.add(Box.createVerticalStrut(6));

        // Progress bar 
        int progress = (int) goal.getProgress();
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(progress);
        progressBar.setString(progress + "%");
        progressBar.setStringPainted(true);
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (goal.isAchieved()) {
            progressBar.setForeground(new Color(76, 175, 80)); // green if done
        } else {
            progressBar.setForeground(new Color(33, 150, 243)); // blue in progress
        }
        card.add(progressBar);
        card.add(Box.createVerticalStrut(6));

        // Status + contribute button row
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        row.setBackground(new Color(245, 250, 255));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel statusLabel = new JLabel(goal.getStatus());
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(goal.isAchieved() ? new Color(76, 175, 80) : new Color(33, 150, 243));
        row.add(statusLabel);

        if (!goal.isAchieved()) {
            JButton contributeBtn = new JButton("+ Add Contribution");
            contributeBtn.setFont(new Font("Arial", Font.PLAIN, 11));
            contributeBtn.setBackground(new Color(33, 150, 243));
            contributeBtn.setForeground(Color.WHITE);
            contributeBtn.setFocusPainted(false);
            contributeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
           
            contributeBtn.addActionListener(e ->
                    handleAddContribution(goal, progressBar, statusLabel, savedLabel));
            row.add(contributeBtn);
        }

        card.add(row);
        return card;
    }

    
    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void showError(String msg) {
        feedbackLabel.setText(msg);
        feedbackLabel.setForeground(Color.RED);
    }

    private void showSuccess(String msg) {
        feedbackLabel.setText(msg);
        feedbackLabel.setForeground(new Color(76, 175, 80));
    }

    private void clearForm() {
        nameField.setText("");
        targetAmountField.setText("");
        initialAmountField.setText("0");
        deadlineField.setText("");
    }

    // Public getter for other screens to access goals (used by Dashboard)
    public List<Goal> getGoals() {
        return goals;
    }

    private void addSampleData() {
        // One sample goal 
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date deadline = sdf.parse("31/12/2026");
            Goal sample = new Goal(nextId++, "Summer Vacation", 5000, 1200, deadline);
            goals.add(sample);
            refreshGoalsList();
        } catch (Exception ignored) {}
    }
}
