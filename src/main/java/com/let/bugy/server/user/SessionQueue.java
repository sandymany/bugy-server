import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Izbriši i dodaj svojeg pravog usera nakomn kaj skužiš kak radi.
 */
class User {

	/**
	 * Inače dodaj gettere i settere umjesto public varijabli.
	 */
	public long timestamp;
	public String username;
	public String sessionID;
	/**
	 * SessionQueue na kojem je ovaj korisnik.
	 */
	public SessionQueue mySessionQueue;
	/**
	 * Ovo je true/false da thread dok odsleepa zna dal je korisnik napravil neku akciju.
	 * Seta se na true u actionOccurred, a na false ga seta Queue dok ga HEAD 'ulovi'.
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
		if(mySessionQueue != null) { // Provjera dal je uopće na nekom queueu, inače bu hitalo NullPointerException
			mySessionQueue.refreshUser(this);
		}
	}

	@Override
	public String toString() {
		return username + " : " + ((System.currentTimeMillis()-timestamp)/1000.) + " sekundi prošlo.";
	}
}

public class SessionQueue {

	private final int closeAfter;
	private final int sessionTimeout;
	/**
	 * Queue na koji stavljaš korisnike
	 */
	private int MAX_BROJ_KORISNIKA = 10;
	private ArrayBlockingQueue<User> sessionQueue = new ArrayBlockingQueue<User>(MAX_BROJ_KORISNIKA);

	/**
	 * Ona tvoja mapa za dohvaćanje korisnika prek sessionID ne mora bit synchronized
	 */
	private Map<String,User> sessions = new HashMap<>();

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
			User u = sessionQueue.take(); // You reached. The. End. Of. The. LINE. (User na kraju queuea)
			u.actionSeen.set(true); // Setamo da smo ga vidli. ak se kaj dogodi u međuvremenu, pogledamo nakon sleepa.
			long elapsedTime = System.currentTimeMillis() - u.timestamp; // Vrijeme prošlo od zadnje akcije
			long remainingToSleep = sessionTimeout - elapsedTime; // Za odspavati
			if(remainingToSleep < 0){ //Ak je manje od 0 onda je već popan i preskačemo ga
				System.out.println("Maknul usera " + u.username);
				continue;
			}
			Thread.sleep(remainingToSleep);
			if(!u.actionSeen.get()){
				sessionQueue.add(u);
			}
			else {
				// Sa queuea smo ga već maknuli, još samo iz dictionaryja
				System.out.println("Maknul usera " + u.username);
				sessions.remove(u.sessionID);
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

	public User getUserBySessionID(String sessionID) {
		return sessions.get(sessionID);
	}


	/**
	 * Testiranje
	 */
	public static void main(String[] args) throws InterruptedException {
		SessionQueue sq = new SessionQueue(99999,5000);
		User u1 = new User("a",UUID.randomUUID().toString()); Thread.sleep(500); //Razmisli zakaj testiram sa sleepanjem svakih pol sekunde...
		User u2 = new User("b",UUID.randomUUID().toString()); Thread.sleep(500);
		User u3 = new User("c",UUID.randomUUID().toString()); Thread.sleep(500);
		User u4 = new User("d",UUID.randomUUID().toString()); Thread.sleep(500);
		User u5 = new User("e",UUID.randomUUID().toString()); Thread.sleep(500);
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

		new Thread(() -> { //Ispisujem queue svakih pol sekunde.
			while(true) {
				System.out.println(sq.sessionQueue);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

}
