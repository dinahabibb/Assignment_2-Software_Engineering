package swing01;
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
        frame.setSize(800,500);
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

            gbc.gridx = 0; gbc.gridy = 1;
            loginPanel.add(new JLabel("Username:"), gbc);
            gbc.gridx = 1;
            loginPanel.add(new JTextField(15), gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            loginPanel.add(new JLabel("Password:"), gbc);
            gbc.gridx = 1;
            loginPanel.add(new JPasswordField(15), gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            loginPanel.add(new JButton("Reset"), gbc);
            gbc.gridx = 1;
            loginPanel.add(new JButton("Login"), gbc);

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

            gbc.gridx = 0; gbc.gridy = 1;
            registerPanel.add(new JLabel("First Name:"), gbc);
            gbc.gridx = 1;
            registerPanel.add(new JTextField(15), gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            registerPanel.add(new JLabel("Last Name:"), gbc);
            gbc.gridx = 1;
            registerPanel.add(new JTextField(15), gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            registerPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            registerPanel.add(new JTextField(15), gbc);

            gbc.gridx = 0; gbc.gridy = 4;
            registerPanel.add(new JLabel("Password:"), gbc);
            gbc.gridx = 1;
            registerPanel.add(new JPasswordField(15), gbc);

            gbc.gridx = 0; gbc.gridy = 5;
            registerPanel.add(new JLabel("Confirm Password:"), gbc);
            gbc.gridx = 1;
            registerPanel.add(new JPasswordField(15), gbc);

            gbc.gridx = 0; gbc.gridy = 6;
            registerPanel.add(new JLabel("Currency:"), gbc);
            gbc.gridx = 1;
            registerPanel.add(new JTextField(15), gbc);

            gbc.gridx = 0; gbc.gridy = 7;
            registerPanel.add(new JButton("Reset"), gbc);

            gbc.gridx = 1;
            registerPanel.add(new JButton("Submit"), gbc);
        }



    public void show(){
        frame.setVisible(true);
    }
}
