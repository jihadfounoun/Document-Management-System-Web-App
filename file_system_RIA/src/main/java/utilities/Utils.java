package utilities;

public class Utils {
	public static boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static boolean isEmailValid(String email) {
		return email.contains("@") && email.indexOf("@") > 0 && email.indexOf("@") < (email.length() - 1);
	}

	public static boolean isNumber(String s) {
		if (s == null)
			return false;
		try {
			int n = Integer.parseInt(s);
			if(n<0)
				return false;
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}