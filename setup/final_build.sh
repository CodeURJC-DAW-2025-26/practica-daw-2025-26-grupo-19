@echo off
echo ====================================================
echo 1. Arrancando la base de datos MySQL...
echo ====================================================
docker run --rm -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=futbolmanager -p 3307:3306 -d mysql:9.2

echo.
echo ====================================================
echo 2. Construyendo la aplicacion (Frontend y Backend)...
echo ====================================================
REM Nos movemos a la raiz del proyecto para que Docker vea el frontend
cd ..
docker build -t mi-app-daw -f docker/Dockerfile .

echo.
echo ====================================================
echo Esperando 12 segundos a que MySQL termine de iniciarse...
echo ====================================================
REM Pausa el script para darle tiempo a la base de datos a estar lista
timeout /t 12 /nobreak

echo.
echo ====================================================
echo 3. Encendiendo la aplicacion en el puerto 8443...
echo ====================================================
docker run -p 8443:8443 -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/futbolmanager mi-app-daw