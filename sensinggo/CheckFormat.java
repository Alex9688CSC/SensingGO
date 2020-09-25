package edu.nctu.wirelab.sensinggo;

import java.util.regex.Pattern;

/**
 * Created by py on 6/27/18.
 */

public class CheckFormat {
	
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

	public static final Pattern BIRTH_PATTERN = Pattern.compile(
		"^\\d{4}[\\-/\\.](0?[1-9]|1[012])[\\-/\\.](0?[1-9]|[12][0-9]|3[01])$"
	);

    public static boolean checkEmail (String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

	public static boolean checkBirth (String date) {
		return BIRTH_PATTERN.matcher(date).matches();
	}
}
