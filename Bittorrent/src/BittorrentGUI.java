import javax.swing.*;
import javax.swing.text.DefaultCaret;

import org.apache.commons.net.ntp.*;

import java.io.*;
import java.io.File.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class BittorrentGUI extends JFrame{
	
	String path, username;
	boolean pathset = false;
	String TIME_SERVER = "time-a.nist.gov";
	
	//Panel vars
	JPanel first;
	JPanel south;
	JPanel north;
	JPanel west;
	
	//Button vars
	JButton sendButton;
	JButton setpath;
	JButton one;
	JButton two;
	JButton three;
	JButton four;
	JButton five;
	
	//Menu vars
	JMenuBar menu;
	JMenu filemenu;
	JMenuItem exitmenu;
	
	//Text area vars
	JTextArea textarea;
	JScrollPane scrollpane;
	
	//Textfield vars
	JTextField usernamefield;
	JTextField pathfield;
	JTextField message;
	
	//Time server vars
	InetAddress addr;
	NTPUDPClient timeClient;
	TimeInfo info;
	
	public BittorrentGUI(){
		
		//Panels
		first = new JPanel(new BorderLayout());
		south = new JPanel(new FlowLayout());
		north = new JPanel(new FlowLayout());
		west = new JPanel();
		west.setLayout(new BoxLayout(west, BoxLayout.Y_AXIS));
		
		//Buttons
		sendButton = new JButton("Send");
		sendButton.addActionListener(new sendListener());
		
		setpath = new JButton("Set");
		setpath.addActionListener(new setListener());
		
		one = new JButton("chat 1");
		two = new JButton("chat 2");
		three = new JButton("chat 3");
		four = new JButton("chat 4");
		five = new JButton("chat 5");
		
		//Menus
		menu = new JMenuBar();
		filemenu = new JMenu("File");
		exitmenu = new JMenuItem("Exit");
		
		//set up window traits
		setTitle("Bittorrent Chat");
		setSize(800, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//set up menu
		filemenu.add(exitmenu);
		menu.add(filemenu);
		setJMenuBar(menu);
		
		//set up text area
		textarea = new JTextArea(10, 50);
		textarea.setBorder(BorderFactory.createLineBorder(Color.black));
		scrollpane = new JScrollPane(textarea);
		textarea.setEditable(false);
		DefaultCaret caret = (DefaultCaret)textarea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		//set up text fields
		usernamefield = new JTextField("Username", 25);
		pathfield = new JTextField("Path", 25);
		message = new JTextField(25);
		message.setText("");
		message.setEditable(false);
		
		//add components to window
		first.add(south, BorderLayout.SOUTH);
		first.add(scrollpane, BorderLayout.CENTER);
		first.add(north, BorderLayout.NORTH);
		first.add(west, BorderLayout.WEST);
		
		north.add(usernamefield);
		north.add(pathfield);
		north.add(setpath);
		
		south.add(message);
		south.add(sendButton);
		
		west.add(one);
		west.add(two);
		west.add(three);
		west.add(four);
		west.add(five);
		
		getContentPane().add(first);
		
		
		
	}
	
	private class setListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (!pathset){
				username = usernamefield.getText();
				usernamefield.setEditable(false);
				path = pathfield.getText();
				pathfield.setEditable(false);
				message.setEditable(true);
				new ListenForMessage().start();
				pathset = true;
			}
		}

	}
	
	private class sendListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if((path == null) || (message.getText().equals(""))){
				return;
			}
			
			else{
				String filename;
				String filepath;
				//Date date = new Date();
				long time = 0;
				timeClient = new NTPUDPClient();
				try {
					addr = InetAddress.getByName(TIME_SERVER);
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					info = timeClient.getTime(addr);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//time = info.getReturnTime();
				//Date date = new Date(time);
				//TimeStamp t = new TimeStamp(new Date());
				//get time in milliseconds
				//filename = String.valueOf(date.getTime());
				//System.out.println(filename);
				//t.getCurrentTime();
				filepath = path + "/" + String.valueOf(info.getMessage().getReceiveTimeStamp().getTime()) + ".msg";
				System.out.println(filepath);
				
				Writer writer = null;
				
				try{
					writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath), "utf-8"));
					writer.write(username + ": " + message.getText());
				}
				
				catch(IOException ex){}
				
				finally{
					try{
						writer.close();
					}
					catch(Exception ex){}
				}
				
				message.setText("");
				
			}
		}

	}
	
	class ListenForMessage extends Thread{
		public void run(){
			System.out.println("got to thread");
			//create folder container for path(stream)
			File folder = new File(path);
			
			//create file filter
			FilenameFilter filter = new FilenameFilter(){

				@Override
				public boolean accept(File file, String filename) {
					// TODO Auto-generated method stub
					if(filename.endsWith(".msg")){
						return true;
					}
					
					else{
						return false;
					}
				}
				
			};
			
			//get list of files
			File[] listOfFiles = folder.listFiles(filter);
			
			//sort list
			Arrays.sort(listOfFiles);
			
			int old_size = 0;
			//int keepTrackOf;
			
			while(true) {
				//get messages from stream
				try{
					Thread.sleep(100);
				}
				catch(Exception ex){}
				
				String msg = "";
				
				listOfFiles = folder.listFiles(filter);
				//System.out.println(listOfFiles.length);
				Arrays.sort(listOfFiles);
				//for(int j = 0; j < listOfFiles.length; j++){
				//	System.out.println(listOfFiles[j].getName());
				//}
									
				//see if there's a new message in stream
				if(listOfFiles.length > old_size)
				{
					for (int i = old_size; i < listOfFiles.length; i++) {						  
						//System.out.println(i);
						File file = listOfFiles[i];
					
						if (file.isFile() && file.getName().endsWith(".msg")) {
							//System.out.println("found new file");
							try {
								//System.out.println(listOfFiles.length);
								msg =  new String(Files.readAllBytes(Paths.get(path + "/" + file.getName()))) + "\n";
								//System.out.println(msg);
							}
							catch(Exception ex) {
								System.out.println("error occurred");
							}
						  
							textarea.append(msg);
							//System.out.println("appending msg");
							
						}
						
					}
					
					old_size = listOfFiles.length;
					//System.out.println(old_size);
					//System.out.println(listOfFiles.length);
				}
						
			}
		
		}
	}
	
	public static void main(String[] args){
		
		Splash s = new Splash();
		
		try{
			Thread.sleep(3000);
		}
		catch(Exception e){}
		
		s.setVisible(false);
		s.dispose();
		
		EventQueue.invokeLater(new Runnable(){
			
			public void run(){
				
				
				BittorrentGUI test = new BittorrentGUI();
				test.setVisible(true);
				
			}
		});
		
	}
}
