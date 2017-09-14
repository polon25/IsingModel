package polonius.modelIsinga;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;

public class SimulationPanel extends JPanel implements Runnable{
	private static final long serialVersionUID = 1L;
	
	int X=0; //Wymiary zestawu spinów
	int Y=0;
	int n=0; //Iteracje
	
	int coeff=10;	//Wspó³czynnik skali (im mniej, tym wiêcej punktów na ekranie)
	int spinNumber=2; //Iloœæ ró¿nych spinów
	int[][] spins; //Tablica wartoœci spinów
	
	public SimulationPanel(int X, int Y, int advantage, int coeff, int spinNumber) { //Konstruktor		
		this.X=Math.round(X/coeff+10);
		this.Y=Math.round(Y/coeff+10);
		spins=new int[this.X][this.Y];
		this.coeff=coeff;
		this.spinNumber=spinNumber;
		
		for (int i=0; i<this.X; i++) {	//Przydzielanie losowo pocz¹tkowych spinów
			for (int j=0; j<this.Y; j++) {
				double spin=spinNumber*Math.random()+((double)advantage)/100; //advantage - wsp. przewagi
				if (spin<1) //Bardziej przejrzysty sposób
					spins[i][j]=0;
				else if (spin<2)
					spins[i][j]=1;
				else if (spin<3)
					spins[i][j]=2;
				else if (spin<4)
					spins[i][j]=3;
				else if (spin<5)
					spins[i][j]=4;
				//spins[i][j]=(int) Math.round(spinNumber*Math.random()-0.5+((double)advantage)/100); //<- Mo¿na i tak
			}
		}
	}
	
	void changeOpinion() { //Wyznaczanie mo¿liwej zmiany wartoœci spinów
		int tmp[][]=spins;	//Kopia zapasowa - spiny s¹ rozpatrywane w sekwencjach, nie w czasie rzeczywistym
		for (int i=1; i<X-1; i++) {	//Pêtla po prawie wszystkich punktach
			for (int j=1; j<Y-1; j++) { //^Rozpatrywanie tylko wewnêtrznych przypadków
				int spinValues[]= {0,0,0,0,0}; //Liczba s¹siaduj¹cych spinów danego rodzaju
				for (int ii=i-1; ii<=i+1; ii++) { //Pêtle po s¹siadach
					for (int jj=j-1; jj<=j+1; jj++) {
						if (ii==i&&jj==j)	//Nie wliczaj rozpatrywanego punktu
							continue;
						switch (spins[ii][jj]) {	//Zliczanie spinó
							case 0: spinValues[0]++; break;
							case 1: spinValues[1]++; break;
							case 2: spinValues[2]++; break;
							case 3: spinValues[3]++; break;
							case 4: spinValues[4]++; break;
							default: break;
						}
					}
				}
				int spin=spinValues[spins[i][j]]; //Iloœæ spinów takich samych
				for (int ii=0; ii<spinNumber; ii++) {	//Pêtla po rodzajach spinów
					if (spinValues[ii]>spin) {	//Je¿eli innych spinów jest wiêcej
						spin=spinValues[ii];	//Nowa wartoœæ do porównania
						spins[i][j]=ii;	//Zmiana spinu (koloru) punktu
					}
				}
			}
		}
		spins=tmp;	//Aktualizacja
	}
	
	public Dimension getPreferredSize(){	//Ustawienie rozmiaru okna graficznego
		return new Dimension(1500, 900);
	}
	
	protected void paintComponent(Graphics g){	//Rysowanie punktów
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		for (int i=0; i<X*coeff; i+=coeff){	//Pêtla po wszystkich punktach i malowanie ich w zale¿noœci od spinu.
			for (int j=0; j<Y*coeff; j+=coeff) {
				if (spins[i/coeff][j/coeff]==0)	//Ustawianie koloru w zale¿noœci od spinu
					g2.setColor(Color.BLUE);
				else if (spins[i/coeff][j/coeff]==1)
					g2.setColor(Color.ORANGE);
				else if (spins[i/coeff][j/coeff]==2)
					g2.setColor(Color.GREEN);
				else if (spins[i/coeff][j/coeff]==3)
					g2.setColor(Color.RED);
				else if (spins[i/coeff][j/coeff]==4)
					g2.setColor(Color.CYAN);
				g2.fill(new Ellipse2D.Double(i-5*coeff, j-5*coeff, coeff, coeff));	//Rysowanie punktu
			}
		}
	}
	
	int prevSum=0;	//Poprzednia suma wszystkich wartoœci spinów (do kontroli, czy coœ siê zmienia)
	
	public void run() {	//Funkcja "g³ówna" - dzia³a przez ca³¹ symulacjê
		int sum=0;	//Suma wszystkich wartoœci spinów
		int spinValues[]= {0,0,0,0,0};	//Liczba spinów danego rodzaju
		for (int i=1; i<X-1; i++) {	//Pêtla po prawie wszystkich (ale tylko wewnêtrznych) punktach
			for (int j=1; j<Y-1; j++) {
				sum+=spins[i][j];	//Dodawanie wartoœci spinu do sumy
				switch (spins[i][j]) {	//Zliczanie iloœci spinów
					case 0: spinValues[0]++; break;
					case 1: spinValues[1]++; break;
					case 2: spinValues[2]++; break;
					case 3: spinValues[3]++; break;
					case 4: spinValues[4]++; break;
					default: break;
				}
			}
		}
		if (prevSum!=sum) {	//Je¿eli suma obecna i poprzednia siê ró¿ni
			System.out.print("Opinie [");	//Wyœwietl statysyki tej iteracji
			System.out.print(n); n++;
			System.out.print("]: Niebieskie - ");
			System.out.print(spinValues[0]);
			System.out.print(" Pomaranczowe - ");
			System.out.print(spinValues[1]);
			System.out.print(" Zielone - ");
			System.out.print(spinValues[2]);
			System.out.print(" Czerwone - ");
			System.out.print(spinValues[3]);
			System.out.print(" Cyjanowe - ");
			System.out.println(spinValues[4]);
			prevSum=sum;
		}
		repaint();
		changeOpinion();
	}

}
