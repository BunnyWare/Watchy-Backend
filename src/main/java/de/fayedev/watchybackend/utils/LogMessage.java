package de.fayedev.watchybackend.utils;

public class LogMessage {

    public static final String UNHANDLED_EXCEPTION = "An unhandled exception occurred.";
    public static final String METHOD_NOT_ALLOWED = "Method not allowed.";
    public static final String BAD_REQUEST = "Request not readable. Please verify the data.";
    public static final String FORBIDDEN = "Request authentication invalid.";
    public static final String JWT_AUTHENTICATION_ERROR = "JWT authentication encountered an error.";
    public static final String JWT_INVALID_OR_EXPIRED = "JWT Token '{}' invalid or expired.";
    public static final String USER_NOT_FOUND = "Could not find user '{}'.";
    public static final String USER_LOGGED_IN = "User '{}' logged in and got token '{}'.";
    public static final String USER_LOGGED_IN_FAILED = "User '{}' tried to login with a wrong password/username.";
    public static final String USER_LOGGED_IN_FAILED_EMAIL = "User '{}' tried to login without confirming their email first.";
    public static final String USER_AUTH_INVALID = "Authentication check failed.";
    public static final String USER_NOT_FOUND_TOKEN = "Could not find user that belongs to the email token '{}'.";
    public static final String USER_NOT_FOUND_EMAIL = "Could not find user with email address '{}'.";
    public static final String USER_REQUEST_CONFIRM = "User '{}' requested an email confirmation to '{}'. Token generated: '{}'.";
    public static final String USER_CONFIRM = "User '{}' confirmed their email address '{}' with token '{}'.";
    public static final String USER_ALREADY_CONFIRMED = "User has already confirmed their email address.";
    public static final String USER_MAILING_DISABLED = "Mailing is not enabled. The use of this endpoint is therefor forbidden.";
    public static final String USER_ALREADY_EXISTS = "User with email '{}' or user name '{}' already exists.";
    public static final String USER_CREATED = "User '{}' with User Name '{}' and Email '{}' created.";
    public static final String USER_CHANGE_PASSWORD = "User '{}' ('{}') changed their password.";
    public static final String MAIL_ERROR = "Mail to '{}' with subject '{}' could not be sent. Underlying error is: ";
    public static final String WEBSOCKET_CONNECTED_QUEUE = "User '{}' connected to websocket queue '{}'.";
    public static final String WEBSOCKET_DISCONNECTED_QUEUE = "User '{}' disconnected from websocket queue '{}'.";
    public static final String WEBSOCKET_MESSAGE_RECEIVED = "Received websocket message '{}' request with id '{}' from '{}' with data: '{}' on queue '{}'.";
    public static final String TMDB_FAILED = "Error in TMDB request.";

    private LogMessage() {
    }
}
