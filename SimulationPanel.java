package polonius.modelIsinga;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;

public class SimulationPanel extends JPanel implements Runnable{
	private static final long serialVersionUID = 1L;
	
	int X=0; //Wymiary zestawu spin�w
	int Y=0;
	int n=0; //Iteracje
	
	int coeff=10;	//Wsp�czynnik skali (im mniej, tym wi�cej punkt�w na ekranie)
	int spinNumber=2; //Ilo�� r�nych spin�w
	int[][] spins; //Tablica warto�ci spin�w
	
	public SimulationPanel(int X, int Y, int advantage, int coeff, int spinNumber) { //Konstruktor		
		this.X=Math.round(X/coeff+10);
		this.Y=Math.round(Y/coeff+10);
		spins=new int[this.X][this.Y];
		this.coeff=coeff;
		this.spinNumber=spinNumber;
		
		for (int i=0; i<this.X; i++) {	//Przydzielanie losowo pocz�tkowych spin�w
			for (int j=0; j<this.Y; j++) {
				double spin=spinNumber*Math.random()+((double)advantage)/100; //advantage - wsp. przewagi
				if (spin<1) //Bardziej przejrzysty spos�b
					spins[i][j]=0;
				else if (spin<2)
					spins[i][j]=1;
				else if (spin<3)
					spins[i][j]=2;
				else if (spin<4)
					spins[i][j]=3;
				else if (spin<5)
					spins[i][j]=4;
				//spins[i][j]=(int) Math.round(spinNumber*Math.random()-0.5+((double)advantage)/100); //<- Mo�na i tak
			}
		}
	}
	
	void changeOpinion() { //Wyznaczanie mo�liwej zmiany warto�ci spin�w
		int tmp[][]=spins;	//Kopia zapasowa - spiny s� rozpatrywane w sekwencjach, nie w czasie rzeczywistym
		for (int i=1; i<X-1; i++) {	//P�tla po prawie wszystkich punktach
			for (int j=1; j<Y-1; j++) { //^Rozpatrywanie tylko wewn�trznych przypadk�w
				int spinValues[]= {0,0,0,0,0}; //Liczba s�siaduj�cych spin�w danego rodzaju
				for (int ii=i-1; ii<=i+1; ii++) { //P�tle po s�siadach
					for (int jj=j-1; jj<=j+1; jj++) {
						if (ii==i&&jj==j)	//Nie wliczaj rozpatrywanego punktu
							continue;
						switch (spins[ii][jj]) {	//Zliczanie spin�
							case 0: spinValues[0]++; break;
							case 1: spinValues[1]++; break;
							case 2: spinValues[2]++; break;
							case 3: spinValues[3]++; break;
							case 4: spinValues[4]++; break;
							default: break;
						}
					}
				}
				int spin=spinValues[spins[i][j]]; //Ilo�� spin�w takich samych
				for (int ii=0; ii<spinNumber; ii++) {	//P�tla po rodzajach spin�w
					if (spinValues[ii]>spin) {	//Je�eli innych spin�w jest wi�cej
						spin=spinValues[ii];	//Nowa warto�� do por�wnania
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
	
	protected void paintComponent(Graphics g){	//Rysowanie punkt�w
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		for (int i=0; i<X*coeff; i+=coeff){	//P�tla po wszystkich punktach i malowanie ich w zale�no�ci od spinu.
			for (int j=0; j<Y*coeff; j+=coeff) {
				if (spins[i/coeff][j/coeff]==0)	//Ustawianie koloru w zale�no�ci od spinu
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
	
	int prevSum=0;	//Poprzednia suma wszystkich warto�ci spin�w (do kontroli, czy co� si� zmienia)
	
	public void run() {	//Funkcja "g��wna" - dzia�a przez ca�� symulacj�
		int sum=0;	//Suma wszystkich warto�ci spin�w
		int spinValues[]= {0,0,0,0,0};	//Liczba spin�w danego rodzaju
		for (int i=1; i<X-1; i++) {	//P�tla po prawie wszystkich (ale tylko wewn�trznych) punktach
			for (int j=1; j<Y-1; j++) {
				sum+=spins[i][j];	//Dodawanie warto�ci spinu do sumy
				switch (spins[i][j]) {	//Zliczanie ilo�ci spin�w
					case 0: spinValues[0]++; break;
					case 1: spinValues[1]++; break;
					case 2: spinValues[2]++; break;
					case 3: spinValues[3]++; break;
					case 4: spinValues[4]++; break;
					default: break;
				}
			}
		}
		if (prevSum!=sum) {	//Je�eli suma obecna i poprzednia si� r�ni
			System.out.print("Opinie [");	//Wy�wietl statysyki tej iteracji
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
