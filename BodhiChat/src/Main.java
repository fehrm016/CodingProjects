import java.awt.EventQueue;
import GUI.BodhiGUI;
import GUI.Splash;

public class Main {
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
				BodhiGUI test = new BodhiGUI();
				test.setVisible(true);
			}
		});
		
	}

}
