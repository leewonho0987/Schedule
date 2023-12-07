package main_UI;

public class UserSession {
    private static String loggedInUserId;
    private static String LoggedInUserName;
    
    public static String getLoggedInUserId() {
        return loggedInUserId;
    }

    public static void setLoggedInUserId(String userId) {
        loggedInUserId = userId;
    }
    
    public static String getLoggedInUserName() {
        return LoggedInUserName;
    }
    
    public static void setLoggedInUserName(String userName) {
        LoggedInUserName = userName;
    }
   
    
}