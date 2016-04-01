import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import Plateau.Case;
import Plateau.Plateau;


public class PlateauFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Client c;
	private JPanel scorepanel; //Panel qui contient les score
	private JPanel buttonpanel; //Panel qui contient les boutons 
	private JTextPane chat; //TextPane pour les msg du chat
	private JLabel car; //Label pour affiche le temps restant 
	private Thread chrono; //Thread pour affiche le chrono
	private JPanel plateau; //Plateau pour afficher lenigme
	private JLabel tab; //Label qui contient le nombre de tour 
	JTextArea solPrint; //Label qui contient la solution
	
	private static String[] arrayofint;
	private String sol; //Contient la solution en p3
	private Plateau.Color color=Plateau.Color.R; // Couleur choisis par lutilisateur en p3

	public PlateauFrame(Client c){
		arrayofint=new String[19];
		for(int i=0;i<19;i++)
			arrayofint[i]=""+i;

		this.c=c;
		this.setTitle("The Game");
		this.setSize(1040,840);
		this.setLocationRelativeTo(null);
	    this.setLayout(new BorderLayout());
	    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

	    JPanel panholderright=new JPanel();//Holds everything in the right
	    panholderright.setPreferredSize(new Dimension(375,800));
        panholderright.setLayout(new GridLayout(3, 1));
        
        buttonpanel =new JPanel(); //Holds the action button
        buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.PAGE_AXIS));
        JLabel deb=new JLabel("Veuillez attendre le debut d'une autre partie.");
        buttonpanel.add(deb);
        
        JPanel scoreboard=new JPanel();//Holds label + scoreboard
        scoreboard.setLayout(new BorderLayout());
        tab=new JLabel("Tableau des scores :",SwingConstants.LEFT);
        scoreboard.add(tab,BorderLayout.NORTH);
        scorepanel=new JPanel(); //holds the score
        scorepanel.setLayout(new BoxLayout(scorepanel, BoxLayout.PAGE_AXIS));
        JScrollPane scroll=new JScrollPane(scorepanel);
        scoreboard.add(scroll);
        
        JPanel chatholder=new JPanel(); //Hold the chatlog
        chatholder.setLayout(new BoxLayout(chatholder, BoxLayout.PAGE_AXIS));
        chat=new JTextPane();
        DefaultCaret caret = (DefaultCaret)chat.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); //Auto scroll
        chat.setContentType("text/plain");
        chat.setEditable(false);
        chat.setText("Bienvenue dans le salon de discussion, "+c.getName()+".\n");
        JScrollPane scrollchat=new JScrollPane(chat);
        JTextField cmd=new JTextField();
        cmd.addKeyListener(new EnterListener(cmd));
        JButton send=new JButton("Send");
        send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s=cmd.getText();
				if(!s.equals(""))
					c.sendMessage(s);
				cmd.setText("");
			}
		});
        cmd.setMaximumSize(new Dimension(Integer.MAX_VALUE,send.getPreferredSize().height));
        JPanel sendbar= new JPanel();
        sendbar.setLayout(new BoxLayout(sendbar, BoxLayout.LINE_AXIS));
        sendbar.add(cmd);
        sendbar.add(send);
        chatholder.add(new JLabel("\n"));
        chatholder.add(scrollchat);
        chatholder.add(sendbar);
        
        //PLateau panel
        plateau=new JPanel();
        plateau.setLayout(new GridLayout(16, 16));
        panholderright.add(buttonpanel);
        panholderright.add(scoreboard);
        panholderright.add(chatholder);
        this.add(plateau, BorderLayout.WEST);
        this.add(panholderright,BorderLayout.EAST);
	    this.addWindowListener(new CloseHandle());
	    this.setResizable(false);
		this.setVisible(true);
	}
	public void updateGraphics(Plateau p){ //Appel lorsque on veut juste afficher les mur
		plateau.removeAll();
		int top=1;
		int bot=1;
		int left=1;
		int right=1;
		for(int i=0;i<16;i++){
			for(int j=0;j<16;j++){
				Case c=p.getCase(i, j);
				if(c.getBottom())
					bot=5;
				if(c.getTop())
					top=5;
				if(c.getLeft())
					left=5;
				if(c.getRight())
					right=5;
					
				JButton tmp=new JButton("");
				tmp.setEnabled(false);
				if(p.getRobotB()!=null && i==p.getRobotB().getX() && j==p.getRobotB().getY()){
					tmp.setIcon(new ImageIcon("image/blue.png"));
					tmp.setDisabledIcon(new ImageIcon("image/blue.png"));
				}
				if(p.getRobotV()!=null && i==p.getRobotV().getX() && j==p.getRobotV().getY()){
					tmp.setIcon(new ImageIcon("image/green.png"));
					tmp.setDisabledIcon(new ImageIcon("image/green.png"));
				}
				if(p.getRobotR()!=null && i==p.getRobotR().getX() && j==p.getRobotR().getY()){
					tmp.setIcon(new ImageIcon("image/red.png"));
					tmp.setDisabledIcon(new ImageIcon("image/red.png"));
				}
				if(p.getRobotJ()!=null && i==p.getRobotJ().getX() && j==p.getRobotJ().getY()){
					tmp.setIcon(new ImageIcon("image/yellow.png"));
					tmp.setDisabledIcon(new ImageIcon("image/yellow.png"));
				}
				if(p.getCible()!=null && i==p.getCible().getX() && j==p.getCible().getY()){
					ImageIcon icon=getAppropriateIcon(p);
					tmp.setIcon(icon);
					tmp.setDisabledIcon(icon);
				}
				tmp.setPreferredSize(new Dimension(40,40));
				tmp.setBorder(new MatteBorder(top, left, bot, right,new Color(0,0,0)));
				plateau.add(tmp);
				bot=top=left=right=1;
			}
		}
		plateau.revalidate();
		plateau.repaint();
	}
	public ImageIcon getAppropriateIcon(Plateau p){
		switch(p.getColor()){
		case R :
			return new ImageIcon("image/redcible.png");
		case V:
			return new ImageIcon("image/greencible.png");
		case B:
			return new ImageIcon("image/bluecible.png");
		case J:
			return new ImageIcon("image/yellowcible.png");
		default :
			System.out.println("Erreur lors de getAppropriate");
			return null;
		}
	}
	public void updateScore(ArrayList<String> name,ArrayList<Integer> score,int tour){
		tab.setText("Tableau des scores : ("+tour+"e tour)");
		scorepanel.removeAll();
	    String s="<html>";
		for(int i=0;i<name.size();i++){
			s+=name.get(i)+" a "+score.get(i)+" points<br><br>";
		}
		s+="</html>";
		JLabel scorelab=new JLabel(s,SwingConstants.RIGHT);
		scorepanel.add(scorelab);
		scorepanel.revalidate();
		scorepanel.repaint();
	}
	public void updateChat(String s,int importance){//Importance = 0 => User; =1 => Mineur ; =2 => Majeur
		StyledDocument doc=chat.getStyledDocument();
		Style style=chat.addStyle("Im a stytle", null);
		Color c =Color.BLACK;
		if(importance==2){
			c=Color.RED;
		}
		if(importance==1){
			c=Color.BLUE;
		}
		StyleConstants.setForeground(style,c );
		try{
			doc.insertString(doc.getLength(), s, style);
		}catch (BadLocationException ble) {
			  System.err.println("Bad Location. Exception:" + ble);
		}
	}
	public void setButtonPhase0(){
		buttonpanel.removeAll();
        JLabel deb=new JLabel("Veuillez attendre le debut d'une autre partie.");
        buttonpanel.add(deb);
        buttonpanel.revalidate();
        buttonpanel.repaint();
	}
	public void setButtonPhase1(){
		buttonpanel.removeAll();
		car=new JLabel("Temps restant : "+300);
		startCountDown(car,300);
		JButton b1=new JButton("Propose solution");
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String ask=(String)JOptionPane.showInputDialog(null,"Choisissez la valeur","Enchere Time",JOptionPane.QUESTION_MESSAGE,null,arrayofint,arrayofint[1]);
				if(ask!=null){
					int nbcoups=Integer.parseInt(ask);
					c.trouve(nbcoups);
				}
			}
		});
		buttonpanel.add(car);
		buttonpanel.add(new JLabel("\n"));
		buttonpanel.add(b1);
		buttonpanel.revalidate();
		buttonpanel.repaint();
	}
	public void setButtonPhase2(){
		buttonpanel.removeAll();
		car=new JLabel("Temps restant : "+30);
		startCountDown(car,30);

		JButton b1=new JButton("Propose enchere");
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String ask=(String)JOptionPane.showInputDialog(null,"Choisissez la valeur","Enchere Time",JOptionPane.QUESTION_MESSAGE,null,arrayofint,arrayofint[1]);
				if(ask!=null){
					int nbcoups=Integer.parseInt(ask);
					c.enchere(nbcoups);
				}
			}
		});
		buttonpanel.add(car);
		buttonpanel.add(new JLabel("\n"));
		buttonpanel.add(b1);
		buttonpanel.revalidate();
		buttonpanel.repaint();
	}
	public void setButtonPhase3Actif(){
		Border defaultborder=UIManager.getBorder("Button.border");
		buttonpanel.removeAll();
		sol="";
		JLabel car=new JLabel("Temps restant :  "+60);
		startCountDown(car,60);
		JPanel paneltop=new JPanel();
		paneltop.setLayout(new BoxLayout(paneltop, BoxLayout.LINE_AXIS));
		JButton undo=new JButton("Undo "); //Undo supprime les 2 dernier char
		undo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
            	if(!sol.equals("")){
            		sol=sol.substring(0, sol.length()-2);
            		updateText(sol);
            	}
			}
		});
		
		JButton b1=new JButton("Submit"); //permet de soumettre une solution
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
            	int response=JOptionPane.showConfirmDialog(null, "Si vous confirmez votre solution sera envoye","Confirm",JOptionPane.YES_NO_OPTION);
            	if(response==JOptionPane.OK_OPTION)
            		c.solution(sol);
			}
		});
		paneltop.add(b1);
		paneltop.add(Box.createRigidArea(new Dimension(100,0)));
		paneltop.add(undo);
		
		// 4 boutons aligne
		JPanel colorpanel=new JPanel();
		colorpanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
		JButton red=new JButton();
		red.setBorder(BorderFactory.createLineBorder(Color.red,2));
		red.setPreferredSize(new Dimension(35,35));
		red.setIcon(new ImageIcon("image/red.png"));
		JButton green=new JButton();
		green.setPreferredSize(new Dimension(35,35));
		green.setIcon(new ImageIcon("image/green.png"));
		JButton yellow=new JButton();
		yellow.setPreferredSize(new Dimension(35,35));
		yellow.setIcon(new ImageIcon("image/yellow.png"));
		JButton blue=new JButton();
		blue.setPreferredSize(new Dimension(35,35));
		blue.setIcon(new ImageIcon("image/blue.png"));
		colorpanel.add(red);
		colorpanel.add(Box.createRigidArea(new Dimension(5,0)));
		colorpanel.add(green);
		colorpanel.add(Box.createRigidArea(new Dimension(5,0)));
		colorpanel.add(yellow);
		colorpanel.add(Box.createRigidArea(new Dimension(5,0)));
		colorpanel.add(blue);
		red.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					color=Plateau.Color.R;
					red.setBorder(BorderFactory.createLineBorder(Color.red,2));
					green.setBorder(defaultborder);
					yellow.setBorder(defaultborder);
					blue.setBorder(defaultborder);

			}
		});
		yellow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					color=Plateau.Color.J;
					yellow.setBorder(BorderFactory.createLineBorder(Color.orange,2));
					green.setBorder(defaultborder);
					red.setBorder(defaultborder);
					blue.setBorder(defaultborder);

			}
		});
		blue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					color=Plateau.Color.B;
					blue.setBorder(BorderFactory.createLineBorder(Color.BLUE,2));
					green.setBorder(defaultborder);
					yellow.setBorder(defaultborder);
					red.setBorder(defaultborder);

			}
		});
		green.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					color=Plateau.Color.V;
					green.setBorder(BorderFactory.createLineBorder(new Color(0,139,0),2));
					red.setBorder(defaultborder);
					yellow.setBorder(defaultborder);
					blue.setBorder(defaultborder);

			}
		});
		
		
		
		//Contient les fleche directionnelle
		JPanel arrow=new JPanel();
		arrow.setLayout(new BoxLayout(arrow, BoxLayout.PAGE_AXIS));
		JButton top=new JButton("Haut");
		top.setMaximumSize(new Dimension(70, 25));
		JPanel leftright=new JPanel();
		leftright.setMaximumSize(new Dimension(260,30));
		leftright.setLayout(new BoxLayout(leftright, BoxLayout.LINE_AXIS));
		JButton left=new JButton("Gauche");
		left.setMaximumSize(new Dimension(90, 25));
		JButton right=new JButton("Droite");
		right.setMaximumSize(new Dimension(90, 25));

		leftright.add(left);
		leftright.add(Box.createRigidArea(new Dimension(85,0)));
		leftright.add(right);
		JButton bot=new JButton("Bas");
		bot.setMaximumSize(new Dimension(70, 25));

		arrow.add(top);
		arrow.add(new JLabel("\n"));
		arrow.add(leftright);
		arrow.add(new JLabel("\n"));
		arrow.add(bot);
		top.setAlignmentX(CENTER_ALIGNMENT);
		bot.setAlignmentX(CENTER_ALIGNMENT);
		top.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sol+=color+"H";
				updateText(sol);
			}
		});
		bot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sol+=color+"B";
				updateText(sol);
			}
		});
		left.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sol+=color+"G";
				updateText(sol);
			}
		});
		right.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sol+=color+"D";
				updateText(sol);
			}
		});

		car.setAlignmentX(CENTER_ALIGNMENT);
		b1.setAlignmentX(CENTER_ALIGNMENT);
		colorpanel.setAlignmentX(CENTER_ALIGNMENT);
		arrow.setAlignmentX(CENTER_ALIGNMENT);
		JPanel bottomfinal=new JPanel();
		bottomfinal.setLayout(new BoxLayout(bottomfinal, BoxLayout.LINE_AXIS));
		solPrint=new JTextArea("Votre solution :");
		solPrint.setEditable(false);
	    solPrint.setLineWrap(true);
	    solPrint.setBackground(UIManager.getColor("Label.background"));
	    solPrint.setFont(UIManager.getFont("Label.font"));
		
	    bottomfinal.add(solPrint);
		bottomfinal.add(Box.createHorizontalGlue());
		bottomfinal.add(new JLabel());
		
		buttonpanel.add(car);
		buttonpanel.add(new JLabel("\n"));
		buttonpanel.add(paneltop);
		buttonpanel.add(new JLabel("\n"));
		buttonpanel.add(colorpanel);
		buttonpanel.add(new JLabel("\n"));
		buttonpanel.add(arrow);
		buttonpanel.add(bottomfinal);
		buttonpanel.revalidate();
		buttonpanel.repaint();	
	}
	public void showAnimation(String solution,Plateau p){
		buttonpanel.removeAll();
		buttonpanel.revalidate();
		buttonpanel.repaint();
	    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		int i,j=0;
		String[] moves=new String[solution.length()/2];
		for(i=0;i<solution.length();i+=2){
			moves[j]=solution.charAt(i)+""+solution.charAt(i+1);
			j++;
		}
		for(String s : moves){
			while(p.canMove(s)){
			}
			updateGraphics(p);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	/* Met a jour le label qui affiche la solution */
	public void updateText(String s){
		String styledString="";
		for(int i=0;i<s.length();i+=2){
			styledString+=s.charAt(i);
			if(s.charAt(i+1)=='H'){
				styledString+="\u2191"; //Unicode character for arrow top
			}
			else if(s.charAt(i+1)=='B'){
				styledString+="\u2193";
			}
			else if(s.charAt(i+1)=='G'){
				styledString+="\u2190";
			}
			else if(s.charAt(i+1)=='D'){
				styledString+="\u2192";
			}
			styledString+=" ";
		}
		solPrint.setText("Votre solution : "+styledString);
	}
	public void setButtonPhase3NonActif(){
		buttonpanel.removeAll();
		JLabel car=new JLabel("Temps restant :  "+60);
		startCountDown(car,60);
		buttonpanel.add(car);
		buttonpanel.revalidate();
		buttonpanel.repaint();
	}
	public void startCountDown(JLabel car, int time){
		chrono=new ChronoThread(car,time);
		chrono.start();
	}
	
	
	/* Thread qui calcule le temps du countdown */
	class ChronoThread extends Thread{
		private JLabel car;
		private int time;
		public ChronoThread(JLabel car,int time){
			this.car=car;
			this.time=time;
		}
		public void run(){
			long t1=System.currentTimeMillis();
			long timepasse=0;
			while(true){
				long t2=System.currentTimeMillis();
				if(t2-t1>1000){
					timepasse=(t2-t1)/1000;
					long tempsrestant=time-timepasse;
					time=(int) tempsrestant;
					if(tempsrestant>=0){
						car.setText("Temps restant : "+tempsrestant+"s");
					}
					else
						break;
					timepasse=0;
					t1=t2;
				}
				yield();

			}
		}
	}
	
	
	/* KeyListener pour que la toucher ENTREE envoye le message du chat*/
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
				if(!s.equals("")){
					c.sendMessage(s);
					j.setText("");
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
		
	}
	
	/* WindowsListener pour envoyer SORTI lors de la fermeture de la page */
	class CloseHandle implements WindowListener{
		@Override
        public void windowClosing(WindowEvent e){
                JDialog.setDefaultLookAndFeelDecorated(true);
            	int response=JOptionPane.showConfirmDialog(null, "Si vous confirmez vous serez deconnecter","Confirm",JOptionPane.YES_NO_OPTION);
            	if(response==JOptionPane.OK_OPTION){
            		c.deconnexion();
                    e.getWindow().dispose();
            	}
            }

		@Override
		public void windowOpened(WindowEvent e) {
		}

		@Override
		public void windowClosed(WindowEvent e) {
		}

		@Override
		public void windowIconified(WindowEvent e) {
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
		}

		@Override
		public void windowActivated(WindowEvent e) {
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
		}
     }
}
