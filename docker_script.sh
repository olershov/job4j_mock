#!bin/bash
# This script for rising an application in Linux
echo START CLEAN 
docker stop site && docker rm site
docker stop notification && docker rm notification
docker stop desc && docker rm desc
docker stop auth && docker rm auth
docker stop mock && docker rm mock
docker stop db_auth && docker rm db_auth
docker stop db_mock && docker rm db_mock
docker stop db_notification && docker rm db_notification
docker stop db_desc && docker rm db_desc

docker rmi job4j_mock-site           
docker rmi job4j_mock-notification   
docker rmi job4j_mock-auth           
docker rmi job4j_mock-mock           
docker rmi job4j_mock-desc  
echo CONTAINERS AND IMAGES ARE REMOVED


echo START BUILD
docker compose build

echo START UP
docker compose up -d

echo -e HAS BEEN CREATED

echo -e INITIALIZATION... PLEASE WAIT...
until curl -s http://localhost:8080 > /dev/null 2>&1; do
    sleep 15
done

echo -e COMPLETED
echo -e GO TO http://localhost:8080/
