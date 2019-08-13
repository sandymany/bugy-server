package com.let.bugy.server.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SessionQueue {

	private final int closeAfter;
	private final int sessionTimeout;
	/**
	 * Queue na koji stavljaš korisnike
	 */
	private int MAX_BROJ_KORISNIKA = 10; // to bu inače blockalo dok pokušaš dodat više tak dugo dok se ne oslobodi mesto i onda se odma doda...
	private ArrayBlockingQueue<User> sessionQueue = new ArrayBlockingQueue<>(MAX_BROJ_KORISNIKA);

	/**
	 * Ona tvoja mapa za dohvaćanje korisnika prek sessionID, razmisli zakaj baš ova
	 */
	private Map<String,User> sessions = new ConcurrentHashMap<>();

	public SessionQueue(int closeAfter, int sessionTimeout){
		this.closeAfter = closeAfter; //Sama implementiraj ak očeš
		this.sessionTimeout = sessionTimeout;
	}


	/**
	 * Starta thread koji počne poppat sessione.
	 * Dodavat korisnike na queue moreš i prije nego počneš s threadom.
	 */
	public void start() throws InterruptedException {

		while(!stop()) { // Ako nećeš da se beskonačno vrti dodaj boolean polje koje postaviš na false u stop metodi i onda bu prestal thread.
			User u = sessionQueue.peek(); // You reached. The. End. Of. The. LINE. (User na kraju queuea)
			if(u == null) {
				u = sessionQueue.take();
			}
			u.actionSeen.set(true); // Setamo da smo ga vidli. ak se kaj dogodi u međuvremenu, pogledamo nakon sleepa.
			long elapsedTime = System.currentTimeMillis() - u.timestamp; // Vrijeme prošlo od zadnje akcije
			long remainingToSleep = sessionTimeout - elapsedTime; // Za odspavati
			if(remainingToSleep < 0){ //Ak je manje od 0 onda je već popan i preskačemo ga
				removeHead();
				continue;
			}
			Thread.sleep(remainingToSleep);
			if(u.actionSeen.get()) {
				removeHead();
			}
		}
	}

	public boolean stop() {
		// Sama moreš ak ti treba
		return false;
	}

	public void addUser(User u){
		sessionQueue.add(u);
		sessions.put(u.sessionID,u);
	}

	public void refreshUser(User u){
		sessionQueue.remove(u);
		sessionQueue.add(u);
	}

	// *spongegar*
	/**
	 * Pitanje za 1 000 000 rupija, zakaj je OVO i ONO DOLE synchronized?
	 * Ako to skužiš, the strongest code is inside you.
	 */
	public void removeHead() throws InterruptedException {
		User u = sessionQueue.take();
		synchronized (u) {
			u.mySessionQueue = null; // ovo više nije njegov session queue
		}
		System.out.println("Makivam usera " + u.username);
		sessions.remove(u.sessionID); //makiva ga s queuea i sa dictionaryja
	}

	/**
	 * Onaj tvoj dictionary za dohvaćanje prek session ID-ja.
	 * OBAVEZNO U BUGYJU MORAŠ PROVJERIT DAL JE TO KAJ SI DOBILA VRAĆENO
	 * @param sessionID ...
	 * @return ...
	 */
	public User getUserBySessionID(String sessionID) {
		return sessions.get(sessionID);
	}


	/**
	 * Testiranje
	 */
	public static void main(String[] args) throws InterruptedException {
		SessionQueue sq = new SessionQueue(99999,5000);
		User u1 = new User("a",UUID.randomUUID().toString());
		User u2 = new User("b",UUID.randomUUID().toString());
		User u3 = new User("c",UUID.randomUUID().toString());
		User u4 = new User("d",UUID.randomUUID().toString());
		User u5 = new User("e",UUID.randomUUID().toString());
		u1.mySessionQueue = sq;
		u2.mySessionQueue = sq;
		u3.mySessionQueue = sq;
		u4.mySessionQueue = sq;
		u5.mySessionQueue = sq;
		sq.addUser(u1);
		sq.addUser(u2);
		sq.addUser(u3);
		sq.addUser(u4);
		sq.addUser(u5);
		System.out.println(sq.sessionQueue);
		new Thread(() ->{ // Startamo queue.
			try {
				sq.start();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();

		//OVO JE SAMO TEST
		//JUST LIKE THE SIMULATIONS!
		new Thread(() -> { //Ispisujem queue svakih pol sekunde.
			List<User> users = new ArrayList<>();
			users.add(u1);
			users.add(u2);
			users.add(u3);
			users.add(u4);
			users.add(u5);
			while(true) {
				System.out.println(sq.sessionQueue);
				Random ran = new Random();
				for(User u: users) {
					int x = ran.nextInt(5); //samo 20% šanse da se dogodi akcija, radi simulacije...
					if(x == 0) {
						u.actionOccured(); //Ak je neki user sreckovic, onda on napravi akciju i timestamp mu se resetira
					}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	/**
	 * Izbriši i dodaj svojeg pravog usera nakomn kaj skužiš kak radi.
	 */
	static class User {

		/**
		 * Inače dodaj gettere i settere umjesto public varijabli.
		 */
		public long timestamp;
		public String username;
		public String sessionID;
		/**
		 * SessionQueue na kojem je ovaj korisnik.
		 * Volatile zato da si threadovi ne keširaju objekta.
		 */
		private volatile SessionQueue mySessionQueue = null;
		/**
		 * Ovo je true/false da thread dok odsleepa zna dal je korisnik napravil neku akciju.
		 * Seta se na true u actionOccurred, a na false ga seta Queue dok ga HEAD 'ulovi'.
		 *
		 * Pitanje za milijun rupija, zakaj nije običan boolean?
		 */
		public AtomicBoolean actionSeen = new AtomicBoolean(false);

		public User(String username, String sessionID) {
			this.username = username;
			this.sessionID = sessionID;
			actionOccured();
		}
		/**
		 * Metoda koja se poziva dok si dobila request od korisnika.
		 * Ona bude se pobrinula da korisnika
		 */
		public void actionOccured(){
			timestamp = System.currentTimeMillis();
			actionSeen.set(false);
			synchronized (this) {
				if (mySessionQueue != null) { // Provjera dal je uopće na nekom queueu, inače bu hitalo NullPointerException
					//OVA LINIJA KOMENTARA JE SECRET HINT!
					mySessionQueue.refreshUser(this);
				}
			}
		}

		@Override
		public String toString() {
			return username + " : " + ((System.currentTimeMillis()-timestamp)/1000.) + "s since last action.";
		}
	}

}
