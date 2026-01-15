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
- ✅ **Utiliser JNDI pour accéder aux ressources et à la configuration**
- ✅ **Configurer des environment entries dans web.xml**
- ✅ **Créer un service de configuration utilisant JNDI**
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
- 💰 Validation de transaction (JNDI Demo) : http://localhost:9080/validate-transaction
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

### Exercice 5 : Configuration JNDI (NOUVEAU)

**Objectif** : Apprendre à utiliser JNDI pour accéder aux ressources et à la configuration.

#### Partie A : Configurer les Entrées d'Environnement

1. Ouvrez `src/main/webapp/WEB-INF/web.xml`

2. Ajoutez une référence de ressource pour le DataSource :
   ```xml
   <resource-ref>
       <description>Banking Database DataSource</description>
       <res-ref-name>jdbc/bankingDS</res-ref-name>
       <res-type>javax.sql.DataSource</res-type>
       <res-auth>Container</res-auth>
       <res-sharing-scope>Shareable</res-sharing-scope>
   </resource-ref>
   ```

3. Ajoutez trois entrées d'environnement :
   ```xml
   <env-entry>
       <description>Maximum number of login attempts</description>
       <env-entry-name>app/maxLoginAttempts</env-entry-name>
       <env-entry-type>java.lang.Integer</env-entry-type>
       <env-entry-value>3</env-entry-value>
   </env-entry>

   <env-entry>
       <description>Support email address</description>
       <env-entry-name>app/supportEmail</env-entry-name>
       <env-entry-type>java.lang.String</env-entry-type>
       <env-entry-value>support@bank.com</env-entry-value>
   </env-entry>

   <env-entry>
       <description>Maximum transaction amount</description>
       <env-entry-name>app/maxTransactionAmount</env-entry-name>
       <env-entry-type>java.lang.Double</env-entry-type>
       <env-entry-value>10000.00</env-entry-value>
   </env-entry>
   ```

#### Partie B : Implémenter le Service de Configuration JNDI

1. Ouvrez `src/main/java/com/bank/config/JndiConfigService.java`

2. Complétez la méthode `init()` :
   ```java
   @PostConstruct
   public void init() {
       logger.info("Initializing JNDI Configuration Service...");
       try {
           lookupDataSource();
           lookupEnvironmentEntries();
           logger.info("JNDI Configuration Service initialized successfully");
       } catch (Exception e) {
           logger.severe("Failed to initialize: " + e.getMessage());
           throw new RuntimeException("JNDI initialization failed", e);
       }
   }
   ```

3. Implémentez `lookupDataSource()` :
   ```java
   private void lookupDataSource() throws NamingException {
       InitialContext ctx = null;
       try {
           ctx = new InitialContext();
           String jndiName = "java:comp/env/jdbc/bankingDS";
           dataSource = (DataSource) ctx.lookup(jndiName);
           logger.info("DataSource looked up: " + jndiName);
           
           // Test connection
           try (var conn = dataSource.getConnection()) {
               logger.info("DataSource connection test successful");
           }
       } finally {
           if (ctx != null) {
               ctx.close();
           }
       }
   }
   ```

4. Implémentez `lookupEnvironmentEntries()` :
   ```java
   private void lookupEnvironmentEntries() throws NamingException {
       InitialContext ctx = null;
       try {
           ctx = new InitialContext();
           
           try {
               maxLoginAttempts = (Integer) ctx.lookup("java:comp/env/app/maxLoginAttempts");
               logger.info("Max login attempts: " + maxLoginAttempts);
           } catch (NamingException e) {
               maxLoginAttempts = 3; // default
           }
           
           try {
               supportEmail = (String) ctx.lookup("java:comp/env/app/supportEmail");
               logger.info("Support email: " + supportEmail);
           } catch (NamingException e) {
               supportEmail = "support@bank.com"; // default
           }
           
           try {
               maxTransactionAmount = (Double) ctx.lookup("java:comp/env/app/maxTransactionAmount");
               logger.info("Max transaction amount: " + maxTransactionAmount);
           } catch (NamingException e) {
               maxTransactionAmount = 10000.0; // default
           }
       } finally {
           if (ctx != null) {
               ctx.close();
           }
       }
   }
   ```

5. Implémentez les getters et méthodes utilitaires :
   ```java
   public DataSource getDataSource() {
       if (dataSource == null) {
           throw new IllegalStateException("DataSource not initialized");
       }
       return dataSource;
   }
   
   public boolean isValidTransactionAmount(Double amount) {
       if (amount == null || amount <= 0) {
           return false;
       }
       return amount <= maxTransactionAmount;
   }
   ```

