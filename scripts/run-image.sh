# remove existing
docker rm -f client-api

# run live (-d for detatched)
docker run --name client-api -p 8080:8080 easylancer/client-api:latest