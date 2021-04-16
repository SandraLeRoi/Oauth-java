import javax.swing.*;
import java.awt.event.*;

public class Swing extends JFrame {
    public Swing() {
        super("titre de l'application");

        WindowListener l = new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        };

        addWindowListener(l);

//        ImageIcon img = new ImageIcon("tips.gif");
        JButton bouton = new JButton("Bouton");


        JPanel panneau = new JPanel();
        panneau.add(bouton);
        setContentPane(panneau);
        setSize(200,100);
        setVisible(true);
    }

}
