package vertx.learn;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

import java.util.UUID;

public class Verticle extends AbstractVerticle {
    private String uuid = UUID.randomUUID().toString();
    private HttpServer server;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        this.server = vertx.createHttpServer().requestHandler(req -> {
            System.out.println(
                    " Verticle instance id: " + uuid +
                            " Context hash: " + context.hashCode() +
                            " Current thread: " + Thread.currentThread()
            );
            req.response().end();
        });
    }

    @Override
    public void start(Promise<Void> startFuture) throws Exception {
        server.listen(8080, res -> {
            if (res.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(res.cause());
            }
        });
    }
}
