package call;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ContactList {

	private static Set<Contact> contacts = new HashSet<>();
	private static List<ContactListUpdateListener> listeners = new ArrayList<>();
	private static Map<Contact, Boolean> online = new HashMap<>();
	private static Lock lock = new ReentrantLock();

	public ContactList() {}

	public static void clear() {
		lock.lock();
		contacts.clear();
		online.clear();
		notifyListeners();
		lock.unlock();
	}

	public static List<Contact> getSortedContacts() {
		lock.lock();
		List<Contact> list = new ArrayList<>(contacts);
		Collections.sort(list, new ContactListComparator());
		lock.unlock();
		return list;
	}

	public static List<Contact> getUnsortedContacts() {
		lock.lock();
		List<Contact> list = new ArrayList<>(contacts);
		lock.unlock();
		return list;
	}

	public static void addContact(Contact contact) {
		lock.lock();
		if (!contacts.contains(contact)) {
			contacts.add(contact);
			notifyListeners(contact);
		}
		lock.unlock();
	}

	public static void removeContact(Contact contact) {
		lock.lock();
		if (contacts.contains(contact)) {
			contacts.remove(contact);
			notifyListeners(contact);
		}
		lock.unlock();
	}

	public static Contact findContact(String host, int port, String user) {
		for (Contact contact : contacts) {
			if ((host == null || contact.isHost(host)) && (port == 0 || contact.isPort(port))
					&& (user == null || contact.isUser(user))) {
				return contact;
			}
		}
		return null;
	}

	public static boolean containsContact(String host, int port, String user) {
		for (Contact contact : contacts) {
			if (contact.isHost(host) && contact.isPort(port) && contact.isUser(user)) {
				return true;
			}
		}
		return false;
	}

	public static boolean containsContact(Contact contact) {
		return contacts.contains(contact);
	}

	public static boolean isOnline(Contact contact) {
		return online.containsKey(contact) && online.get(contact);
	}

	public static void setOnline(Contact contact, boolean onlinestatus) {
		lock.lock();
		online.put(contact, onlinestatus);
		notifyListeners(contact);
		lock.unlock();
	}

	public static void update() {
		lock.lock();
		notifyListeners();
		lock.unlock();
	}

	private static void notifyListeners() {
		for (ContactListUpdateListener listener : listeners) {
			listener.onAnyContactUpdate();
		}
	}

	private static void notifyListeners(Contact contact) {
		for (ContactListUpdateListener listener : listeners) {
			listener.onContactUpdate(contact);
			listener.onAnyContactUpdate();
		}
	}

	@SuppressWarnings("unused")
	private static void debugContactList() {
		List<Contact> list = new ArrayList<Contact>(ContactList.getSortedContacts());
		Util.log("contactlist:", "--------");
		for (Contact c : list) {
			if (list.size() > 2) {
				Util.log("contactlist:", c.getId() + " comp(1) = " + c.compareTo(list.get(0))
						+ ", comp(2) = " + c.compareTo(list.get(1)) + ", equal(1) = " + c.equals(list.get(0))
						+ ", equal(2) = " + c.equals(list.get(1)));
			} else {
				Util.log("contactlist:", c.getId());
			}
		}
		Util.log("contactlist:", "________");
	}

	public static void addListener(ContactListUpdateListener listener) {
		listeners.add(listener);
	}

	public static Contact me() {
		return new Contact("127.0.0.1", Config.CURRENT_PORT, Util.getUserName());
	}
}
