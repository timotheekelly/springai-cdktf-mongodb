package com.mycompany.app;

import software.constructs.Construct;
import com.hashicorp.cdktf.TerraformStack;
import com.hashicorp.cdktf.providers.mongodbatlas.provider.MongodbatlasProvider;
import com.hashicorp.cdktf.providers.mongodbatlas.cluster.Cluster;
import com.hashicorp.cdktf.TerraformOutput;

public class MainStack extends TerraformStack {

    private final Cluster mongoCluster;

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
                .providerRegionName("EU_WEST_1")
                .diskSizeGb(10)
                .build();

        // Output MongoDB URI after deployment
        TerraformOutput.Builder.create(this, "MongoDbUri")
                .value(mongoCluster.getConnectionStrings())
                .build();
    }
}
