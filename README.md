# terraform-springai

 Initial setup
```bash
# Set environment variables
export MONGODB_PUBLIC_KEY=your_public_key
export MONGODB_PRIVATE_KEY=your_private_key
export MONGODB_PROJECT_ID=your_project_id

# Build the application
./gradlew clean build

# Synthesize Terraform configuration
cdktf synth

# Deploy MongoDB Cluster using CDKTF
cdktf deploy
```

This will take several minutes to deploy. In the meantime, download [MongoDB/devcenter-articles](https://huggingface.co/datasets/MongoDB/devcenter-articles) to a directory `docs` in the resources folder.

Once the Cluster has been deployed, copy the connection string, excluding the `mongodb+srv://` at the start. 

Now set the env variables for connecting:
```bash
export MONGODB_USER="<database_username>"
export MONGODB_PASSWORD="<databse_password>"
export MONGODB_URI="<connection_string>"
```

Finally run the application.
```bash
# Run the Spring Boot application
./gradlew runSpringBoot
```

Load sample docs into the database:
```bash
curl -X GET http://localhost:8080/api/docs/load
```

Provide a test query:
```bash
curl -X GET "http://localhost:8080/question?message=How%20to%20analyze%20time-series%20data%20with%20Python%20and%20MongoDB?%20Explain%20the%20steps"
```
