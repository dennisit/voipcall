package call.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import call.AbstractId;
import call.Contact;
import call.ContactList;
import call.ContactListUpdateListener;

public class ContactListGui extends AbstractId implements ContactListUpdateListener {

	@SuppressWarnings("unused")
	private final MainWindow main;
	private final JList<Contact> peerlist;
	private final ContactListModel peermodel;
	private final JPanel panel;

	public ContactListGui(MainWindow main) {
		this.main = main;

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		peermodel = new ContactListModel();
		peerlist = new JList<>(peermodel);
		peerlist.setCellRenderer(new ContactListCellRenderer());

		// peer list
		new ContactMouseAdapter(peerlist, peermodel);

		ContactList.addListener(this);
	}

	public void addToWindow(JFrame window) {
		window.getContentPane().add(BorderLayout.WEST, getComponent());
	}

	public JComponent getComponent() {
		JScrollPane listPane = new JScrollPane(peerlist);
		// panel.add(BorderLayout.NORTH, new JLabel("Contacts", JLabel.CENTER));
		panel.add(BorderLayout.CENTER, listPane);
		panel.setPreferredSize(new Dimension(180, 350));
		return panel;
	}

	@Override
	public void onAnyContactUpdate() {
		panel.repaint();
	}

	@Override
	public void onContactUpdate(Contact contact) {}

	public void setSelectedContact(Contact contact) {
		if (contact != null) {
			int index = peermodel.indexOfElement(contact);
			if (index >= 0 && peerlist.getSelectedIndex() != index) {
				peerlist.setSelectedIndex(index);
			}
		} else {
			peerlist.clearSelection();
		}
	}

	@Override
	public String getId() {
		return "ContactListGui";
	}

}
