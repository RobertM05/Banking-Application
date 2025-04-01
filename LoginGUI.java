package org.example.gui;

import org.example.User;
import org.example.db.MyJDBC;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class LoginGUI extends JFrame {
    public LoginGUI() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400,300);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(lblUsername,gbc);

        JTextField usernameField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(usernameField,gbc);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(lblPassword,gbc);

        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(passwordField,gbc);

        JButton btnLogin = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(btnLogin,gbc);

        JLabel registerLabel = new JLabel("Don't have an account? Register here");
        registerLabel.setForeground(Color.BLUE);
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 3;
        mainPanel.add(registerLabel,gbc);

        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                char[] passwordChars = passwordField.getPassword();
                String username = usernameField.getText().trim();
                String password = new String(passwordChars);

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginGUI.this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    Arrays.fill(passwordChars, '*');
                    return;
                }

                try {

                    User user = MyJDBC.validateLogin(username, password);
                    if (user != null) {
                        LoginGUI.this.dispose();
                        new DashboardGUI(user).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(LoginGUI.this,
                                "Invalid username or password!", "Authentication failed!", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginGUI.this, "Database connection error" + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);

                } finally {
                    Arrays.fill(passwordChars, '\0');
                    passwordField.setText("");
                }
            }
        });

       registerLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    LoginGUI.this.dispose();
                    new RegistrationGUI().setVisible(true);
                }
            });

            add(mainPanel);
            setVisible(true);
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> new LoginGUI());
        }
    }
