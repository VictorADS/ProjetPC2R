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
import javax.swing.JOptionPane;

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
	Plateau backupplateau;
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
	public void connexion(String s) { // Function de connection
		try {

			System.out.println("Client : connexion ");
			name = s;
			sock = new Socket(addr, port);
			out = new PrintStream(new DataOutputStream(sock.getOutputStream()));
			in = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
			
			reader = new ReadThread(in); // Demarre le reader thread
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
		backupplateau = plateau;
		PlateauFrame f = (PlateauFrame) fenetre;
		f.updateGraphics(plateau);
	}

	public void sendMessage(String s) {
		System.out.println("Client : envoi message " + s);
		out.print("SEND/" + name + "/" + s+"/\n");
		out.flush();
	}

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

	// THREAD QUI GERE LES REPONSE DU SERVER
	class ReadThread extends Thread {
		private BufferedReader in;

		public ReadThread(BufferedReader in) {
			this.in = in;
		}

		@SuppressWarnings("deprecation")
		public void run() {
			String s = "";
			while (true) {
				try {
					s=in.readLine();
					if(s==null){
						System.out.println("Le serveur s'est étéint ");
						this.stop();
					}
					traitement(s);
					yield();
				} catch (IOException e) {
					System.out.println("Erreur dans le ReadThread ");
					this.stop();
				}
			}
		}

		public void traitement(String s) {
			String[] string = s.split("/");
			if (s.startsWith("BIENVENUE")) {
				System.out.println("Serveur : " + s);
				fenetre.dispose();
				affichePlateau();
			}
			if (s.startsWith("CONNECTE")) {
				System.out.println("Serveur : Le joueur " + string[1]
						+ " s'est connecte");
				String msg = "Serveur : Le joueur " + string[1]
						+ " s'est connecte\n";
				updateChat(msg, 1);
			}
			if (s.startsWith("DECONNEXION")) {
				System.out.println("Serveur : Le joueur " + string[1]
						+ " s'est deconnecte.");
				String msg = "Serveur : Le joueur " + string[1]
						+ " s'est deconnecte\n";
				updateChat(msg, 1);
			}
			if (s.startsWith("SESSION")) {
				System.out.println("Serveur : La session va commencer");
				handleSession(string[1]);
				String msg = "Serveur : la session va commencer\n";
				updateChat(msg, 2);
			}
			if (s.startsWith("VAINQUEUR")) {
				System.out.println("Serveur : Fin de la session. Score final affich� !");
				String msg = "Serveur : Fin de la session. Score final affich� ! \n";
				updateChat(msg, 2);
				updateScore(string[1]);
				PlateauFrame f = (PlateauFrame) fenetre;
				f.setButtonPhase0();
			}
			if (s.startsWith("TOUR")) {
				System.out.println("Serveur : l'enigme est " + string[1]);
				System.out.println("Serveur : Les scores  sont " + string[2]);

				updateScore(string[2]);
				updatePlateau(string[1]);
				PlateauFrame f = (PlateauFrame) fenetre;
				f.setButtonPhase1();
				JOptionPane.showMessageDialog(null,
						"Debut de la phase de reflexion !", "GL",
						JOptionPane.INFORMATION_MESSAGE);
				String msg = "Serveur : Un nouveau tour vient de commencer !\n";
				updateChat(msg, 2);
			}
			if (s.startsWith("TUASTROUVE")) {
				System.out.println("Serveur : " + s); //
				String msg = "Serveur : Fin de la phase de reflexion !\n";
				PlateauFrame f = (PlateauFrame) fenetre;
				f.setButtonPhase2();
				JOptionPane.showMessageDialog(null,
						"Bravo, tu as ete le premier a trouve !\nDebut de la phase d'enchere !", "GL",
						JOptionPane.INFORMATION_MESSAGE);

				updateChat(msg, 2);
			}
			if (s.startsWith("ILATROUVE")) {
				System.out.println("Serveur : " + s);
				String msg = "Serveur : " + string[1]
						+ " a trouve une solution en " + string[2]
						+ " coups !\n";
				updateChat(msg, 2);
				PlateauFrame f = (PlateauFrame) fenetre;
				f.setButtonPhase2();
				JOptionPane.showMessageDialog(null,
						"Debut de la phase d'enchere !", "GL",
						JOptionPane.INFORMATION_MESSAGE);

			}
			if (s.startsWith("FINREFLEXION")) {
				System.out.println("Serveur : " + s);
				String msg = "Serveur : Fin de la phase de reflexion !\n";
				PlateauFrame f = (PlateauFrame) fenetre;
				f.setButtonPhase2();
				JOptionPane.showMessageDialog(null,
						"Debut de la phase d'enchere !", "GL",
						JOptionPane.INFORMATION_MESSAGE);

				updateChat(msg, 2);
			}
			if (s.startsWith("VALIDATION")) {
				System.out.println("Serveur : " + s);
				JOptionPane.showMessageDialog(null,
						"Votre enchere a ete selectionne !", "GL",
						JOptionPane.INFORMATION_MESSAGE);
			}
			if (s.startsWith("ECHEC")) {
				System.out.println("Serveur : " + s); 
				JOptionPane.showMessageDialog(null,
						"Votre enchere a echoue a cause de " + string[1],
						"Fail", JOptionPane.INFORMATION_MESSAGE);
			}
			if (s.startsWith("NOUVELLEENCHERE")) {
				System.out.println("Serveur : " + s);
				String msg = "Serveur : " + string[1]
						+ " encheri, solution en " + string[2] + " coups !\n";
				updateChat(msg, 1);
			}
			if (s.startsWith("FINENCHERE")) {
				System.out.println("Serveur : " + s);
				String msg="";
				if(string[1].equals("")){
					msg="Serveur : Personne n'a encheri lors de cette partie.\n";
				}else{
					msg= "Serveur : Fin de la phase d'enchere. "
						+ string[1] + " pense avoir trouve en " + string[2]
						+ " coups !\n";
				}
				updateChat(msg, 2);

				PlateauFrame f = (PlateauFrame) fenetre;
				if (string[1].equals(name)) {
					System.out.println("Tu es actif");
					f.setButtonPhase3Actif();
					JOptionPane
							.showMessageDialog(null, "Propose ta solution !");

				} else {
					f.setButtonPhase3NonActif();
					System.out.println("Pas actif");
				}
			}
			if (s.startsWith("SASOLUTION")) {
				System.out.println("Serveur : " + s);
				String msg = "Serveur : " + string[1] + " propose "
						+ string[2] + " !\n";
				soltoPrint = string[2];
				System.out.println("La solution que j'ai recu est "+soltoPrint);
				updateChat(msg, 2);
			}
			if (s.startsWith("BONNE")) {
				System.out.println("Serveur : " + s);
				String msg = "Serveur : c'etait effectivement la bonne reponse !\n";
				updateChat(msg, 2);
				PlateauFrame f = (PlateauFrame) fenetre;
				System.out.println("Ici");
				f.showAnimation(soltoPrint, plateau);
				System.out.println("Fini");
			}
			if (s.startsWith("MAUVAISE")) {
				System.out.println("Serveur : " + s);
				String msg = "Serveur : Mauvaise reponse. A " + string[1]
						+ " de jouer !\n";
				PlateauFrame f = (PlateauFrame) fenetre;
				if (string[1].equals(name)) {
					f.setButtonPhase3Actif();
					JOptionPane.showMessageDialog(null,
							"C'est a toi de proposer une solution");
				} else {
					f.setButtonPhase3NonActif();
				}
				updateChat(msg, 2);

			}
			if (s.startsWith("FINRESO")) {
				System.out.println("Serveur : " + s);
				String msg = "Serveur : Fin de la phase de résolution. Preparez vous pour le prochain tour !\n";
				updateScore("");
				PlateauFrame f = (PlateauFrame) fenetre;
				f.setButtonPhase0();
				updateChat(msg, 2);
			}
			if (s.startsWith("TROPLONG")) {
				String msg = "Serveur : Trop tard. A " + string[1]
						+ " de jouer !\n";
				PlateauFrame f = (PlateauFrame) fenetre;
				if (string[1].equals(name)) {
					f.setButtonPhase3Actif();
					JOptionPane.showMessageDialog(null,
							"C'est a toi de proposer une solution");
				} else {
					f.setButtonPhase3NonActif();
				}
				updateChat(msg, 2);
			}
			if (s.startsWith("CHAT")) {
				System.out.println("Serveur : " + s);
				String msg = string[1] + " :\n" + string[2] + "\n";
				updateChat(msg, 0);
			}
		}
	}
}
