package polonius.modelIsinga;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * 
 * @author Jacek Pi³ka
 *
 * Interfejs programu
 *
 */

public class Window extends JFrame {
	private static final long serialVersionUID = 1L;

	static ScheduledExecutorService scheduler;	//Scheduler do wielow¹tkowoœci (umo¿liwia m.in. sterowanie podczas symulacji)
	
	static int X=1600;	//Wymiary okna
	static int Y=900;
	
	static SimulationPanel simulationPanel;
	
	public Window() {
		setTitle("Model Isinga");	//Tytu³ okna
		setSize(X,Y);	//Rozmiar okna
		setDefaultCloseOperation(EXIT_ON_CLOSE);	//Wy³¹czenie programu po klikniêciu czerwonego "X"
		
		JPanel options = new JPanel();	//Panel opcji
		add(BorderLayout.EAST, options);	//Po³o¿enie panelu
		
		JButton start = new JButton("Start");	//Przyciski, pola tekstowe i comboBoxy
		JButton stop = new JButton("Stop");
		JTextField speed = new JTextField("200");
		JTextField advantage = new JTextField("0");
		JTextField coeff = new JTextField("10");
		Integer spinNumber[]= {2,3,4,5};
		JComboBox<Integer> spins = new JComboBox<>(spinNumber);
		
		options.setLayout(new GridLayout(25,1));	//Wstawianie powy¿szego do panelu
		options.add(new JPanel());
		options.add(start);
		options.add(stop);
		options.add(new JPanel());
		options.add(new JLabel("Krok [ms]"));
		options.add(speed);
		options.add(new JLabel("Przewaga"));
		options.add(advantage);	
		options.add(new JLabel("Skala"));
		options.add(coeff);
		options.add(new JLabel("Spiny"));
		options.add(spins);
		options.add(new JPanel());
		options.add(new JLabel("by Jacek Pi³ka"));
		
		//Tworzenie obiektu symulacyjnego
		simulationPanel = new SimulationPanel(X,Y,Integer.parseInt(advantage.getText()),Integer.parseInt(coeff.getText()),
				(Integer) spins.getSelectedItem());
		add(BorderLayout.CENTER, simulationPanel);	//Dodanie go do panelu g³ównego
		this.pack();
		
		start.addActionListener(new ActionListener() {	//Listener na przycisk startu
			public void actionPerformed(ActionEvent arg0) {
				start.setEnabled(false);	//Wy³¹czenie przycisku startu
				simulationPanel = new SimulationPanel(X,Y,Integer.parseInt(advantage.getText()),Integer.parseInt(coeff.getText()),
						(Integer) spins.getSelectedItem());	//Utworzenie obiektu symulacyjnego
				add(BorderLayout.CENTER, simulationPanel);	//Dodanie go do panelu g³ównego
				pack();
				SwingUtilities.invokeLater(new Runnable() {	//W³¹czenie schedulera (symulacji)
					public void run() {
						scheduler=Executors.newScheduledThreadPool(5);	//Ustawienie iloœci w¹tków i ich kroku dzia³ania
						scheduler.scheduleAtFixedRate(simulationPanel, 0, Integer.parseInt(speed.getText()), TimeUnit.MILLISECONDS);
					}
				});
			}
		});
		
		stop.addActionListener(new ActionListener() {	//Listener na przycisk stopu
			public void actionPerformed(ActionEvent arg0) {
				scheduler.shutdown();	//Wy³¹czenie schedulera (symulacji)
				start.setEnabled(true);	//W³¹czenie przycisku startu
			}
		});
	}
	
	public static void main(String[] args) {
		JFrame window = new Window();	//Stworzenie okna
		Toolkit tools=Toolkit.getDefaultToolkit();	//Umo¿liwia ustawienie po³o¿enai okna w przestrzeni
		Dimension dim=tools.getScreenSize();
		final int framewidth=X;
		final int frameheight=Y;
		window.setLocation((int)dim.getWidth()/2 - framewidth/2, (int) dim.getHeight()/2 - frameheight/2);
		window.setVisible(true);	//Wyœwietlenie okna
	}

}