#### Partie C : Tester le Service JNDI

1. Démarrez l'application :
   ```bash
   mvn liberty:dev
   ```

2. Vérifiez les logs de démarrage :
   ```
   [INFO] Initializing JNDI Configuration Service...
   [INFO] DataSource looked up: java:comp/env/jdbc/bankingDS
   [INFO] DataSource connection test successful
   [INFO] Max login attempts: 3
   [INFO] Support email: support@bank.com
   [INFO] Max transaction amount: 10000.0
   [INFO] JNDI Configuration Service initialized successfully
   ```

3. Testez via un endpoint (optionnel - créez un servlet de test) :
   ```java
   @WebServlet("/config")
   public class ConfigTestServlet extends HttpServlet {
       @Inject
       private JndiConfigService configService;
       
       protected void doGet(HttpServletRequest request, HttpServletResponse response) 
               throws IOException {
           response.setContentType("text/plain");
           response.getWriter().println(configService.getConfigurationSummary());
       }
   }
   ```

#### Points Clés à Comprendre

1. **JNDI Naming Contexts** :
   - `java:comp/env` : Contexte portable pour les ressources
   - Fonctionne sur tous les serveurs Jakarta EE

2. **Resource References** :
   - Déclarées dans `web.xml`
   - Mappées aux ressources réelles dans `server.xml`

3. **Environment Entries** :
   - Configuration externalisée
   - Modifiable sans recompilation
   - Types supportés : String, Integer, Double, Boolean, etc.

4. **Caching** :
   - Lookups JNDI coûteux
   - Cache les résultats dans des champs
   - Initialisation au démarrage avec `@PostConstruct`

5. **Error Handling** :
   - Toujours fermer `InitialContext` dans finally
   - Fournir des valeurs par défaut
   - Logger les erreurs pour le débogage

#### Partie D : Utiliser JNDI dans un Servlet (Transaction Validator)

**Objectif** : Créer un servlet qui utilise la configuration JNDI pour valider les montants de transaction.

1. Le servlet `TransactionValidatorServlet` est déjà implémenté dans la solution
2. Il utilise `JndiConfigService` pour accéder au paramètre `app/maxTransactionAmount`
3. Testez-le en accédant à : http://localhost:9080/validate-transaction

**Fonctionnalités du servlet :**
- Affiche la configuration JNDI actuelle (max amount, support email, max login attempts)
- Formulaire pour valider un montant de transaction
- Validation côté serveur utilisant le paramètre JNDI
- Support des appels API (JSON) et interface web (HTML)

**Exemples de test :**

Via le navigateur :
1. Accédez à http://localhost:9080/validate-transaction
2. Entrez un montant (ex: 5000.00) et cliquez sur "Validate Transaction"
3. Le système vérifie si le montant est ≤ maxTransactionAmount (10000.00 par défaut)

Via curl (API) :
```bash
# Transaction valide
curl -X POST http://localhost:9080/validate-transaction \
  -d "amount=5000.00&description=Payment to supplier"

# Transaction invalide (dépasse la limite)
curl -X POST http://localhost:9080/validate-transaction \
  -d "amount=15000.00&description=Large payment"

# Réponse JSON
curl -X POST http://localhost:9080/validate-transaction \
  -H "Accept: application/json" \
  -d "amount=8000.00"
```

**Réponses attendues :**

Transaction valide (≤ 10000.00) :
```
✅ Transaction Valid
Amount: €5,000.00
Maximum Allowed: €10,000.00
Status: APPROVED
```

Transaction invalide (> 10000.00) :
```
❌ Transaction Invalid
Amount: €15,000.00
Maximum Allowed: €10,000.00
Status: REJECTED
💡 Tip: For transactions above €10,000.00, please contact support@bank.com
```

**Modifier la limite de transaction :**

Pour changer la limite maximale, modifiez `web.xml` :
```xml
<env-entry>
    <env-entry-name>app/maxTransactionAmount</env-entry-name>
    <env-entry-type>java.lang.Double</env-entry-type>
    <env-entry-value>25000.00</env-entry-value>  <!-- Nouvelle limite -->
</env-entry>
```

Puis redémarrez l'application pour que les changements prennent effet.


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
8. **JNDI** (NOUVEAU) permet l'accès programmatique aux ressources et à la configuration
9. **Environment Entries** (NOUVEAU) externalisent la configuration dans web.xml

## 🚀 Prochaines Étapes

Dans le Lab 04, vous allez :
- Ajouter des API REST avec JAX-RS
- Implémenter la sécurité avec JWT
- Créer des tests d'intégration
- Déployer l'application en production

---

**Made with Bob** 🤖