package cz.xored.test.golushko;

import cz.xored.test.golushko.core.ApplicationConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class HostVerticle extends AbstractVerticle {

    private final AtomicInteger clientsCount = new AtomicInteger(0);
    private final Logger logger = LoggerFactory.getLogger(HostVerticle.class);

    @Override
    public void start() throws Exception {

        SharedData sharedData = vertx.sharedData();

        EventBus eb = vertx.eventBus();

        JsonObject usersOptions = config().getJsonObject(ApplicationConfig.HOST_USERS_NAME);

        Long usersRefreshPeriod = usersOptions.getLong(ApplicationConfig.HOST_USERS_REFRESH_PERIOD_NAME) * 1000;

        vertx.setPeriodic(usersRefreshPeriod, id -> {
            JsonObject object = new JsonObject();
            object.put("author", "Server");
            object.put("messageDate", new Date().toString());
            object.put("message", clientsCount.get());
            eb.publish("service.chat.online", object);
        });

        eb.consumer("service.chat.output", message -> {
            System.out.println(message.body());
            JsonObject jsonMessage = (JsonObject)message.body();
            JsonObject object = new JsonObject();
            object.put("author", "Client");
            object.put("messageDate", new Date().toString());
            object.put("message", jsonMessage.getValue("message"));
            eb.publish("service.chat.broadcast", object);
        });

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);

        PermittedOptions chatPermittedOptions = new PermittedOptions().setAddressRegex("service.chat.*");

        PermittedOptions screenshotPermittedOptions = new PermittedOptions().setAddress("service.screenshot.updated");

        BridgeOptions options = new BridgeOptions();
        options.addOutboundPermitted(chatPermittedOptions);
        options.addOutboundPermitted(screenshotPermittedOptions);
        options.addInboundPermitted(chatPermittedOptions);
        sockJSHandler.bridge(options, bridgeEvent -> {
            if(bridgeEvent.type() == BridgeEventType.SOCKET_CREATED) {
                clientsCount.incrementAndGet();
                logger.info("New client connected.");
            } else if(bridgeEvent.type() == BridgeEventType.SOCKET_IDLE || bridgeEvent.type() == BridgeEventType.SOCKET_CLOSED) {
                int newCount = clientsCount.decrementAndGet();
                logger.info("Client disconnected.");
                if(newCount < 0) {
                    clientsCount.compareAndSet(newCount, 0);
                }
            }
            bridgeEvent.complete(true);
        });

        router.route("/eventbus/*").handler(sockJSHandler);

        router.route("/exchange/screenshot").handler(routingContext -> {

            HttpServerResponse response = routingContext.response();
            response.putHeader("Content-type", "image/jpeg");

            LocalMap<String, Buffer> screenshotExchange = sharedData.getLocalMap("exchange");
            Buffer screenshot = screenshotExchange.get("screenshot");
            response.end(screenshot);
        });

        Integer port = config().getInteger("port");

        server.requestHandler(router::accept).listen(port);

        logger.info("Listening " + port);
    }


}
