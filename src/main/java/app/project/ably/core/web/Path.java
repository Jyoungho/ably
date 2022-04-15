package app.project.ably.core.web;

public class Path {

    /** API Prefix */
    public static final String API                                     = "/api";

    public static final String VERSION                                 = "/v1";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Auth
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String AUTH_LOGIN                                   = API + VERSION + "/auth/login";

    public static final String AUTH_TOKEN                                   = API + VERSION + "/auth/token";

    public static final String AUTH_CHECK_PHONE_NUMBER                      = API + VERSION + "/auth/check/phone-number";

    public static final String AUTH_CHECK_PHONE_NUMBER_MESSAGE              = API + VERSION + "/auth/check/phone-number/message";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | User
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String USER_SIGNUP                                  = API + VERSION + "/user/signup";

    public static final String USER_INFO                                    = API + VERSION + "/user/info";

    public static final String USER_PASSWORD                                = API + VERSION + "/user/password";
}

