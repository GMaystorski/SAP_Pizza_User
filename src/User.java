import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.List;

import javax.swing.*;
public class User {
	
	private Socket socket;
	private ObjectInputStream objScan;
	private PrintStream printout;
	
	public User(Socket socket) throws IOException {
		
		this.socket = socket;
		printout = new PrintStream(socket.getOutputStream());
		objScan = new ObjectInputStream(socket.getInputStream());
		showMenu();
		
	}
	
	public User(ObjectInputStream objScan,PrintStream printout) {
		this.objScan = objScan;
		this.printout = printout;
	}
	
	public User() {
		
	}
	
	
	
	public void showMenu() {
		 JFrame frame = new JFrame();
		 frame.setTitle("Login");
		 frame.setLayout(null);
		 frame.setSize(350,200);
		 
		 JLabel userLabel = new JLabel("Username: ");
		 userLabel.setBounds(60,30,80,15);
		 frame.add(userLabel);
		 
		 JTextField userField = new JTextField();
		 userField.setEditable(true);
		 userField.setBounds(150,25,150,25);
		 frame.add(userField);
		 
		 
		 JLabel passLabel = new JLabel("Password: ");
		 passLabel.setBounds(60,80,80,15);
		 frame.add(passLabel);
		 
		 JPasswordField pass = new JPasswordField();
		 pass.setEditable(true);
		 pass.setBounds(150,75,150,25);
		 frame.add(pass);
		 
		 JButton log = new JButton("Login");
		 log.setBounds(80,120,80,20);
		 frame.add(log);
		 
		 JButton reg = new JButton("Register");
		 reg.setBounds(170,120,90,20);
		 frame.add(reg);
		 
		 frame.setVisible(true);
		 
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 
		 log.addActionListener(new ActionListener() {
			 @Override
			 public void actionPerformed(ActionEvent event) {
				 try {
					 printout.println(userField.getText());
					 printout.println(pass.getPassword());
					 printout.println("login");
					 List<Object> results =  (List<Object>) objScan.readObject();
					 if(!results.isEmpty()) {
						 switch(results.get(2).toString()) {
						 	case "false" : new Client(objScan,printout,userField.getText());
						 			frame.dispose();
						 			JOptionPane.showMessageDialog(null, "User logged successfully!");
						 			break;
						 	case "true" : new Admin(objScan,printout,userField.getText());
						 			frame.dispose();
						 			JOptionPane.showMessageDialog(null, "Admin logged successfully!");
						 			break;
						 }
					 }
					 else JOptionPane.showMessageDialog(null, "Incorrect login , to create a profile press 'Register'");
						
				 }
				 catch(ClassNotFoundException | IOException e) {
					 closeResources();
					 e.printStackTrace();
				 }
			 }
			 
		 });
		 
		 reg.addActionListener(new ActionListener() {
			 @Override
			 public void actionPerformed(ActionEvent event) {
				 try {
					 printout.println(userField.getText());
					 printout.println(pass.getPassword());
					 printout.println("register");
					 int i = (int) objScan.readObject();
					 //System.out.println(i);
					 if(i == 0) {
						 JOptionPane.showMessageDialog(null, "Failed to register user!");
						 throw new RegistrationException();
					 }
					 else {
						 new Client(objScan,printout,userField.getText());
						 frame.dispose();
						 JOptionPane.showMessageDialog(null, "Registration Successful!"); 
					 }
				 }
				 catch(RegistrationException | IOException | ClassNotFoundException e) {
					 closeResources();
					 e.printStackTrace();
				 }
			 }
		 });
		 
		 
	}
	
	public void closeResources() {
		try {
			printout.close();
			objScan.close();
			socket.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void logOut(JFrame frame,JButton button,PrintStream printout) {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
					frame.dispose();
					JOptionPane.showMessageDialog(null, "User Logged Out!");
					printout.println("logout");
					restart(printout);
			}
		});
	}
	
	
	
	public void restart(PrintStream printout) {
		setPrintout(printout);
		showMenu();
	}
	
	public void goBack(JFrame frame,JButton button,PrintStream printout) {
		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				printout.println("back");
				frame.dispose();
				showMenu();
			}
		});
		
	}
	
	public ObjectInputStream getObjScan() {
		return objScan;
	}

	public PrintStream getPrintout() {
		return printout;
	}

	public void setObjScan(ObjectInputStream objScan) {
		this.objScan = objScan;
	}

	public void setPrintout(PrintStream printout) {
		this.printout = printout;
	}


}
