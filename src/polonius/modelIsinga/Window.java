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

public class Window extends JFrame {
	private static final long serialVersionUID = 1L;

	static ScheduledExecutorService scheduler;
	
	static int X=1600;
	static int Y=900;
	
	static SimulationPanel simulationPanel;
	
	public Window() {
		setTitle("Model Isinga");
		setSize(X,Y);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel options = new JPanel();
		add(BorderLayout.EAST, options);
		
		JButton start = new JButton("Start");
		JButton stop = new JButton("Stop");
		JTextField speed = new JTextField("200");
		JTextField advantage = new JTextField("0");
		JTextField coeff = new JTextField("10");
		Integer spinNumber[]= {2,3,4,5};
		JComboBox<Integer> spins = new JComboBox<>(spinNumber);
		
		options.setLayout(new GridLayout(25,1));
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
		
		simulationPanel = new SimulationPanel(X,Y,Integer.parseInt(advantage.getText()),Integer.parseInt(coeff.getText()),
				(Integer) spins.getSelectedItem());
		add(BorderLayout.CENTER, simulationPanel);
		this.pack();
		
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				start.setEnabled(false);
				simulationPanel = new SimulationPanel(X,Y,Integer.parseInt(advantage.getText()),Integer.parseInt(coeff.getText()),
						(Integer) spins.getSelectedItem());
				add(BorderLayout.CENTER, simulationPanel);
				pack();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						scheduler=Executors.newScheduledThreadPool(5);
						scheduler.scheduleAtFixedRate(simulationPanel, 0, Integer.parseInt(speed.getText()), TimeUnit.MILLISECONDS);
					}
				});
			}
		});
		
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				scheduler.shutdown();
				start.setEnabled(true);
			}
		});
	}
	
	public static void main(String[] args) {
		JFrame window = new Window();
		Toolkit tools=Toolkit.getDefaultToolkit();
		Dimension dim=tools.getScreenSize();
		final int framewidth=X;
		final int frameheight=Y;
		window.setLocation((int)dim.getWidth()/2 - framewidth/2, (int) dim.getHeight()/2 - frameheight/2);
		window.setVisible(true);
	}

}
