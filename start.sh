# Script pour démarrer l'application

echo "==================================="
echo "Airline Management Application"
echo "==================================="
echo ""

# Vérifier que PostgreSQL est en cours d'exécution
echo "Vérification de PostgreSQL..."
psql -U postgres -c "SELECT version();" > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo "❌ PostgreSQL n'est pas en cours d'exécution!"
    echo "Veuillez démarrer PostgreSQL et réessayer."
    exit 1
fi

echo "✓ PostgreSQL est actif"
echo ""

# Créer la base de données si elle n'existe pas
echo "Création de la base de données..."
psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname = 'airline_db'" | grep -q 1

if [ $? -ne 0 ]; then
    psql -U postgres -c "CREATE DATABASE airline_db;"
    echo "✓ Base de données 'airline_db' créée"
else
    echo "✓ Base de données 'airline_db' existe déjà"
fi

echo ""
echo "Compilation de l'application..."
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Erreur lors de la compilation!"
    exit 1
fi

echo "✓ Compilation réussie"
echo ""
echo "Démarrage de l'application..."
echo "L'application sera accessible sur: http://localhost:8081"
echo ""

mvn spring-boot:run
