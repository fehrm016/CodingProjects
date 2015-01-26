package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.Action;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.FlowLayout;
import java.awt.Label;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JSeparator;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class BodhiGUI extends JFrame implements ActionListener{

	String path, username;
	boolean pathset = false;
	String TIME_SERVER = "time-a.nist.gov";
	
	private JPanel contentPane;
	private JTextArea consultingTextArea;
	private JTextField userField;
	private JTextField pathField;
	private JTextField messageField;
	private JButton setButton;
	private JButton sendButton;
	
	//Time server vars
	InetAddress addr;
	NTPUDPClient timeClient;
	TimeInfo info;
	
	/**
	 * Create the frame.
	 */
	public BodhiGUI() {
		setTitle("Bodhi Digital");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		Label userLabel = new Label("User Name:");
		panel_1.add(userLabel);
		
		userField = new JTextField();
		panel_1.add(userField);
		userField.setColumns(15);
		
		Label pathLabel = new Label("Path:");
		panel_1.add(pathLabel);
		
		pathField = new JTextField();
		panel_1.add(pathField);
		pathField.setColumns(15);
		
		setButton = new JButton("Set");
		setButton.addActionListener(this);
		panel_1.add(setButton);
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		messageField = new JTextField();
		messageField.setEditable(false);
		panel_2.add(messageField);
		messageField.setColumns(25);
		
		sendButton = new JButton("Send");
		sendButton.addActionListener(this);
		panel_2.add(sendButton);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(new EmptyBorder(10, 20, 10, 20));
		tabbedPane.setToolTipText("");
		panel.add(tabbedPane, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Consulting", null, scrollPane, null);
		
		consultingTextArea = new JTextArea();
		consultingTextArea.setWrapStyleWord(true);
		consultingTextArea.setLineWrap(true);
		consultingTextArea.setEditable(false);
		scrollPane.setViewportView(consultingTextArea);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		tabbedPane.addTab("Support", null, scrollPane_1, null);
		
		JTextArea supportTextArea = new JTextArea();
		supportTextArea.setWrapStyleWord(true);
		supportTextArea.setLineWrap(true);
		supportTextArea.setEditable(false);
		scrollPane_1.setViewportView(supportTextArea);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		Object comp = e.getSource();
		
		if(comp == setButton){
			
			if (!pathset){
				username = userField.getText();
				userField.setEditable(false);
				path = pathField.getText();
				pathField.setEditable(false);
				messageField.setEditable(true);
				new ListenForMessage().start();
				pathset = true;
			}
			
		}
		
		if(comp == sendButton){
			if((pathField == null) || (messageField.getText().equals(""))){
				return;
			}
			
			else{
				String filepath;
				timeClient = new NTPUDPClient();
				try {
					addr = InetAddress.getByName(TIME_SERVER);
				} catch (UnknownHostException e1) {
					
					e1.printStackTrace();
				}
				try {
					info = timeClient.getTime(addr);
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
				
				filepath = path + "/" + String.valueOf(info.getMessage().getReceiveTimeStamp().getTime()) + ".msg";
				System.out.println(filepath);
				
				Writer writer = null;
				
				try{
					writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath), "utf-8"));
					writer.write(username + ": " + messageField.getText());
				}
				
				catch(IOException ex){}
				
				finally{
					try{
						writer.close();
					}
					catch(Exception ex){}
				}
				
				messageField.setText("");
				
			}
		}
		
	}
	
	public class ListenForMessage extends Thread{
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
				Arrays.sort(listOfFiles);
									
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
						  
							consultingTextArea.append(msg);
							
						}
						
					}
					
					old_size = listOfFiles.length;

				}
						
			}
		
		}
	}

}
