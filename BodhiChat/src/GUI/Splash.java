package GUI;
import java.awt.*;

import javax.swing.*;


public class Splash extends JWindow{
		
	public Splash() {		
		
		JLabel image = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/splashscreen.jpg"))));
		JPanel pane = new JPanel(new BorderLayout());
		setSize(800, 500);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle window = getBounds();
		setLocation((screen.width - window.width) / 2,(screen.height - window.height)/2);
		pane.add(image);
		getContentPane().add(pane);
		pack();
		setVisible(true);
		
	}
}
