package architecture.domain;

public class AuthenticationHelper {

    public static boolean checkConfirmPassWordMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public static boolean checkEmailFormat(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@gmail\\.com";
        return email.matches(emailPattern);
    }

    public static boolean checkPasswordFormat(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
        return password.matches(passwordPattern);
    }
}
