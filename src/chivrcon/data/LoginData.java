package chivrcon.data;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LoginData {

	private InetAddress address;
	private int port;
	private String adminPassword;

	public LoginData(String ip, int port, String adminPassword) throws UnknownHostException {
		this.address = InetAddress.getByName(ip);
		this.port = port;
		this.adminPassword = adminPassword;
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public boolean isValid() {
		return address != null && address.getAddress().length > 0 && port != 0;
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
	        return false;
	    }

	    if (!LoginData.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }

	    final LoginData other = (LoginData) obj;

	    return address.equals(other.address) && adminPassword.equals(other.adminPassword) && port == other.getPort();
	}

	@Override
	public int hashCode() {
	    return 53 * 7 + this.address.hashCode() + adminPassword.hashCode() + port;
	}

	@Override
	public String toString() {
		return address + ":" + port;
	}

}
