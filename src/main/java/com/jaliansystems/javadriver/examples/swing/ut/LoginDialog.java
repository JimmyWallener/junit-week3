/*
  Example taken and modified from: http://www.zentut.com/java-swing/simple-login-dialog/
 */

package com.jaliansystems.javadriver.examples.swing.ut;

import java.awt.*;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class LoginDialog extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final JTextField tfUsername;
    private final JPasswordField pfPassword;
    private final JButton btnLogin;
    private final JLabel capsWarning;
    private boolean succeeded;

    public LoginDialog() {
        super("Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(300,200));
        GridBagConstraints cs = new GridBagConstraints();

        cs.fill = GridBagConstraints.HORIZONTAL;

        JLabel lbUsername = new JLabel("Username: ");
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(lbUsername, cs);

        tfUsername = new JTextField(20);
        tfUsername.setName("username");
        tfUsername.setToolTipText("Enter user name");
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(tfUsername, cs);

        JLabel lbPassword = new JLabel("Password: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(lbPassword, cs);

        pfPassword = new JPasswordField(20);
        pfPassword.setName("password");
        pfPassword.setToolTipText("Enter password");
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(pfPassword, cs);
        panel.setBorder(new LineBorder(Color.GRAY));

        capsWarning = new JLabel();
        capsWarning.setBackground(Color.RED);
        capsWarning.setOpaque(true);
        capsWarning.setForeground(Color.white);
        cs.gridx = 1;
        cs.gridy = 2;
        cs.gridwidth = 1;
        panel.add(capsWarning,cs);

        btnLogin = new JButton("Login");
        btnLogin.setName("login_button");
        btnLogin.setEnabled(false);

        btnLogin.addActionListener(e -> {
            if (authenticate(getUsername(), getPassword())) {
                onSuccess();
                succeeded = true;
                dispose();
            } else {
                onInvalidCredentials();
                // reset username and password
                tfUsername.setText("");
                pfPassword.setText("");
                succeeded = false;

            }
        });
        tfUsername.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                enableLoginButton();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                enableLoginButton();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                enableLoginButton();
            }
        });
        pfPassword.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                enableLoginButton();
                capsLockWarning();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                enableLoginButton();
                capsLockWarning();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                enableLoginButton();
                capsLockWarning();
            }
        });
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setName("cancel_button");
        btnCancel.addActionListener(e -> {
            dispose();
            onCancel();
        });
        JPanel bp = new JPanel();
        bp.add(btnLogin);
        bp.add(btnCancel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public boolean capsLockWarning() {
        if (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) {
            capsWarning.setText("CAPS LOCK IS ON");
            return true;
        } else {
            capsWarning.setText("");
        }
        return false;
    }

    private void enableLoginButton() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            SwingUtilities.invokeLater(
                    () -> btnLogin.setEnabled(tfUsername.getText().length() > 0 && pfPassword.getPassword().length > 0));
        }).start();

    }

    public String getUsername() {
        return tfUsername.getText().trim();
    }

    public String getPassword() {
        return new String(pfPassword.getPassword());
    }

    public boolean isSucceeded() {
        return succeeded;
    }
    protected boolean authenticate(String username, String password) {

        return username.equals("bob") && password.equals("secret");
    }

    protected void onSuccess() {
        JOptionPane.showMessageDialog(LoginDialog.this, "Hi " + getUsername() + "! You have successfully logged in.",
                "Login Success", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    protected void onCancel() {
        JOptionPane.showMessageDialog(LoginDialog.this, "Sorry to see you going.", "Login Cancel", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    protected void onInvalidCredentials() {
        JOptionPane.showMessageDialog(LoginDialog.this, "Invalid username or password", "Invalid Login", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        System.out.println("LoginDialog.main()");
        LoginDialog d = new LoginDialog();
        SwingUtilities.invokeLater(() -> d.setVisible(true));
    }
}
