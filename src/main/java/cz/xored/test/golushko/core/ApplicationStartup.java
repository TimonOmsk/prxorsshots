package cz.xored.test.golushko.core;

import cz.xored.test.golushko.HostVerticle;
import cz.xored.test.golushko.screenshot.SchreenshotHostVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class ApplicationStartup {

    public void start() {
        Vertx vertx = Vertx.vertx();

        ConfigStoreOptions defaultConfig = new ConfigStoreOptions()
                .setType("json")
                .setConfig(ApplicationConfig.createDefaultConfiguration());

        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setConfig(new JsonObject().put("path", "config.json"));

        ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(defaultConfig).addStore(fileStore);

        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);

        retriever.getConfig(jsonObjectAsyncResult -> {
            if(jsonObjectAsyncResult.failed()) {
                System.out.println("Failed to load configuration");
                System.exit(-1);
            } else {
                deployVerticles(vertx, jsonObjectAsyncResult.result());
            }
        });

    }

    private void deployVerticles(Vertx vertx, JsonObject config) {

        JsonObject screenshotConfig = config.getJsonObject("screenshots");
        if(screenshotConfig != null) {
            DeploymentOptions screenshotDeploymentOptions = new DeploymentOptions().setConfig(screenshotConfig);
            vertx.deployVerticle(new SchreenshotHostVerticle(), screenshotDeploymentOptions);
        } else {
            System.out.println("Cannot load configuration for screenshot capturing feature.");
        }

        JsonObject hostConfig = config.getJsonObject("host");
        if(hostConfig != null) {
            DeploymentOptions hostDeploymentOptions = new DeploymentOptions().setConfig(hostConfig);
            vertx.deployVerticle(new HostVerticle(), hostDeploymentOptions);
        } else {
            System.out.println("Cannot load configuration for host feature.");
        }

    }




}
