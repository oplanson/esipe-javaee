<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Lab 03 - JPA et Intégration Base de Données

## 📋 Objectifs

Ce laboratoire est la **continuation directe du Lab 02**. Vous allez transformer l'application bancaire pour utiliser une vraie base de données PostgreSQL au lieu du stockage en mémoire.

### Ce que vous allez apprendre

- ✅ Configurer JPA (Jakarta Persistence API) avec Hibernate
- ✅ Créer des entités JPA avec relations (@Entity, @OneToMany, @ManyToOne)
- ✅ Utiliser EntityManager pour les opérations CRUD
- ✅ Gérer les transactions avec @Transactional
- ✅ Configurer une DataSource PostgreSQL dans Liberty
- ✅ Utiliser Flyway pour les migrations de base de données
- ✅ Injecter des dépendances avec CDI (@Inject)
- ✅ Comprendre la différence entre stockage en mémoire et persistance

## 🎯 Contexte

Dans le Lab 02, vous avez créé une application web avec des servlets et JSP, mais les données étaient stockées en mémoire (dans une HashMap). À chaque redémarrage du serveur, toutes les données étaient perdues.

**Problème** : Comment persister les données de manière permanente ?

**Solution** : Utiliser JPA avec PostgreSQL et Flyway pour gérer le schéma de base de données.

## 📁 Structure du Projet

```
Lab03-JPA/
├── pom.xml                          # Dépendances Maven (JPA, PostgreSQL, Flyway)
├── docker-compose.yml               # PostgreSQL en conteneur
├── src/
│   ├── main/
│   │   ├── java/com/bank/
│   │   │   ├── model/
│   │   │   │   ├── Client.java     # Entité JPA avec @Entity
│   │   │   │   └── Account.java    # Entité JPA avec relation @ManyToOne
│   │   │   ├── service/
│   │   │   │   └── ClientService.java  # Service avec EntityManager
│   │   │   ├── web/
│   │   │   │   └── ClientController.java  # Servlet avec @Inject
│   │   │   └── health/
│   │   │       ├── DatabaseHealthCheck.java
│   │   │       └── WebAppReadinessCheck.java
│   │   ├── resources/
│   │   │   ├── META-INF/
│   │   │   │   ├── persistence.xml          # Configuration JPA
│   │   │   │   ├── beans.xml                # Configuration CDI
│   │   │   │   └── microprofile-config.properties
│   │   │   └── db/migration/
│   │   │       ├── V1__create_clients_table.sql
│   │   │       ├── V2__create_accounts_table.sql
│   │   │       └── V3__add_account_status.sql
│   │   ├── liberty/config/
│   │   │   ├── server.xml           # Configuration Liberty avec DataSource
│   │   │   └── bootstrap.properties # Variables d'environnement
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml
│   │       │   └── views/           # JSP (identiques au Lab 02)
│   │       ├── css/
│   │       └── index.html
└── README.md
```

## 🔄 Différences avec Lab 02

| Aspect | Lab 02 (Mémoire) | Lab 03 (Base de données) |
|--------|------------------|--------------------------|
| **Stockage** | HashMap en mémoire | PostgreSQL |
| **Persistance** | ❌ Perdu au redémarrage | ✅ Permanent |
| **Service** | `new ClientService()` | `@Inject ClientService` |
| **Entités** | POJO simples | Entités JPA avec @Entity |
| **Transactions** | Aucune | @Transactional |
| **Relations** | clientId (Long) | @ManyToOne / @OneToMany |
| **Migrations** | Aucune | Flyway (V1, V2, V3) |

## 🚀 Démarrage Rapide

### Prérequis

- Java 17+
- Maven 3.8+
- Docker et Docker Compose (pour PostgreSQL)
- Open Liberty (installé via Maven)

### Étape 1 : Démarrer PostgreSQL

```bash
# Démarrer la base de données
docker-compose up -d

# Vérifier que PostgreSQL est prêt
docker exec banking-db pg_isready -U bankuser -d bankdb
```

### Étape 2 : Construire et Déployer

```bash
# Construire l'application
mvn clean package

# Démarrer Liberty
mvn liberty:dev
```

### Étape 3 : Tester l'Application

