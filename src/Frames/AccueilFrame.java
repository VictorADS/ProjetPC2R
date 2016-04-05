package Frames;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Client.Client;

/*
 * 
 */
public class AccueilFrame  extends JFrame{
	
	private static final long serialVersionUID = 1L;
	private Client c;
	
	public AccueilFrame(Client c){
		this.c=c;
		this.setTitle("Identification");
		this.setSize(400, 400);
		this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLayout(new BorderLayout());
	    
        JPanel panholder=new JPanel();
	    GridBagLayout gridbag = new GridBagLayout();	//GridBagLayout pour centrer le panel
	    GridBagConstraints constraints = new GridBagConstraints();
	    constraints.fill = GridBagConstraints.CENTER;
	    gridbag.setConstraints(panholder, constraints);

	    panholder.setLayout(gridbag);
	    
	    JPanel pan=new JPanel();
	    pan.setLayout(new GridLayout(3,1));
	    JLabel id=new JLabel("Identifiant :"); // LABEL POUR IDENTIFIANT
	    JTextField area=new JTextField(20); // JTEXTFIELD 
	    	area.addKeyListener(new EnterListener(area));
	    	
	    JButton button=new JButton("Connexion"); //BUTTON CONNEXION
		    button.addActionListener(new ActionListener() {
							@Override
				public void actionPerformed(ActionEvent e) {
					String s=area.getText();
					if(s.equals("")){
						JOptionPane.showMessageDialog(null, "Il faut entrer un identifiant","Error", JOptionPane.WARNING_MESSAGE);
					}else{
						c.connexion(s);
					}
						
				}
			});
		    
		pan.add(id);
	    pan.add(area);
	    pan.add(button);
	    panholder.add(pan);
	    this.add(panholder,BorderLayout.CENTER);
		this.setVisible(true);
	}
	
	
	/*
	 * Listener qui permet d'envoyer une requete de connexion si
	 * le texte n'est pas vide avec la touche "ENTER"
	 */
	class EnterListener implements KeyListener{
		private JTextField j;
		
		public  EnterListener(JTextField j) {
			this.j=j;
		}
		
		@Override
		public void keyTyped(KeyEvent e) {
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode()==10){
				String s=j.getText();
				if(s.equals("")){
					JOptionPane.showMessageDialog(null, "Il faut entrer un identifiant","Error", JOptionPane.WARNING_MESSAGE);
				}else{
					c.connexion(s);
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			
		}
		
	}
}
