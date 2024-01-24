package chatapplication;

// import ChatApp;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

import static chatapplication.MulticastClient.name;

public class Menu extends JFrame implements ActionListener
{
  private JTextField usernameField;
  private JPasswordField passwordField;
  private JButton loginButton;
  private JTextField signUpUsernameField;
  private JPasswordField signUpPasswordField;
  private JButton signUpButton;
  private JTabbedPane tabbedPane;
  private JLabel errorMessageLabel;

  private static final String JDBC_URL = "jdbc:mysql://localhost:3306/chirp";
  private static final String JDBC_USER = "bivas";
  private static final String JDBC_PASSWORD = "16146";

  public Menu ()
  {
    setTitle ("Chirp Registration");
    setSize (480, 360);
    setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

    JPanel signInPanel = createSignInPanel ();
    JPanel signUpPanel = createSignUpPanel ();

    tabbedPane = new JTabbedPane ();
    tabbedPane.addTab ("Sign In", signInPanel);
    tabbedPane.addTab ("Sign Up", signUpPanel);

    errorMessageLabel = new JLabel ();
    errorMessageLabel.setForeground (Color.RED);

    setLayout (new BorderLayout ());
    add (tabbedPane, BorderLayout.CENTER);
    add (errorMessageLabel, BorderLayout.SOUTH);

    setLocationRelativeTo (null);
    setVisible (true);
  }

  private JPanel
  createSignInPanel ()
  {
    JPanel panel = new JPanel ();
    panel.setLayout (new GridLayout (3, 2, 10, 10));

    JLabel usernameLabel = new JLabel ("Username:");
    JLabel passwordLabel = new JLabel ("Password:");
    usernameField = new JTextField ();
    passwordField = new JPasswordField ();
    loginButton = new JButton ("Login");

    panel.add (usernameLabel);
    panel.add (usernameField);
    panel.add (passwordLabel);
    panel.add (passwordField);
    panel.add (new JLabel ());
    panel.add (loginButton);

    loginButton.addActionListener (this);

    return panel;
  }

  private JPanel
  createSignUpPanel ()
  {
    JPanel panel = new JPanel ();
    panel.setLayout (new GridLayout (3, 2, 10, 10));

    JLabel usernameLabel = new JLabel ("Username:");
    JLabel passwordLabel = new JLabel ("Password:");
    signUpUsernameField = new JTextField ();
    signUpPasswordField = new JPasswordField ();
    signUpButton = new JButton ("Sign Up");

    panel.add (usernameLabel);
    panel.add (signUpUsernameField);
    panel.add (passwordLabel);
    panel.add (signUpPasswordField);
    panel.add (new JLabel ());
    panel.add (signUpButton);

    signUpButton.addActionListener (this);

    return panel;
  }

  public void
  actionPerformed (ActionEvent e)
  {
    if (e.getSource () == loginButton)
      {
        String inputUsername = usernameField.getText ();
        char[] inputPassword = passwordField.getPassword ();

        if (validateLogin (inputUsername, String.valueOf (inputPassword)))
          {

            errorMessageLabel.setForeground (Color.GREEN);
            errorMessageLabel.setText ("Login Successful!");

            this.setVisible (false);
            name = inputUsername;
            java.awt.EventQueue.invokeLater (new Runnable () {
              public void run ()
              {
                new ChatApp (inputUsername).setVisible (true);
              }
            });
          }
        else
          {
            errorMessageLabel.setForeground (Color.RED);
            errorMessageLabel.setText (
                "Invalid username or password. Please try again.");
          }

        usernameField.setText ("");
        passwordField.setText ("");
      }
    else if (e.getSource () == signUpButton)
      {
        String signUpUsername = signUpUsernameField.getText ();
        char[] signUpPassword = signUpPasswordField.getPassword ();

        if (performSignUp (signUpUsername, String.valueOf (signUpPassword)))
          {
            errorMessageLabel.setForeground (Color.GREEN);
            errorMessageLabel.setText ("Sign Up Successful!");
          }
        else
          {
            errorMessageLabel.setForeground (Color.RED);
            errorMessageLabel.setText (
                "Error during sign-up. Please try again.");
          }

        signUpUsernameField.setText ("");
        signUpPasswordField.setText ("");
      }
  }

  private boolean
  validateLogin (String username, String password)
  {
    try (Connection connection
         = DriverManager.getConnection (JDBC_URL, JDBC_USER, JDBC_PASSWORD))
      {
        String query
            = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement preparedStatement
             = connection.prepareStatement (query))
          {
            preparedStatement.setString (1, username);
            preparedStatement.setString (2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery ())
              {
                return resultSet.next ();
              }
          }
      }
    catch (SQLException ex)
      {
        ex.printStackTrace ();
        return false;
      }
  }

  private boolean
  performSignUp (String username, String password)
  {
    try (Connection connection
         = DriverManager.getConnection (JDBC_URL, JDBC_USER, JDBC_PASSWORD))
      {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement preparedStatement
             = connection.prepareStatement (query))
          {
            preparedStatement.setString (1, username);
            preparedStatement.setString (2, password);

            int rowsAffected = preparedStatement.executeUpdate ();
            return rowsAffected > 0;
          }
      }
    catch (SQLException ex)
      {
        ex.printStackTrace ();
        return false;
      }
  }

  public static void
  main (String[] args)
  {
    SwingUtilities.invokeLater (() -> { new Menu (); });
  }
}
