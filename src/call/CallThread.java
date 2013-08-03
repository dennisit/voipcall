package call;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class CallThread extends AbstractConnection implements Runnable {

	private final Socket socket;
	private final long time;
	private CallRecorder out;
	private CallPlayer in;

	public CallThread(Contact contact, Socket socket, List<String> headers) {
		super(contact);
		this.socket = socket;
		this.time = System.currentTimeMillis();
	}

	@Override
	public void run() {
		try {
			open();

			OutputStream outstream = socket.getOutputStream();
			out = new CallRecorder(contact, outstream);
			out.saveTo(new CallCapture(time, contact.getHost(), "output"));
			new Thread(out).start();

			InputStream instream = socket.getInputStream();
			in = new CallPlayer(contact, new BufferedInputStream(instream));
			in.saveTo(new CallCapture(time, contact.getHost(), "input"));
			new Thread(in).start();

		} catch (LineUnavailableException | UnsupportedAudioFileException e) {
			e.printStackTrace();
			close();
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
	}

	public Socket getSocket() {
		return socket;
	}

	@Override
	public String getId() {
		return "CallThread<" + contact.getId() + ">";
	}
}