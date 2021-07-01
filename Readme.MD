# Event Hub Site
This is a event hub web application(i.e., EventHubSite in the architure diagram) that lets the organization users to log in and manage the events, sources, targets, integrations and consumer profiles. This service is developed using SpringBoot MVC, Bootstrap CSS and Spring Thymeleaf.

![alt text](Architecture.png)

## Running in local using Maven
mvn spring-boot:run -Dversion=1.0.0 -Dspring-boot.run.arguments=--spring.profiles.active=local

## Building docker
 mvn compile jib:dockerBuild

## Running docker in local
docker run -p 8080:8080 -e GOOGLE_CLOUD_PROJECT='event-hub-249001' -e "SPRING_PROFILES_ACTIVE=local" -t gcr.io/event-hub-249001/event-hub-site:0.0.1-SNAPSHOT

## Pushing to container registry
mvn clean compile jib:build

## Deploying to GKE
kubectl apply -f deployment

## Helm Deployments
### Plain Vanilla K8S Blue Green Deployment
#### Deployment to identical prod systems
helm upgrade --install --set image.tag=3e91cd82-059e-4f21-9a70-983fca43b7e3 --namespace prod event-hub-site-galaxy ./helm/blue-green-deploy/prod-galaxy 

helm upgrade --install --set image.tag=3e91cd82-059e-4f21-9a70-983fca43b7e3 --namespace prod event-hub-site-nebula ./helm/blue-green-deploy/prod-nebula 
#### Ingress to switch between two environments
kubectl apply -f galaxy-ingress.yaml --namespace prod

kubectl apply -f nebula-ingress.yaml --namespace prod

## Canary deployment
### Current version deployment
helm upgrade --install --namespace dev --set app.name=event-hub-site-dev --set app.version=e73b424e4c6bd0c26cde0a40eb1f93af1f041306 --set image.tag=e73b424e4c6bd0c26cde0a40eb1f93af1f041306 event-hub-site-dev ./helm/dev

helm upgrade --install --namespace dev --set app.name=event-hub-site-canary --set app.version=fcaa501c9d4dee7acde1fe8e51ac82d7dc02ca54 --set image.tag=fcaa501c9d4dee7acde1fe8e51ac82d7dc02ca54 event-hub-site-canary ./helm/canary-deploy
