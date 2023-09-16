echo Create Network and run Pgadmin
docker network create gestion_rh_network

echo Building the Api Gateway And Eureka Server
cd discoveryserver && call discoveryserver.bat && cd ../api-gateway && call api-gateway.bat && cd ..
docker-compose -f docker-compose-principale.yml up -d --build

endlocal