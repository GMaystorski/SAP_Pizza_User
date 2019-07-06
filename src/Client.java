import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Client extends User{
	
	//private Socket socket;
	private ObjectInputStream objScan;
	private PrintStream printout;
	private String username;
	
	public Client(ObjectInputStream objScan, PrintStream printout,String username) {
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
		JButton b1 = new JButton("Create an order");//Dobavi izvestqvane za ostavashto vreme do dostavka
		JButton b2 = new JButton("View all products");
		JButton b3 = new JButton("Repeat previous order");
		JButton b4 = new JButton("Log out");
		frame.add(b1);
		frame.add(b2);
		frame.add(b3);
		frame.add(b4);
		
		//Set frame attributes//
		frame.setLayout(new GridLayout(4,1));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		logOut(frame,b4,printout);
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
