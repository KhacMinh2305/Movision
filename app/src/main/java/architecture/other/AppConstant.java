package architecture.other;

public class AppConstant {
    // Auth
    public static final String WEB_CLIENT_ID = "699008899828-nmeo8086k0rm7svnq77edqjogvbfdkti.apps.googleusercontent.com";
    // App key
    public static final String FILE_NAME = "key_file";
    public static final int API_KEY = 0;
    public static final int READ_ACCESS_TOKEN = 1;
    public static final int ACCESS_TOKEN = 2;
    public static final int SESSION_ID = 3;
    public static final int USERNAME = 4;
    public static final int PASSWORD = 5;
    public static final String API_KEY_NAME = "api_key";
    public static final String READ_ACCESS_TOKEN_NAME = "read_access_token";
    public static final String ACCESS_TOKEN_NAME = "access_token";
    public static final String SESSION_ID_NAME = "session_id";
    public static final String USERNAME_NAME = "username";
    public static final String PASSWORD_NAME = "password";

    // TmDB API
    public static final String TMDB_BASE_URL = "https://api.themoviedb.org/";
    public static final String TMDB_REQUEST_ACCESS_TOKEN_URL = "https://api.themoviedb.org/3/authentication/token/new";
    public static final String TMDB_AUTHENTICATE_URL = "https://www.themoviedb.org/authenticate/";
    public static final String TMDB_CREATE_SESSION_URL = "https://api.themoviedb.org/3/authentication/session/new";
    public static final String TMDB_IMAGE_HOST = "https://image.tmdb.org/t/p/original/";
    public static final String YOUTUBE_EMBED_URL = "https://www.youtube.com/embed/";

    // TmDB response
    public static final int TMDB_EXPIRED_TOKEN_CODE = 33;
    public static final int TMDB_WRONG_ACCOUNT_CODE = 30;

    // Room
    public static final String LOCAL_DATABASE_NAME = "local_database";

    // Category in Home Fragments
    public static final String CATEGORY_UPCOMING_TITLE = "Coming soon";
    public static final String CATEGORY_TRENDING_TITLE = "Trending now";
    public static final String CATEGORY_TOP_RATED_TITLE = "Top rated movies";
    public static final String CATEGORY_POPULAR_TITLE = "People also watch";
    public static final String CATEGORY_PLAYING_TITLE = "Playing now";
    public static final String CATEGORY_UNDEFINED_TITLE = "Undefined";
    public static final String POPULAR_PEOPLE_TITLE = "Popular people";
    public static final String TRENDING_PEOPLE_TITLE  = "Trending people";

    public static final String CATEGORY_UPCOMING_TAG = "movie_upcoming";
    public static final String CATEGORY_TRENDING_TAG = "movie_trending";
    public static final String CATEGORY_TOP_RATED_TAG = "movie_top_rated";
    public static final String CATEGORY_POPULAR_TAG = "movie_popular";
    public static final String CATEGORY_PLAYING_TAG = "movie_playing";
    public static final String CATEGORY_UNDEFINED_TAG = "undefined";
    public static final String POPULAR_PEOPLE_TAG = "people_popular";
    public static final String TRENDING_PEOPLE_TAG = "people_trending";

    public static final int HUMAN_MALE = 2;
    public static final int HUMAN_FEMALE = 1;
    public static final int HUMAN_ALL = -1;

    public static final String GMAIL_AUTH_PROPS = "mail.smtp.auth";
    public static final String GMAIL_HOST_PROPS = "mail.smtp.host";
    public static final String GMAIL_PORT_PROPS = "mail.smtp.port";
    public static final String GMAIL_SSL_PROPS = "mail.smtp.ssl.enable";
    public static final String GMAIL_AUTH_VALUE = "true";
    public static final String GMAIL_HOST_VALUE = "smtp.gmail.com";
    public static final String GMAIL_PORT_VALUE = "465";
    public static final String GMAIL_SSL_VALUE = "true";
    public static final String GMAIL_APP_SENDER = "doankhacminh2301@gmail.com";
    public static final String GMAIL_PASSWORD = "uldffaenyjtdjrji"; // add to DataStore

    public static final int SUCCESS_CODE = 1;
    public static final int FAILURE_CODE = 0;

    public static final String DEFAULT_USERNAME = "Undefined";
    public static final String DEFAULT_GMAIL = "Undefined";
    public static final String DEFAULT_PHONE_NUMBER = "Undefined";

    public static final String EMAIL_PROVIDER = "password";
}
