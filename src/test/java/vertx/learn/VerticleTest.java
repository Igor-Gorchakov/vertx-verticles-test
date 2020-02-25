package vertx.learn;

import io.vertx.core.*;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(VertxUnitRunner.class)
public class VerticleTest {

    @Test
    public void standardVTest(TestContext context) {
        Async async = context.async();
        // given
        Vertx vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions()
                .setInstances(2);
        vertx.deployVerticle(Verticle.class.getName(), options);
        HttpClient client = vertx.createHttpClient();
        // when
        List<Future> futures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Promise promise = Promise.promise();
            client.getNow(8080, "localhost", "/", httpClientResponse -> {
                promise.complete();
            });
            futures.add(promise.future());
        }
        // then
        CompositeFuture.all(futures).setHandler(ar -> {
            vertx.undeploy(Verticle.class.getName());
            vertx.close();
            async.complete();
        });
    }

    @Test
    public void workerVTest(TestContext context) {
        Async async = context.async();
        // given
        Vertx vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions()
                .setWorker(true)
                .setInstances(1)
                .setWorkerPoolName("worker-pool")
                .setWorkerPoolSize(1);
        vertx.deployVerticle(Verticle.class.getName(), options);
        HttpClient client = vertx.createHttpClient();
        // when
        List<Future> futures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Promise promise = Promise.promise();
            client.getNow(8080, "localhost", "/", httpClientResponse -> {
                promise.complete();
            });
            futures.add(promise.future());
        }
        // then
        CompositeFuture.all(futures).setHandler(ar -> {
            vertx.undeploy(Verticle.class.getName());
            vertx.close();
            async.complete();
        });
    }

    @Test
    public void multiThreadedWorkerVTest(TestContext context) {
        Async async = context.async();
        // given
        Vertx vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions()
                .setWorker(true)
                .setInstances(1)
                .setWorkerPoolName("worker-pool")
                .setWorkerPoolSize(2);
        vertx.deployVerticle(Verticle.class.getName(), options);
        HttpClient client = vertx.createHttpClient();
        // when
        List<Future> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Promise promise = Promise.promise();
            client.getNow(8080, "localhost", "/", httpClientResponse -> {
                promise.complete();
            });
            futures.add(promise.future());
        }
        // then
        CompositeFuture.all(futures).setHandler(ar -> {
            vertx.undeploy(Verticle.class.getName());
            vertx.close();
            async.complete();
        });
    }
}
