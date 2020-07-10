package polito.it.noleggio.model;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.PriorityQueue;

import polito.it.noleggio.model.Event.EventType;

public class Simulator {

	// Coda degli eventi
	private PriorityQueue<Event> queue = new PriorityQueue<>();

	// Parametri di simulazione (definiti con un valore di default)
	private int NC = 10; // Numero auto
	private Duration T_IN = Duration.of(10, ChronoUnit.MINUTES); // Intervallo tra i clienti

	private final LocalTime oraApertura = LocalTime.of(8, 00); // (Perametri utili per la
	private final LocalTime oraChiusura = LocalTime.of(17, 00);// definizione della coda)

	// Modello del mondo
	private int nAutoDisponibili; // Auto disponibili nel deposito (tra 0 e NC)

	// Calcolo dei valori
	private int numClienti;
	private int numClientiInsoddisfatti;

	// Metodi per impostare i parametri
	public void setNumCars(int N) {
		this.NC = N;
	}

	public void setClientFrequency(Duration d) {
		this.T_IN = d;
	}

	// Metodi per restituire i risultati
	public int getNumClienti() {
		return numClienti;
	}

	public int getNumClientiInsoddisfatti() {
		return numClientiInsoddisfatti;
	}

	// SIMULAZIONE
	public void run() {
		// preparazione iniziale (mondo + codaEventi)
		this.nAutoDisponibili = this.NC;
		this.numClienti = this.numClientiInsoddisfatti = 0;

		this.queue.clear();
		LocalTime oraArrivoCliente = this.oraApertura;
		do {
			Event e = new Event(oraArrivoCliente, EventType.NEW_CLIENT);
			this.queue.add(e);
			oraArrivoCliente = oraArrivoCliente.plus(this.T_IN);
		} while (oraArrivoCliente.isBefore(this.oraChiusura));

		// esecuzione del ciclo di simulazione
		while (!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			System.out.println(e);
			processEvent(e);
		}
	}

	private void processEvent(Event e) {
		switch (e.getType()) {
		case NEW_CLIENT:
			if(this.nAutoDisponibili > 0) {
				//Cliente servito
				this.nAutoDisponibili--;
				this.numClienti++;
					//Bisogna generare un nuovo evento --> restituzione auto
					double num = Math.random(); //num [0,1)
					Duration travel;
					if(num < 1.0/3.0)
						travel = Duration.of(1, ChronoUnit.HOURS);
					else if(num < 2.0/3.0)
						travel = Duration.of(2, ChronoUnit.HOURS);
					else
						travel = Duration.of(3, ChronoUnit.HOURS);
					Event nuovo = new Event(e.getTime().plus(travel),EventType.CAR_RETURNED);
					this.queue.add(nuovo);
			}else {
				//Cliente insoddisfatto
				this.numClienti++;
				this.numClientiInsoddisfatti++;
			}
			break;
		case CAR_RETURNED:
			this.nAutoDisponibili++;
			break;
		}
	}
}
