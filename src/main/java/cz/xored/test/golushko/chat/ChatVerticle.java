package cz.xored.test.golushko.chat;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import java.util.Date;

/**
 * Created by MCRC on 04.05.2017.
 */
public class ChatVerticle extends AbstractVerticle{

    private final static String EB_ADDRESS_SERVICE_CHAT_BROADCAST = "service.chat.broadcast";
    private final static String EB_ADDRESS_SERVICE_CHAT_OUTPUT = "service.chat.output";

    @Override
    public void start() throws Exception {

        EventBus eb = vertx.eventBus();

        eb.consumer(EB_ADDRESS_SERVICE_CHAT_OUTPUT, message -> {
            JsonObject jsonMessage = (JsonObject)message.body();
            JsonObject object = new JsonObject();
            object.put("author", "Client");
            object.put("messageDate", new Date().toString());
            object.put("message", jsonMessage.getValue("message"));
            eb.publish(EB_ADDRESS_SERVICE_CHAT_BROADCAST, object);
        });
    }
}