Ouvrez votre navigateur : http://localhost:9080/

**Endpoints disponibles :**
- 🏠 Page d'accueil : http://localhost:9080/
- 👥 Liste des clients : http://localhost:9080/clients
- ➕ Ajouter un client : http://localhost:9080/client?action=new
- 💊 Health check : http://localhost:9080/health
- 📊 Métriques : http://localhost:9080/metrics

## 📚 Concepts Clés

### 1. Entités JPA

Les entités JPA sont des classes Java annotées qui représentent des tables de base de données.

**Client.java** (extrait) :
```java
@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Account> accounts;
}
```

**Points importants :**
- `@Entity` : Marque la classe comme entité JPA
- `@Id` : Clé primaire
- `@GeneratedValue` : Auto-incrémentation
- `@OneToMany` : Relation un-à-plusieurs avec Account

### 2. Relations JPA

**Relation bidirectionnelle Client ↔ Account :**

```java
// Dans Client.java
@OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
private List<Account> accounts;

// Dans Account.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "client_id", nullable = false)
private Client client;
```

### 3. EntityManager et Transactions

**ClientService.java** (extrait) :
```java
@ApplicationScoped
public class ClientService {
    
    @PersistenceContext(unitName = "bankingPU")
    private EntityManager em;
    
    @Transactional
    public Client create(Client client) {
        em.persist(client);
        em.flush();
        return client;
    }
    
    public List<Client> findAll() {
        return em.createNamedQuery("Client.findAll", Client.class)
                .getResultList();
    }
}
```

**Points importants :**
- `@ApplicationScoped` : Bean CDI avec cycle de vie application
- `@PersistenceContext` : Injection de l'EntityManager
- `@Transactional` : Gestion automatique des transactions
- `em.persist()` : Insérer une nouvelle entité
- `em.merge()` : Mettre à jour une entité existante
- `em.remove()` : Supprimer une entité

### 4. Migrations Flyway

Flyway gère l'évolution du schéma de base de données de manière versionnée.

**V1__create_clients_table.sql** :
```sql
CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**V2__create_accounts_table.sql** :
```sql
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(34) NOT NULL UNIQUE,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    type VARCHAR(20) NOT NULL,
    client_id BIGINT NOT NULL,
    CONSTRAINT fk_accounts_client FOREIGN KEY (client_id) 
        REFERENCES clients(id) ON DELETE CASCADE
);
```

**V3__add_account_status.sql** :
```sql
ALTER TABLE accounts ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
```

**Ordre d'exécution :**
1. V1 : Créer la table clients
2. V2 : Créer la table accounts avec clé étrangère
3. V3 : Ajouter la colonne status (démonstration d'évolution)

### 5. Configuration JPA

**persistence.xml** :
```xml
<persistence-unit name="bankingPU" transaction-type="JTA">
    <jta-data-source>jdbc/bankingDS</jta-data-source>
    
    <class>com.bank.model.Client</class>
    <class>com.bank.model.Account</class>
    
    <properties>
        <property name="hibernate.dialect" 
                  value="org.hibernate.dialect.PostgreSQLDialect"/>
        <property name="hibernate.hbm2ddl.auto" value="validate"/>
    </properties>
</persistence-unit>
```

**Points importants :**
- `transaction-type="JTA"` : Transactions gérées par le serveur
- `hibernate.hbm2ddl.auto=validate` : Valider le schéma (pas de création auto)
- Flyway crée le schéma, JPA le valide

### 6. DataSource Liberty

**server.xml** (extrait) :
```xml
<library id="postgresql-lib">
    <fileset dir="${shared.resource.dir}/postgresql" includes="*.jar"/>
</library>

<dataSource id="DefaultDataSource" jndiName="jdbc/bankingDS">
    <jdbcDriver libraryRef="postgresql-lib"/>
    <properties.postgresql 
        serverName="${env.DB_HOST}"
        portNumber="${env.DB_PORT}"
        databaseName="${env.DB_NAME}"
        user="${env.DB_USER}"
        password="${env.DB_PASSWORD}"/>
