# terraform-springai
 
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

# Run the Spring Boot application
./gradlew bootRun
```
