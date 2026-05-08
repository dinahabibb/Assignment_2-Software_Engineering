package model;

import javax.swing.*;
import java.awt.*;

/**
 * Authentication window of the application.
 * Handles user login and registration using Swing UI.
 * Uses CardLayout to switch between Login and Register forms.
 */
public class MainWindow {

    /** Main application frame */
    private JFrame frame;

    /** Panel for registration form */
    private JPanel registerPanel;

    /** Panel for login form */
    private JPanel loginPanel;

    /**
     * Initializes the authentication window.
     */
    public MainWindow() {
        initializeFrame();
    }

    /**
     * Builds and configures the main JFrame and its components.
     * Sets up login/register panels and navigation buttons.
     */
    public void initializeFrame() {
        frame = new JFrame();
        frame.setTitle("Authentication");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);

        // Buttons panel (Login/Register switch)
        JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JButton loginButton = new JButton("Login");
        loginButton.setFocusable(false);
        panel1.add(loginButton);

        JButton registerButton = new JButton("Register");
        registerButton.setFocusable(false);
        panel1.add(registerButton);

        // Card layout for switching forms
        CardLayout cardLayout = new CardLayout();
        JPanel panel2 = new JPanel(cardLayout);

        loginPanel = new JPanel(new GridBagLayout());
        registerPanel = new JPanel(new GridBagLayout());

        loginForm();
        registerForm();

        panel2.add(loginPanel, "LOGIN");
        panel2.add(registerPanel, "REGISTER");

        // Switch to login form
        loginButton.addActionListener(e -> cardLayout.show(panel2, "LOGIN"));

        // Switch to register form
        registerButton.addActionListener(e -> cardLayout.show(panel2, "REGISTER"));

        frame.setLayout(new BorderLayout());
        frame.add(panel1, BorderLayout.NORTH);
        frame.add(panel2, BorderLayout.CENTER);
    }

    /**
     * Builds the login form UI and handles login validation.
     */
    public void loginForm() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;


        JLabel title = new JLabel("Login Form");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField emailField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        gbc.gridx = 0; gbc.gridy = 1;
        loginPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        // Reset button
        gbc.gridx = 0; gbc.gridy = 3;
        JButton resetBtn = new JButton("Reset");
        resetBtn.addActionListener(e -> {
            emailField.setText("");
            passwordField.setText("");
        });
        loginPanel.add(resetBtn, gbc);

        // Login button
        gbc.gridx = 1;
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                JOptionPane.showMessageDialog(frame, "Invalid email format.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (password.length() < 6) {
                JOptionPane.showMessageDialog(frame, "Password must be at least 6 characters.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean ok = DataManager.validateLogin(email, password);

            if (ok) {
                JOptionPane.showMessageDialog(frame, "Login successful!");
                new ui.MainFrame(email).setVisible(true);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid email or password",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginPanel.add(loginBtn, gbc);
    }

    /**
     * Builds the registration form UI and handles user registration.
     */
    public void registerForm() {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("Register Form");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        registerPanel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField firstNameField = new JTextField(15);
        JTextField lastNameField = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JPasswordField confField = new JPasswordField(15);

        // Input fields setup omitted here for brevity (same as your code)

        // Register button
        gbc.gridx = 1;
        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());
            String confirm = new String(confField.getPassword());

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                    || password.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                JOptionPane.showMessageDialog(frame, "Invalid email format.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (password.length() < 6) {
                JOptionPane.showMessageDialog(frame, "Password must be at least 6 characters.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(frame, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = new User(firstName, lastName, email, password);
            DataManager.saveUser(user);

            new ui.MainFrame(email).setVisible(true);
            frame.dispose();
        });

        registerPanel.add(registerBtn, gbc);
    }

    /**
     * Displays the authentication window.
     */
    public void show() {
        frame.setVisible(true);
    }
}
