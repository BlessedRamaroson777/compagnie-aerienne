@echo off
REM Script pour démarrer l'application sur Windows

echo ===================================
echo Airline Management Application
echo ===================================
echo.

REM Vérifier que PostgreSQL est en cours d'exécution
echo Verification de PostgreSQL...
psql -U postgres -c "SELECT version();" >nul 2>&1

if %errorlevel% neq 0 (
    echo X PostgreSQL n'est pas en cours d'execution!
    echo Veuillez demarrer PostgreSQL et reessayer.
    pause
    exit /b 1
)

echo [OK] PostgreSQL est actif
echo.

REM Créer la base de données si elle n'existe pas
echo Creation de la base de donnees...
psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname = 'airline_db'" | findstr "1" >nul

if %errorlevel% neq 0 (
    psql -U postgres -c "CREATE DATABASE airline_db;"
    echo [OK] Base de donnees 'airline_db' creee
) else (
    echo [OK] Base de donnees 'airline_db' existe deja
)

echo.
echo Compilation de l'application...
call mvn clean install -DskipTests

if %errorlevel% neq 0 (
    echo X Erreur lors de la compilation!
    pause
    exit /b 1
)

echo [OK] Compilation reussie
echo.
echo Demarrage de l'application...
echo L'application sera accessible sur: http://localhost:8080
echo.

call mvn spring-boot:run
