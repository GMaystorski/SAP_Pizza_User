import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Admin extends User{
	
	//private Socket socket;
	private ObjectInputStream objScan;
	private PrintStream printout;
	private String username;

	public Admin(ObjectInputStream objScan,PrintStream printout,String username) {
		setObjScan(objScan);
		setPrintout(printout);
		this.username = username;
		showMenu();
	}
	
	@Override
	public void showMenu() {
		
		//Create frame//
		JFrame frame = new JFrame();
		frame.setTitle("Menu");
		frame.setSize(300,300);
		
		//Create buttons and add to frame//
		JButton b1 = new JButton("Add new products");
		JButton b2 = new JButton("Manage existing products");
		JButton b3 = new JButton("View orders for a set period of time");
		JButton b4 = new JButton("Manage admin permissions");
		JButton b5 = new JButton("Log out");
		frame.add(b1);
		frame.add(b2);
		frame.add(b3);
		frame.add(b4);
		frame.add(b5);
		
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
					frame.dispose();
					addProduct();
			}
		});
		
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
					frame.dispose();
					printout.println("2");
					modifyProduct();
			}
		});
		
		//Set frame attributes//
		frame.setLayout(new GridLayout(5,1));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		logOut(frame,b5,printout);
	}
	
	@Override
	public void restart(PrintStream printout) {
		super.setPrintout(this.printout);
		super.setObjScan(this.objScan);
		super.showMenu();
	}
	
	public void addProduct() {
		
		printout.println("1");
		
		JFrame frame = new JFrame();
		frame.setTitle("Add products");
		frame.setSize(300,200);
		
		JLabel nameLabel = new JLabel("Name: ");
		nameLabel.setFont(new Font(nameLabel.getFont().getFontName(),Font.PLAIN,15));
		nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		JTextField nameField = new JTextField();
		frame.add(nameLabel);
		frame.add(nameField);
		
		JLabel quantLabel = new JLabel("Quantity: ");
		quantLabel.setFont(new Font(quantLabel.getFont().getFontName(),Font.PLAIN,15));
		quantLabel.setHorizontalAlignment(SwingConstants.CENTER);
		JTextField quantField = new JTextField();
		frame.add(quantLabel);
		frame.add(quantField);
		
		JLabel priceLabel = new JLabel("Price: ");
		priceLabel.setFont(new Font(priceLabel.getFont().getFontName(),Font.PLAIN,15));
		priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
		JTextField priceField = new JTextField();
		frame.add(priceLabel);
		frame.add(priceField);
		
		JButton back = new JButton("Back");
		frame.add(back);
		
		JButton addButton = new JButton("Add");
		frame.add(addButton);
		
		frame.setLayout(new GridLayout(4,2));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		goBack(frame,back,printout);
		
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					printout.println("add");
					printout.println(nameField.getText());
					printout.println(quantField.getText());
					printout.println(priceField.getText());
					int i = (int) objScan.readObject();
					if(i == 0) {
						JOptionPane.showMessageDialog(null, "Failed to add product!");
					}
					else JOptionPane.showMessageDialog(null, "Product successfully added!");
				}
				catch(ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
	public void modifyProduct()  {
		try {
			List<Object> products = (List<Object>) objScan.readObject();
			JFrame frame = new JFrame("Manage products");
			frame.setSize(600, 50*(products.size())/3);
			frame.setLayout(new GridLayout(2+(products.size())/3,5));
			frame.add(new JLabel("Product name"));
			frame.add(new JLabel("Quantity"));
			frame.add(new JLabel("Price"));
			frame.add(new JLabel(" "));
			frame.add(new JLabel(" "));
			
			
			for(int i = 0 ; i<products.size() ; i+=3) {
				JTextField b1 = new JTextField(products.get(i).toString());
				frame.add(b1);
				JTextField b2 = new JTextField(products.get(i+1).toString());
				frame.add(b2);
				JTextField b3 = new JTextField(products.get(i+2).toString());
				frame.add(b3);
				JButton up = new JButton("Update");
				up.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						updateProduct(b1.getText(),b2.getText(),Double.parseDouble(b3.getText()));
						frame.dispose();
						modifyProduct();
					}
				});
				frame.add(up);
				JButton delete = new JButton("Delete");
				delete.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						deleteProduct(b1.getText());
						frame.dispose();
						modifyProduct();
					}
				});
				frame.add(delete);
			}
			JButton back = new JButton("Back");
			goBack(frame,back,printout);
			frame.add(back);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
		catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void updateProduct(String name,String quantity,double price) {

		try {
			printout.println("update");
			printout.println(name);
			printout.println(quantity);
			printout.println(price);
			int i = (int) objScan.readObject();
			
			if(i == 0) {
				JOptionPane.showMessageDialog(null, "Update failed!");
			}
			else JOptionPane.showMessageDialog(null, "Update successful!");
		}
		catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void deleteProduct(String name) {
		try {
			printout.println("delete");
			printout.println(name);
			int i = (int) objScan.readObject();
			if(i == 0) {
				JOptionPane.showMessageDialog(null, "Delete failed!");
			}
			else JOptionPane.showMessageDialog(null, "Delete successful!");
		}
		catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void setObjScan(ObjectInputStream objScan) {
		this.objScan = objScan;
	}

	public void setPrintout(PrintStream printout) {
		this.printout = printout;
	}
	



}