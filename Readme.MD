#Building docker
 mvn compile jib:dockerBuild

#Running docker in local
docker run -p 8080:8080 -e GOOGLE_CLOUD_PROJECT='event-hub-249001' -t gcr.io/event-hub-249001/event-hub-site:0.0.1-SNAPSHOT

# Pushing to container registry
mvn compile jib:build
