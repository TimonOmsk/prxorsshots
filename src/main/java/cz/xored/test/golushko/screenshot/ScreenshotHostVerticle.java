package cz.xored.test.golushko.screenshot;

import cz.xored.test.golushko.core.ApplicationConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class ScreenshotHostVerticle extends AbstractVerticle {

    private final static String EB_ADDRESS_SERVICE_SCREENSHOT_UPDATED = "service.screenshot.updated";

    private final AtomicReference<BufferedImage> lastScreenshot = new AtomicReference<>();
    private long screenshotRefreshTimerId;
    private final Logger LOG = LoggerFactory.getLogger(ScreenshotHostVerticle.class);

    @Override
    public void start() throws Exception {
        final Robot robot = new Robot();
        final Rectangle screenSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        SharedData sharedData = vertx.sharedData();
        final EventBus eb = vertx.eventBus();
        Long screenshotRefreshTime = config().getLong(ApplicationConfig.SCREENSHOT_UPDATE_PERIOD_NAME) * 1000;


        this.screenshotRefreshTimerId = getVertx().setPeriodic(screenshotRefreshTime, id -> {
            BufferedImage newScreenshot = robot.createScreenCapture(screenSize);
            BufferedImage oldScreennshot;
            do {
                oldScreennshot = lastScreenshot.get();
            } while (!lastScreenshot.compareAndSet(oldScreennshot, newScreenshot));


            LocalMap<String, Buffer> imageLocalMap = sharedData.getLocalMap("exchange");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(newScreenshot, "jpg", baos);
            } catch (IOException e) {
                LOG.error("Cannot write screenshot to stream", e);
            }
            if(baos.size() != 0) {
                imageLocalMap.put("screenshot", Buffer.buffer(baos.toByteArray()));
                eb.send(EB_ADDRESS_SERVICE_SCREENSHOT_UPDATED, String.valueOf(System.currentTimeMillis()));
            }
        });
    }




    @Override
    public void stop() throws Exception {
        getVertx().cancelTimer(screenshotRefreshTimerId);
    }
}
