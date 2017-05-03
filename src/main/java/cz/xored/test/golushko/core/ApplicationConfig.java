package cz.xored.test.golushko.core;

import io.vertx.core.json.JsonObject;

/**
 * Created by MCRC on 26.04.2017.
 */
public class ApplicationConfig {

    public final static String HOST_NAME = "host";
    public final static String HOST_PORT_NAME = "port";
    public final static String HOST_USERS_NAME = "users";
    public final static String HOST_USERS_REFRESH_PERIOD_NAME = "refresh_period";

    public final static String SCREENSHOT_NAME = "screenshot";
    public final static String SCREENSHOT_UPDATE_PERIOD_NAME = "update_period";


    public static JsonObject createDefaultConfiguration() {
        JsonObject root = new JsonObject();

        JsonObject hostOptions = new JsonObject();
        hostOptions.put(HOST_PORT_NAME, 8080);

        JsonObject usersHostOptions = new JsonObject();
        usersHostOptions.put(HOST_USERS_REFRESH_PERIOD_NAME, 1);

        hostOptions.put(HOST_USERS_NAME, usersHostOptions);

        root.put(HOST_NAME, hostOptions);

        JsonObject screenshotOptions = new JsonObject();
        screenshotOptions.put(SCREENSHOT_UPDATE_PERIOD_NAME, 5);

        root.put(SCREENSHOT_NAME, screenshotOptions);

        return root;
    }
}
