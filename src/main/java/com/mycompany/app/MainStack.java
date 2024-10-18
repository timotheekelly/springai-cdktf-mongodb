package com.mycompany.app;

import software.constructs.Construct;
import com.hashicorp.cdktf.TerraformStack;
import com.hashicorp.cdktf.providers.mongodbatlas.provider.MongodbatlasProvider;
import com.hashicorp.cdktf.providers.mongodbatlas.cluster.Cluster;
import com.hashicorp.cdktf.TerraformOutput;

public class MainStack extends TerraformStack {

    private final Cluster mongoCluster;
    private String mongoDbUri;

    public MainStack(final Construct scope, final String id) {
        super(scope, id);

        // MongoDB Atlas Provider Configuration
        MongodbatlasProvider.Builder.create(this, "MongoDBAtlasProvider")
                .publicKey(System.getenv("MONGODB_PUBLIC_KEY"))
                .privateKey(System.getenv("MONGODB_PRIVATE_KEY"))
                .build();

        // MongoDB Cluster Configuration
        mongoCluster = Cluster.Builder.create(this, "MongoCluster")
                .name("myCluster")
                .projectId(System.getenv("MONGODB_PROJECT_ID"))
                .providerName("AWS")
                .providerInstanceSizeName("M10")
                .providerRegionName("US_EAST_1")
                .diskSizeGb(10)
                .build();

        // Output MongoDB URI after deployment
        TerraformOutput.Builder.create(this, "MongoDbUri")
                .value(mongoCluster.getConnectionStrings().get(0).getStandardSrv())
                .build();

        // Store the URI in a field
        this.mongoDbUri = mongoCluster.getConnectionStrings().get(0).getStandardSrv();
    }

    // Method to return the MongoDB URI
    public String getMongoDbUri() {
        return mongoDbUri;
    }

    public Cluster getMongoCluster() {
        return mongoCluster;
    }
}