</dataSource>
```

## 🔍 Exercices Pratiques

### Exercice 1 : Comprendre les Migrations

1. Examinez les fichiers de migration dans `src/main/resources/db/migration/`
2. Connectez-vous à PostgreSQL :
   ```bash
   docker exec -it banking-db psql -U bankuser -d bankdb
   ```
3. Vérifiez les tables créées :
   ```sql
   \dt
   SELECT * FROM flyway_schema_history;
   ```

### Exercice 2 : Ajouter une Nouvelle Migration

Créez `V4__add_client_phone.sql` :
```sql
ALTER TABLE clients ADD COLUMN phone VARCHAR(20);
CREATE INDEX idx_clients_phone ON clients(phone);
```

Redémarrez l'application et vérifiez que la migration s'exécute.

### Exercice 3 : Créer une Requête Nommée

1. Ajoutez une Named Query dans `Client.java` :
   ```java
   @NamedQuery(
       name = "Client.findByEmailDomain",
       query = "SELECT c FROM Client c WHERE c.email LIKE :domain"
   )
   ```

2. Utilisez-la dans `ClientService.java` :
   ```java
   public List<Client> findByEmailDomain(String domain) {
       return em.createNamedQuery("Client.findByEmailDomain", Client.class)
               .setParameter("domain", "%@" + domain)
               .getResultList();
   }
   ```

### Exercice 4 : Gérer les Relations

1. Créez un nouveau client avec des comptes :
   ```java
   Client client = new Client("Test User", "test@example.com");
   Account account = new Account("FR76...", 1000.0, "CHECKING");
   client.addAccount(account);
   clientService.create(client);
   ```

2. Vérifiez que la relation est bien persistée dans la base de données.

## 🧪 Tests et Validation

### Tester les Endpoints

```bash
# Liste des clients
curl http://localhost:9080/clients

# Créer un client
curl -X POST http://localhost:9080/client \
  -d "action=create&name=John Doe&email=john@example.com"

# Health check
curl http://localhost:9080/health
```

### Vérifier la Base de Données

```bash
# Compter les clients
docker exec banking-db psql -U bankuser -d bankdb \
  -c "SELECT COUNT(*) FROM clients;"

# Voir tous les clients
docker exec banking-db psql -U bankuser -d bankdb \
  -c "SELECT id, name, email FROM clients;"

# Voir les comptes avec leurs clients
docker exec banking-db psql -U bankuser -d bankdb \
  -c "SELECT a.number, a.balance, c.name FROM accounts a JOIN clients c ON a.client_id = c.id;"
```

## 🐛 Dépannage

### Problème : Flyway ne s'exécute pas

**Solution** : Vérifiez que PostgreSQL est démarré et accessible :
```bash
docker-compose ps
docker logs banking-db
```

### Problème : Erreur "Table already exists"

**Solution** : Réinitialisez la base de données :
```bash
docker-compose down -v
docker-compose up -d
```

### Problème : EntityManager null

**Solution** : Vérifiez que `beans.xml` existe dans `src/main/resources/META-INF/`

### Problème : Transactions ne fonctionnent pas

**Solution** : Assurez-vous que :
1. `@Transactional` est présent sur les méthodes de modification
2. Le service est injecté avec `@Inject` (pas `new`)
3. La feature `cdi-4.0` est activée dans `server.xml`

## 📖 Ressources

- [Jakarta Persistence (JPA) Specification](https://jakarta.ee/specifications/persistence/)
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Open Liberty JPA Guide](https://openliberty.io/guides/jpa-intro.html)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

## 🎓 Points Clés à Retenir

1. **JPA** simplifie l'accès aux données avec un mapping objet-relationnel
2. **EntityManager** gère le cycle de vie des entités
3. **@Transactional** assure la cohérence des données
4. **Flyway** gère les migrations de schéma de manière versionnée
5. **CDI** (@Inject) permet l'injection de dépendances
6. **Relations JPA** (@OneToMany, @ManyToOne) remplacent les clés étrangères manuelles
7. **Lazy Loading** optimise les performances en chargeant les données à la demande

## 🚀 Prochaines Étapes

Dans le Lab 04, vous allez :
- Ajouter des API REST avec JAX-RS
- Implémenter la sécurité avec JWT
- Créer des tests d'intégration
- Déployer l'application en production

---

**Made with Bob** 🤖