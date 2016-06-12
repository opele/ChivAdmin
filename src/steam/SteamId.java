package steam;

/**
 * According to the Chiv RCon protocol the steamId is encodes as a long. But it simply takes up 8 bytes and actually consists of 2 integers: [Steam3Id.B, Steam3Id.B, Steam3Id.A, Steam3Id.A] Uscript:
 * PRI.UniqueId.Uid.A/.B example: PRI.UniqueId.Uid = U:1:11111111
 *
 * Part A is the actual id whereas B is a constant.
 *
 */
public class SteamId {

	private int steamIdA;
	private int steamIdB;

	public SteamId() {
		this(0);
	}

	public SteamId(int partA) {
		this(partA, 17825793);
	}

	public SteamId(int steamIdA, int steamIdB) {
		this.steamIdA = steamIdA;
		this.steamIdB = steamIdB;
	}

	public int getSteamIdA() {
		return steamIdA;
	}

	public void setSteamIdA(int steamIdA) {
		this.steamIdA = steamIdA;
	}

	public int getSteamIdB() {
		return steamIdB;
	}

	public void setSteamIdB(int steamIdB) {
		this.steamIdB = steamIdB;
	}

	/**
	 * Converts the steamId part A to its corresponding community id (64-bit integer). It assumes default parameters, so returned IDs may be incorrect for extraordinary accounts.
	 *
	 * @return
	 */
	public long toCommunityId() {
		long universe = 1l;
		long accType = 1l;
		long instance = 1l;

		return ((universe << 56l) | (accType << 52l) | (instance << 32l) | steamIdA);
	}

	/**
	 * This method is not accurate. The actual format is:
	 *
	 * The textual representation follows the pattern '[C:U:A]' or '[C:U:A:I]' depending on the type of Steam ID. C is a single character that represents the Account Type, or a combination of the
	 * Account Type and Instance ID (mostly 'U' for 'Individual') U is the Universe (what Steam system this Steam ID comes from. In almost all cases, this will be Public, thus set to 1) A is the
	 * Account ID I is the Instance ID. If not present, the default instance ID for that 'C' value is used.
	 *
	 * @return
	 */
	public String toSteamId3() {
		return "[U:1:" + steamIdA + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!SteamId.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		final SteamId other = (SteamId) obj;

		return steamIdA == other.steamIdA && steamIdB == other.steamIdB;
	}

	@Override
	public int hashCode() {
		return 53 * 3 + steamIdA + steamIdB;
	}

	@Override
	public String toString() {
		return "U:" + steamIdA + ":" + steamIdB;
	}

	/**
	 * Tries to convert a String to a SteamId.
	 *
	 * @param id
	 *            a String representation of the SteamId3 (e.g. [U:1:12345678])
	 * @return a possibly valid SteamId or null
	 */
	public static SteamId parse(String id) {
		if (id == null || id.trim().length() <= 0 || !id.contains(":"))
			return null;

		String sId = id.substring(id.lastIndexOf(':') + 1).trim().replace("]", "");

		try {
			int iId = Integer.parseInt(sId);
			if (iId > 0) {
				return new SteamId(iId);
			}
		} catch (Exception e) {
		}

		return null;
	}
}
