package call.debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import call.CallClient;
import call.Config;

public class TestClient {
	public static void main(String[] args) {
		new TestClient(args.length > 0 ? args[0] : null);
	}

	public TestClient(String ip) {
		CallClient client;
		try {
			// client = Client.connect("127.0.0.1");
			if (ip == null) {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				System.out.print("Connect to: ");
				System.out.flush();
				ip = br.readLine();
				if (ip.length() < 3)
					ip = "127.0.0.1";
			}
			client = new CallClient(ip, Config.DEFAULT_PORT);
			Thread thr = client.startCall();
			thr.join();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
