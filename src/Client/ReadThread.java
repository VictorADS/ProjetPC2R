package Client;
import java.awt.Dialog.ModalityType;
import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import Frames.PlateauFrame;

// THREAD QUI GERE LES REPONSE DU SERVER
class ReadThread extends Thread {
		private BufferedReader in;
		private Client c;
		public ReadThread(Client c) {
			this.in = c.getBRIN();
			this.c=c;
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
				c.getFenetre().dispose();
				c.affichePlateau();
			}
			if (s.startsWith("CONNECTE")) {
				System.out.println("Serveur : Le joueur " + string[1]
						+ " s'est connecte");
				String msg = "Serveur : Le joueur " + string[1]
						+ " s'est connecte\n";
				c.updateChat(msg, 1);
			}
			if (s.startsWith("DECONNEXION")) {
				System.out.println("Serveur : Le joueur " + string[1]
						+ " s'est deconnecte.");
				String msg = "Serveur : Le joueur " + string[1]
						+ " s'est deconnecte\n";
				c.updateChat(msg, 1);
			}
			if (s.startsWith("SESSION")) {
				System.out.println("Serveur : La session va commencer");
				c.handleSession(string[1]);
				String msg = "Serveur : la session va commencer\n";
				c.updateChat(msg, 2);
			}
			if (s.startsWith("VAINQUEUR")) {
				System.out.println("Serveur : Fin de la session. Score final affiche !");
				String msg = "Serveur : Fin de la session. Score final affiche ! \n";
				c.updateChat(msg, 2);
				c.updateScore(string[1]);
				PlateauFrame f = (PlateauFrame) c.getFenetre();
				f.setButtonPhase0();
				f.showAnimation(c.getsoltoPrint(), c.getPlateau());
			}
			if (s.startsWith("TOUR")) {
				System.out.println("Serveur : l'enigme est " + string[1]);
				System.out.println("Serveur : Les scores  sont " + string[2]);

				c.updateScore(string[2]);
				c.updatePlateau(string[1]);
				PlateauFrame f = (PlateauFrame) c.getFenetre();
				f.setButtonPhase1();
				JOptionPane pane=new JOptionPane("Debut de la phase de reflexion !",
												JOptionPane.INFORMATION_MESSAGE);
				JDialog dialog=pane.createDialog(null, "GL");
				dialog.setModalityType(ModalityType.MODELESS);
				dialog.setVisible(true);
				String msg = "Serveur : Un nouveau tour vient de commencer !\n";
				c.updateChat(msg, 2);
			}
			if (s.startsWith("TUASTROUVE")) {
				System.out.println("Serveur : " + s); //
				String msg = "Serveur : Fin de la phase de reflexion !\n";
				PlateauFrame f = (PlateauFrame) c.getFenetre();
				f.setButtonPhase2();
				JOptionPane pane=new JOptionPane("Bravo, tu as ete le premier a trouve !\nDebut de la phase d'enchere !",
						JOptionPane.INFORMATION_MESSAGE);
				JDialog dialog=pane.createDialog(null, "GL");
				dialog.setModalityType(ModalityType.MODELESS);
				dialog.setVisible(true);
				c.updateChat(msg, 2);
			}
			if (s.startsWith("ILATROUVE")) {
				System.out.println("Serveur : " + s);
				String msg = "Serveur : " + string[1]
						+ " a trouve une solution en " + string[2]
						+ " coups !\n";
				c.updateChat(msg, 2);
				PlateauFrame f = (PlateauFrame) c.getFenetre();
				f.setButtonPhase2();
				JOptionPane pane=new JOptionPane("Debut de la phase d'enchere !",
							JOptionPane.INFORMATION_MESSAGE);
				JDialog dialog=pane.createDialog(null, "GL");
				dialog.setModalityType(ModalityType.MODELESS);
				dialog.setVisible(true);

			}
			if (s.startsWith("FINREFLEXION")) {
				System.out.println("Serveur : " + s);
				String msg = "Serveur : Fin de la phase de reflexion !\n";
				PlateauFrame f = (PlateauFrame) c.getFenetre();
				f.setButtonPhase2();
				JOptionPane pane=new JOptionPane("Debut de la phase d'enchere !",
						JOptionPane.INFORMATION_MESSAGE);
				JDialog dialog=pane.createDialog(null, "GL");
				dialog.setModalityType(ModalityType.MODELESS);
				dialog.setVisible(true);
				c.updateChat(msg, 2);
			}
			if (s.startsWith("VALIDATION")) {
				System.out.println("Serveur : " + s);
				JOptionPane pane=new JOptionPane("Votre enchere a ete selectionne !",
						JOptionPane.INFORMATION_MESSAGE);
				JDialog dialog=pane.createDialog(null, "GL");
				dialog.setModalityType(ModalityType.MODELESS);
				dialog.setVisible(true);
			}
			if (s.startsWith("ECHEC")) {
				System.out.println("Serveur : " + s); 
				JOptionPane pane=new JOptionPane("Votre enchere a echoue a cause de " + string[1],
						JOptionPane.INFORMATION_MESSAGE);
				JDialog dialog=pane.createDialog(null, "Fail");
				dialog.setModalityType(ModalityType.MODELESS);
				dialog.setVisible(true);
				
			}
			if (s.startsWith("NOUVELLEENCHERE")) {
				System.out.println("Serveur : " + s);
				String msg = "Serveur : " + string[1]
						+ " encheri, solution en " + string[2] + " coups !\n";
				c.updateChat(msg, 1);
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
				c.updateChat(msg, 2);

				PlateauFrame f = (PlateauFrame) c.getFenetre();
				if (string[1].equals(c.getName())) {
					System.out.println("Tu es actif");
					f.setButtonPhase3Actif();
					JOptionPane pane=new JOptionPane("Propose ta solution",
							JOptionPane.INFORMATION_MESSAGE);
					JDialog dialog=pane.createDialog(null, "GL");
					dialog.setModalityType(ModalityType.MODELESS);
					dialog.setVisible(true);

				} else {
					f.setButtonPhase3NonActif();
					System.out.println("Pas actif");
				}
			}
			if (s.startsWith("SASOLUTION")) {
				System.out.println("Serveur : " + s);
				String msg = "Serveur : " + string[1] + " propose "
						+ string[2] + " !\n";
				c.setsoltoPrint(string[2]);
				System.out.println("La solution que j'ai recu est "+string[2]);
				c.updateChat(msg, 2);
			}
			if (s.startsWith("BONNE")) {
				System.out.println("Serveur : " + s);
				String msg = "Serveur : c'etait effectivement la bonne reponse !\n";
				c.updateChat(msg, 2);
				System.out.println("Ici");
				PlateauFrame f = (PlateauFrame) c.getFenetre();
				f.showAnimation(c.getsoltoPrint(), c.getPlateau());
				System.out.println("Fini");
			}
			if (s.startsWith("MAUVAISE")) {
				System.out.println("Serveur : " + s);
				String msg = "Serveur : Mauvaise reponse. A " + string[1]
						+ " de jouer !\n";
				PlateauFrame f = (PlateauFrame) c.getFenetre();
				if (string[1].equals(c.getName())) {
					f.setButtonPhase3Actif();
					JOptionPane pane=new JOptionPane("C'est a toi de proposer une solution",
							JOptionPane.INFORMATION_MESSAGE);
					JDialog dialog=pane.createDialog(null, "GL");
					dialog.setModalityType(ModalityType.MODELESS);
					dialog.setVisible(true);
				} else {
					f.setButtonPhase3NonActif();
				}
				c.updateChat(msg, 2);

			}
			if (s.startsWith("FINRESO")) {
				System.out.println("Serveur : " + s);
				String msg = "Serveur : Fin de la phase de résolution. Preparez vous pour le prochain tour !\n";
				c.updateScore("");
				PlateauFrame f = (PlateauFrame) c.getFenetre();
				f.setButtonPhase0();
				c.updateChat(msg, 2);
			}
			if (s.startsWith("TROPLONG")) {
				String msg = "Serveur : Trop tard. A " + string[1]
						+ " de jouer !\n";
				PlateauFrame f = (PlateauFrame) c.getFenetre();
				if (string[1].equals(c.getName())) {
					f.setButtonPhase3Actif();
					JOptionPane pane=new JOptionPane("C'est a toi de proposer une solution",
							JOptionPane.INFORMATION_MESSAGE);
					JDialog dialog=pane.createDialog(null, "GL");
					dialog.setModalityType(ModalityType.MODELESS);
					dialog.setVisible(true);
				} else {
					f.setButtonPhase3NonActif();
				}
				c.updateChat(msg, 2);
			}
			if (s.startsWith("CHAT")) {
				System.out.println("Serveur : " + s);
				String msg = string[1] + " :\n" + string[2] + "\n";
				c.updateChat(msg, 0);
			}
		}
	}