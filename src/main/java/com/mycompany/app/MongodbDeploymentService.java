package com.mycompany.app;

import com.hashicorp.cdktf.App;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.concurrent.*;

@Service
public class MongoDbDeploymentService {

    private static final String MONGODB_API_URL = "https://cloud.mongodb.com/api/atlas/v1.0/groups/{PROJECT_ID}/clusters/{CLUSTER_NAME}";
    private static final String PUBLIC_KEY = System.getenv("MONGODB_PUBLIC_KEY");
    private static final String PRIVATE_KEY = System.getenv("MONGODB_PRIVATE_KEY");

    @Autowired
    private MainStack mainStack;

    @Autowired
    private App cdktfApp;

    @PostConstruct
    public void deployAndConnectToMongoDb() throws Exception {
        // Step 1: Deploy MongoDB cluster using CDKTF
        deployMongoDbCluster();

        // Step 2: Poll for cluster readiness
        boolean clusterReady = waitForClusterReady();
        if (!clusterReady) {
            System.err.println("Cluster deployment timed out.");
            return;
        }

        // Step 3: Retrieve MongoDB URI
        String mongoUri = mainStack.getMongoDbUri();
        System.out.println("MongoDB URI: " + mongoUri);

        // Step 4: Use the MongoDB URI to connect to MongoDB
        connectToMongoDb(mongoUri);
    }

    private void deployMongoDbCluster() {
        cdktfApp.synth();
        System.out.println("Deploying MongoDB cluster...");
    }

    private boolean waitForClusterReady() throws InterruptedException, ExecutionException, TimeoutException {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        Runnable pollTask = () -> {
            try {
                if (isClusterReady()) {
                    future.complete(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(pollTask, 0, 30, TimeUnit.SECONDS);

        try {
            return future.get(10, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            scheduledFuture.cancel(true);
            future.complete(false);
            return false;
        } finally {
            scheduler.shutdown();
        }
    }

    private boolean isClusterReady() throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(MONGODB_API_URL.replace("{PROJECT_ID}", System.getenv("MONGODB_PROJECT_ID"))
                    .replace("{CLUSTER_NAME}", "myCluster"));
            request.addHeader("Authorization", "Basic " + Base64.getEncoder()
                    .encodeToString((PUBLIC_KEY + ":" + PRIVATE_KEY).getBytes()));
            request.addHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());
                JSONObject clusterInfo = new JSONObject(json);
                String stateName = clusterInfo.getString("stateName");
                return "IDLE".equals(stateName);
            }
        }
    }

    private void connectToMongoDb(String mongoUri) {
        try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
            MongoDatabase database = mongoClient.getDatabase("test"); // Replace 'test' with your actual database name
            System.out.println("Connected to MongoDB successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
