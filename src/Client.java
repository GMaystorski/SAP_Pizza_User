import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Client extends User{
	
	//private Socket socket;
	private ObjectInputStream objScan;
	private PrintStream printout;
	private String username;
	private List<String> cart;
	private int move = 0;
	
	public Client(ObjectInputStream objScan, PrintStream printout,String username) {
		setObjScan(objScan);
		setPrintout(printout);
		this.username = username;
		cart = new ArrayList<>();
		showMenu();
	}
	
	@Override
	public void showMenu() {
		//Create frame//
		JFrame frame = new JFrame();
		frame.setTitle("Menu");
		frame.setSize(300,300);
		
		//Create buttons and add to frame//
		JButton b1 = new JButton("Create an order");//Dobavi izvestqvane za ostavashto vreme do dostavka
		JButton b2 = new JButton("View all products");
		JButton b3 = new JButton("Repeat previous order");
		JButton b4 = new JButton("Log out");
		frame.add(b1);
		frame.add(b2);
		frame.add(b3);
		frame.add(b4);
		
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if(cart.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Your cart is empty! Please select products and come back.");
					b2.doClick();
				}
				else {
					printout.println("1");
					frame.dispose();
					enterLocation();
				}
			}
		});
		
		
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				printout.println("2");
				frame.dispose();
				viewProducts(b1);
			}
		});
		
		b3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				printout.println("3");
				frame.dispose();
				optionThree(b1);
			}
		});
		
		//Set frame attributes//
		frame.setLayout(new GridLayout(4,1));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		logOut(frame,b4,printout);
	}
	
	
	public void viewProducts(JButton button) {
		try {
			List<Object> products = (List<Object>) objScan.readObject();
			JFrame frame = new JFrame("View menu");
			frame.setSize(750,50*(products.size()/3));
			frame.setLayout(new GridLayout(2+(products.size()/3),5));
			
			JButton back = new JButton("Back");
			goBack(frame,back,printout);
			frame.add(back);
			frame.add(new JLabel(" "));
			frame.add(new JLabel(" "));
			frame.add(new JLabel(" "));
			JButton toOrder = new JButton("Proceed to order");
			toOrder.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					printout.println("proceed");
					frame.dispose();
					button.doClick();
				}
			});
			frame.add(toOrder);
			
			frame.add(new JLabel("Product name"));
			frame.add(new JLabel("Quantity"));
			frame.add(new JLabel("Price(lv.)"));
			frame.add(new JLabel(" "));
			frame.add(new JLabel(" "));
			
			for(int i = 0 ; i < products.size() ; i+=3) {
				JTextField b1 = new JTextField(products.get(i).toString());
				b1.setEditable(false);
				frame.add(b1);
				JTextField b2 = new JTextField(products.get(i+1).toString());
				b2.setEditable(false);
				frame.add(b2);
				JTextField b3 = new JTextField(products.get(i+2).toString());
				b3.setEditable(false);
				frame.add(b3);
				JButton add = new JButton("Add to Cart");
				add.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent event) {
						int amount = addToCart(b1.getText(),b3.getText());
						printCart();
						JOptionPane.showMessageDialog(null, "You have " + amount + " " + b1.getText()+ (amount>1 ? "'s " : " ") + "in your cart!");
					}
				});
				frame.add(add);
				JButton remove = new JButton("Remove from Cart");
				remove.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						int res = removeFromCart(b1.getText());
						JOptionPane.showMessageDialog(null, res == 0 ? "Item not present in cart!" : "Item removed from cart!");
					}
				});
				frame.add(remove);	
			}
			
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
		catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public int addToCart(String product,String price) {
			if(cart.contains(product)) {
				int temp = Integer.parseInt(cart.get(cart.indexOf(product)+1));
				temp++;
				cart.set(cart.indexOf(product)+1, String.valueOf(temp));
				return temp;
			}
			else {
				cart.add(product);
				cart.add("1");
				cart.add(price);
				return 1;
			}
	}
	
	public void createOrder(String location) {
		double sum = 0;
		JFrame frame = new JFrame("Create an order");
		frame.setSize(600,100*cart.size()/3);
		frame.setLayout(new GridLayout(2+(cart.size()/3),4));
		frame.add(new JLabel("Product name"));
		frame.add(new JLabel("Quantity in cart"));
		frame.add(new JLabel("Price"));
		
		JButton button = new JButton("Order");
		button.addActionListener(new ActionListener() {
			@Override 
			public void actionPerformed(ActionEvent event) {
				sendOrder(location);
			}
		});
		frame.add(button);
		
		for(int i = 0 ; i < cart.size() ; i+=3) {
			JTextField t1 = new JTextField(cart.get(i));
			t1.setEditable(false);
			frame.add(t1);
			
			JTextField t2 = new JTextField(cart.get(i+1));
			t2.setEditable(false);
			frame.add(t2);
			
			JTextField t3 = new JTextField(cart.get(i+2));
			t3.setEditable(false);
			frame.add(t3);
			
			double temp = Integer.parseInt(cart.get(i+1))*Double.parseDouble(cart.get(i+2));
			JTextField t4 = new JTextField("Subtotal: " + temp);
			t4.setEditable(false);
			frame.add(t4);
			sum+=temp;
		}
		
		JButton back = new JButton("Back");
		goBack(frame,back,printout);
		frame.add(back);
		
		frame.add(new JLabel(" "));
		frame.add(new JLabel(" "));
		
		JTextField total = new JTextField("Total: " + sum);
		total.setEditable(false);
		frame.add(total);
		
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void sendOrder(String location) {
		try {
			printout.println("create");
			printout.println(location);
			sendCart();
			int i = (int)objScan.readObject();
			if(i == 0) {
				JOptionPane.showMessageDialog(null, "Order creation failed!");
			}
			else if(i == 1) {
				JOptionPane.showMessageDialog(null, "Order creation successful!");
			}
		}
		catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void optionThree(JButton button) {
		try {
			int count = (int) objScan.readObject();
			if(count == 0) {
				JOptionPane.showMessageDialog(null, "You have no orders yet!");
				button.doClick();
			}
			else {
				List<List<String>> carts = new ArrayList<>();
				String[] locations = new String[count];
				for(int i = 0 ; i < count ; i++) {
					carts.add((List<String>) objScan.readObject());
					locations[i] = objScan.readObject().toString();
				}
				reCreateOrder(carts,locations,button);
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reCreateOrder(List<List<String>> cart,String[] location,JButton b1) {
		double sum = 0;
		JFrame frame = new JFrame("Repeat order");
		frame.setSize(600,100*cart.get(move).size()/3);
		frame.setLayout(new GridLayout(2+(cart.get(move).size()/3),4));
		frame.add(new JLabel("Product name"));
		frame.add(new JLabel("Quantity in cart"));
		frame.add(new JLabel("Price"));
		
		JTextField total = new JTextField();
		total.setEditable(false);

		frame.add(total);
		
		for(int i = 0 ; i < cart.get(move).size() ; i+=3) {
			JTextField t1 = new JTextField(cart.get(move).get(i));
			t1.setEditable(false);
			frame.add(t1);
			
			JTextField t2 = new JTextField(cart.get(move).get(i+1));
			t2.setEditable(false);
			frame.add(t2);
			
			JTextField t3 = new JTextField(cart.get(move).get(i+2));
			t3.setEditable(false);
			frame.add(t3);
			
			double temp = Integer.parseInt(cart.get(move).get(i+1))*Double.parseDouble(cart.get(move).get(i+2));
			JTextField t4 = new JTextField("Subtotal: " + temp);
			t4.setEditable(false);
			frame.add(t4);
			sum+=temp;
		}
		
		total.setText("Total: " + sum);
		
		JButton back = new JButton("Back");
		goBack(frame,back,printout);
		frame.add(back);
		
		JButton previous = new JButton("Previous");
		JButton next = new JButton("Next");
		previous.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				if(move == 0 ) {
					JOptionPane.showMessageDialog(null, "No previous order!");
				}
				else {
					frame.dispose();
					move--;
					reCreateOrder(cart,location,b1);
				}
			}
		});
		next.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				if(move == (cart.size() - 1) ) {
					JOptionPane.showMessageDialog(null, "No next order!");
				}
				else {
					frame.dispose();
					move++;
					reCreateOrder(cart,location,b1);
				}
			}
		});
		frame.add(previous);
		frame.add(next);
		
		JButton button = new JButton("Order");
		button.addActionListener(new ActionListener() {
			@Override 
			public void actionPerformed(ActionEvent event) {
				replaceCart(cart.get(move));
				sendOrder(location[move]);
				frame.dispose();
				optionThree(b1);
			}
		});
		frame.add(button);
		
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void enterLocation() {
		JFrame frame = new JFrame("Enter location");
		frame.setSize(300, 100);
		frame.setLayout(new GridLayout(1,2));
		JTextField t1 = new JTextField();
		frame.add(t1);
		JButton b1 = new JButton("Confirm");
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				frame.dispose();
				createOrder(t1.getText());
			}
		});
		frame.add(b1);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void replaceCart(List<String> newCart) {
		cart.clear();
		newCart.forEach(x -> cart.add(x));
	}
	
	public int removeFromCart(String product) {
		if(cart.contains(product)) {
			cart.remove(cart.indexOf(product)+1);
			cart.remove(cart.indexOf(product)+1);
			cart.remove(product);
			return 1;
		}
		else return 0;
	}
	
	public void sendCart() {
		printout.println(String.valueOf(cart.size()));
		for(String str : cart) {
			printout.println(str);
		}
	}
	
	public void printCart() {
		cart.forEach(x -> System.out.println(x));
	}
	
	
	
	@Override
	public void restart(PrintStream printout) {
		super.setPrintout(this.printout);
		super.setObjScan(this.objScan);
		super.showMenu();
	}
	

	public ObjectInputStream getObjScan() {
		return objScan;
	}

	public void setObjScan(ObjectInputStream objScan) {
		this.objScan = objScan;
	}

	public PrintStream getPrintout() {
		return printout;
	}

	public void setPrintout(PrintStream printout) {
		this.printout = printout;
	}

}
