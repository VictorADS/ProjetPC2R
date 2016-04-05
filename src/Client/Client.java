package Client;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import Frames.AccueilFrame;
import Frames.PlateauFrame;
import Plateau.Case;
import Plateau.Case.SIDE;
import Plateau.Plateau;
import Plateau.Plateau.Color;

public class Client {
	Socket sock; // La socket du client
	String name; // Le nom du client
	int port; // Port du serveur
	String addr; // Adresse du serveur
	JFrame fenetre; // Jframe que l'on utilise
	PrintStream out; // Channel de sortie du client
	BufferedReader in; // Buffer dentre du client
	ReadThread reader;
	Plateau plateau;
	String soltoPrint;

	public Client(String addr, int port) {
		this.port = port;
		this.addr = addr;
		System.out.println("Numeroclient "+Thread.currentThread().getId());
		affichePageAccueil();
	}
	
	public String getName(){
		return name;
	}
	public String getsoltoPrint(){
		return soltoPrint;
	}
	public void setsoltoPrint(String s){
		soltoPrint=s;
	}
	public Plateau getPlateau(){
		return plateau;
	}
	public BufferedReader getBRIN(){
		return in;
	}
	public JFrame getFenetre(){
		return fenetre;
	}
	
	public void connexion(String s) { // Function de connection
		try {

			System.out.println("Client : connexion ");
			name = s;
			sock = new Socket(addr, port);
			out = new PrintStream(new DataOutputStream(sock.getOutputStream()));
			in = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
			
			reader = new ReadThread(this); // Demarre le reader thread
			reader.start();
			
			out.print("CONNEXION/" + name + "/\n");
			out.flush();

		} catch (IOException e) {
			System.out.println("Erreur pendant la connexion " + e.getMessage());
		}
	}

	@SuppressWarnings("deprecation")
	public void deconnexion() { // function de deconnexion
		try {
			System.out.println("Client : deconnexion\n");
			out.print("SORT/" + name + "/\n");
			out.flush();
			reader.stop();
			out.close();
			in.close();
			sock.close();
		} catch (IOException e) {
			System.out.println("Erreur pendant la deconnexion");
		}
	}

	public void trouve(int nbcoups) {
		System.out.println("Client : trouve");
		out.print("SOLUTION/" + name + "/" + nbcoups + "/\n");
		out.flush();
	}

	public void enchere(int nbcoups) {
		System.out.println("Client : enchere");
		out.print("ENCHERE/" + name + "/" + nbcoups + "/\n");
		out.flush();
	}

	public void solution(String dep) {
		System.out.println("Client : solution");
		soltoPrint=dep;
		out.print("SOLUTION/" + name + "/" + dep + "/\n");
		out.flush();
	}

	// Creer le plateau sans robot avec des mur
	public void handleSession(String plat) {
		Pattern p = Pattern.compile("(\\d*,\\d*,\\w*)");
		Matcher m = p.matcher(plat);
		plateau = new Plateau();
		while (m.find()) {
			String[] res = m.group().split(",");
			int x = Integer.parseInt(res[0]);
			int y = Integer.parseInt(res[1]);
			SIDE side = Case.getSide(res[2]);
			switch (side) {
			case H:
				plateau.getCase(x, y).setTop(true);
				if(x -1 >=0)
					plateau.getCase(x - 1, y).setBottom(true);
				break;
			case B:
				plateau.getCase(x, y).setBottom(true);
				if(x + 1 <= 15)
					plateau.getCase(x + 1, y).setTop(true);
				break;
			case D:
				plateau.getCase(x, y).setRight(true);
				if(y + 1 <= 15)
					plateau.getCase(x, y + 1).setLeft(true);
				break;
			case G:
				plateau.getCase(x, y).setLeft(true);
				if(y - 1 >= 0)
					plateau.getCase(x, y - 1).setRight(true);
				break;
			default:
				break;
			}

		}
		PlateauFrame f = (PlateauFrame) fenetre;
		f.updateGraphics(plateau);
	}

	// Permet de maj le tableau de score
	public void updateScore(String results) {
		int tour = 0;
		ArrayList<String> nameList = new ArrayList<String>();
		ArrayList<Integer> scoreList = new ArrayList<Integer>();

		if (results.length() > 0) {
			Pattern p = Pattern.compile("(\\w*,\\d*)");
			Matcher m = p.matcher(results);
			String[] nbtour = results.split("\\(");
			tour = Integer.parseInt(nbtour[0]);
			while (m.find()) {
				String[] res = m.group().split(",");
				int score = Integer.parseInt(res[1]);
				String name = res[0];
				nameList.add(name);
				scoreList.add(score);
			}
			PlateauFrame f = (PlateauFrame) fenetre;
			f.updateScore(nameList, scoreList, tour);
		}
	}

	// Permet de creer le plateau avec les positions des robots
	// Affiche le plateau a lutilisateur
	public void updatePlateau(String plat) {
		String withoutParenth = plat.replaceAll("[()]", "");
		String[] args = withoutParenth.split(",");
		int xr = Integer.parseInt(args[0]);
		int yr = Integer.parseInt(args[1]);
		int xb = Integer.parseInt(args[2]);
		int yb = Integer.parseInt(args[3]);
		int xj = Integer.parseInt(args[4]);
		int yj = Integer.parseInt(args[5]);
		int xv = Integer.parseInt(args[6]);
		int yv = Integer.parseInt(args[7]);
		int xc = Integer.parseInt(args[8]);
		int yc = Integer.parseInt(args[9]);
		Color c = plateau.getColor(args[10]);

		Point rouge = new Point(xr, yr);
		Point vert = new Point(xv, yv);
		Point jaune = new Point(xj, yj);
		Point bleu = new Point(xb, yb);
		Point cible = new Point(xc, yc);
		plateau.setColor(c);
		plateau.setCible(cible);
		plateau.setRobotB(bleu);
		plateau.setRobotR(rouge);
		plateau.setRobotV(vert);
		plateau.setRobotJ(jaune);
		PlateauFrame f = (PlateauFrame) fenetre;
		f.updateGraphics(plateau);
	}
	//Envoi d'un message au serveur
	public void sendMessage(String s) {
		System.out.println("Client : envoi message " + s);
		out.print("SEND/" + name + "/" + s+"/\n");
		out.flush();
	}
	//Mise a jour du chat
	// isServer est une variable qui indique l'importance du message
	// 0 = user, 1=Serveur faible importance, 2 = Serveur haute importance
	public void updateChat(String s, int isServer) {
		PlateauFrame f = (PlateauFrame) fenetre;
		String msg = "" + s + "";
		f.updateChat(msg, isServer);
	}

	public void affichePlateau() {
		fenetre = new PlateauFrame(this);
	}

	public void affichePageAccueil() { // Methode de la page d'accueil
		fenetre = new AccueilFrame(this);
	}

	
}
