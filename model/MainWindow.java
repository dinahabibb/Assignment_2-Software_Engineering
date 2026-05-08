package model;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class MainWindow {
    private JFrame frame;
    private JPanel registerPanel;
    private JPanel loginPanel;


    public MainWindow(){
        initializeFrame();
    }

    public void initializeFrame(){
        frame = new JFrame();
        frame.setTitle("Authentication");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1280,720);
        frame.setLocationRelativeTo(null);

        //Buttons Panel
        JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JButton loginButton = new JButton("Login");
        loginButton.setFocusable(false);
        panel1.add(loginButton);

        JButton registerButton = new JButton("Register");
        registerButton.setFocusable(false);
        panel1.add(registerButton);

        //Forms panel
        CardLayout cardLayout = new CardLayout();
        JPanel panel2 = new JPanel(cardLayout);

        loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());

        registerPanel = new JPanel();
        registerPanel.setLayout(new GridBagLayout());


        loginForm();
        registerForm();

        panel2.add(loginPanel, "LOGIN");
        panel2.add(registerPanel, "REGISTER");

        // Button actions
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(panel2, "LOGIN");

            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(panel2, "REGISTER");
            }
        });

        //Panel position
        frame.setLayout(new BorderLayout());
        frame.add(panel1, BorderLayout.NORTH);
        frame.add(panel2, BorderLayout.CENTER);

    }
    //Login panel action
    public void loginForm(){
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 10, 5, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;

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

            gbc.gridx = 0; gbc.gridy = 3;
            JButton resetBtn = new JButton("Reset");
            resetBtn.addActionListener(e -> {
                emailField.setText("");
                passwordField.setText("");
            });
            loginPanel.add(resetBtn, gbc);

            gbc.gridx = 1;
            JButton loginBtn = new JButton("Login");
            loginBtn.addActionListener(e -> {
                String email     = emailField.getText().trim();
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
                    new ui.MainFrame(email).setVisible(true);;
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid email or password",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }    

            });
            loginPanel.add(loginBtn, gbc);

    }

    //Register panel action
    public void registerForm(){

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 10, 5, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;

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
            JTextField lastNameField  = new JTextField(15);
            JTextField emailField     = new JTextField(15);
            JPasswordField passField  = new JPasswordField(15);
            JPasswordField confField  = new JPasswordField(15);
            // JTextField currencyField  = new JTextField(15);

            gbc.gridx = 0; gbc.gridy = 1;
            registerPanel.add(new JLabel("First Name:"), gbc);
            gbc.gridx = 1;
            registerPanel.add(firstNameField, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            registerPanel.add(new JLabel("Last Name:"), gbc);
            gbc.gridx = 1;
            registerPanel.add(lastNameField, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            registerPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            registerPanel.add(emailField, gbc);

            gbc.gridx = 0; gbc.gridy = 4;
            registerPanel.add(new JLabel("Password:"), gbc);
            gbc.gridx = 1;
            registerPanel.add(passField, gbc);

            gbc.gridx = 0; gbc.gridy = 5;
            registerPanel.add(new JLabel("Confirm Password:"), gbc);
            gbc.gridx = 1;
            registerPanel.add(confField, gbc);

            // gbc.gridx = 0; gbc.gridy = 6;
            // registerPanel.add(new JLabel("Currency:"), gbc);
            // gbc.gridx = 1;
            // registerPanel.add(new JTextField(15), gbc);

            gbc.gridx = 0; gbc.gridy = 7;
            JButton resetBtn = new JButton("Reset");
            resetBtn.addActionListener(e -> {
                firstNameField.setText("");
                lastNameField.setText("");
                emailField.setText("");
                passField.setText("");
                confField.setText("");
            });
            registerPanel.add(resetBtn, gbc);

            gbc.gridx = 1;
            JButton registerBtn = new JButton("Register");
            registerBtn.addActionListener(e -> {
                String firstName = firstNameField.getText().trim();
                String lastName  = lastNameField.getText().trim();
                String email     = emailField.getText().trim();
                String password  = new String(passField.getPassword());
                String confirm   = new String(confField.getPassword());
            
                // Validate empty fields
                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                        || password.isEmpty() || confirm.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate email format
                if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                    JOptionPane.showMessageDialog(frame, "Invalid email format.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate password length
                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(frame, "Password must be at least 6 characters.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate passwords match
                if (!password.equals(confirm)) {
                    JOptionPane.showMessageDialog(frame, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                User user = new User(firstName, lastName, email, password);
                DataManager.saveUser(user);

                // JOptionPane.showMessageDialog(frame, "Registered successfully!");

                new ui.MainFrame(email).setVisible(true);;
                frame.dispose();
            });
            registerPanel.add(registerBtn, gbc);
        }

    public void show(){
        frame.setVisible(true);
    }
}
