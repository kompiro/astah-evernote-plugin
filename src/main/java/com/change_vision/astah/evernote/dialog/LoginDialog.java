package com.change_vision.astah.evernote.dialog;

import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class LoginDialog extends JDialog implements ActionListener {

	private final class CancelActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	}

	private static final String ACTION_LOGIN = "login"; //$NON-NLS-1$
	private static final String ACTION_CANCEL = "cancel"; //$NON-NLS-1$

	private String username = ""; //$NON-NLS-1$
	private String password = ""; //$NON-NLS-1$

	private JTextField userField = new JTextField(15);
	private JPasswordField passField = new JPasswordField(15);

	private boolean canceled = true;

	public LoginDialog(Window window) {
		super(window);
		setTitle(Messages.getString("LoginDialog.title")); //$NON-NLS-1$
		setModal(true);
		getContentPane().setLayout(new GridLayout(3, 2));
		getContentPane().add(
				new JLabel(Messages.getString("LoginDialog.username_label"))); //$NON-NLS-1$
		getContentPane().add(userField);
		userField.addActionListener(this);
		getContentPane().add(
				new JLabel(Messages.getString("LoginDialog.password_label"))); //$NON-NLS-1$
		getContentPane().add(passField);
		passField.addActionListener(this);
		JButton login = new JButton(
				Messages.getString("LoginDialog.login_label")); //$NON-NLS-1$
		login.setActionCommand(ACTION_LOGIN);
		getContentPane().add(login);
		login.addActionListener(this);
		JButton cancel = new JButton(
				Messages.getString("LoginDialog.cancel_label")); //$NON-NLS-1$
		cancel.setActionCommand(ACTION_CANCEL);
		getContentPane().add(cancel);
		cancel.addActionListener(new CancelActionListener());
		pack();
		setLocationRelativeTo(window);
	}

	public void actionPerformed(ActionEvent e) {
		canceled = false;
		username = userField.getText();
		password = new String(passField.getPassword());
		setVisible(false);
	}

	public boolean isCanceled() {
		return canceled;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public static void main(String[] args) {
		LoginDialog dialog = new LoginDialog(null);
		dialog.setVisible(true);
	}

}
