package org.example.gui;

import org.example.db.MyJDBC;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegistrationGUI extends JFrame {
    public RegistrationGUI() {
        setTitle("Registration Form");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        // Title Label
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        titleLabel.setBounds(150, 10, 200, 30);
        panel.add(titleLabel);

        // Full Name Field
        JLabel lblFullName = new JLabel("Full Name:");
        lblFullName.setBounds(50, 50, 100, 25);
        panel.add(lblFullName);

        JTextField nameField = new JTextField();
        nameField.setBounds(160, 50, 200, 25);
        panel.add(nameField);

        // Email Field
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(50, 85, 100, 25);
        panel.add(lblEmail);

        JTextField emailField = new JTextField();
        emailField.setBounds(160, 85, 200, 25);
        panel.add(emailField);

        // Username Field
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(50, 120, 100, 25);
        panel.add(lblUsername);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(160, 120, 200, 25);
        panel.add(usernameField);

        // Password Field
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(50, 155, 100, 25);
        panel.add(lblPassword);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(160, 155, 200, 25);
        panel.add(passwordField);

        // Confirm Password Field
        JLabel lblConfirmPass = new JLabel("Confirm Password:");
        lblConfirmPass.setBounds(50, 190, 120, 25);
        panel.add(lblConfirmPass);

        JPasswordField confirmPassField = new JPasswordField();
        confirmPassField.setBounds(160, 190, 200, 25);
        panel.add(confirmPassField);

        // Phone Number Field
        JLabel lblPhone = new JLabel("Phone:");
        lblPhone.setBounds(50, 225, 100, 25);
        panel.add(lblPhone);

        JTextField phoneField = new JTextField();
        phoneField.setBounds(160, 225, 200, 25);
        panel.add(phoneField);

        // Register Button
        JButton btnRegister = new JButton("Register");
        btnRegister.setBounds(160, 270, 100, 30);
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get field values
                String fullName = nameField.getText();
                String email = emailField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPass = new String(confirmPassField.getPassword());
                String phone = phoneField.getText();

                // Validation
                if(fullName.isEmpty() || email.isEmpty() || username.isEmpty() ||
                        password.isEmpty() || confirmPass.isEmpty()) {
                    showError("All fields are required!");
                    return;
                }

                if(!password.equals(confirmPass)) {
                    showError("Passwords do not match!");
                    return;
                }

                if(!email.contains("@")) {
                    showError("Invalid email format!");
                    return;
                }

                if(!phone.matches("\\d+")) {
                    showError("Phone number must contain only numbers!");
                    return;
                }

                // If validation passes
                boolean registrationSuccess = MyJDBC.registerUser(
                        fullName, email, username, password, phone
                );

                if(registrationSuccess) {
                    JOptionPane.showMessageDialog(RegistrationGUI.this,
                            "Registration Successful!\nYou can now login.");
                    dispose();
                    new LoginGUI().setVisible(true);
                } else {
                    showError("Registration failed. Username might be taken.");
                }
            }

            private void showError(String message) {
                JOptionPane.showMessageDialog(RegistrationGUI.this,
                        message, "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(btnRegister);

        // Back to Login Link
        JLabel loginLink = new JLabel("Already have an account? Login here");
        loginLink.setForeground(Color.BLUE);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.setBounds(120, 310, 250, 25);
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginGUI().setVisible(true);
            }
        });
        panel.add(loginLink);

        setVisible(true);
    }
}