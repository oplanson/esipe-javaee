<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# JSF Navigation Rules - Explication

## Qu'est-ce qu'une Navigation Rule ?

Les **navigation rules** dans JSF définissent comment l'application navigue d'une page à une autre. Elles mappent les **outcomes** (résultats retournés par les méthodes du backing bean) vers des **view IDs** (chemins des fichiers XHTML).

## Structure d'une Navigation Rule

```xml
<navigation-rule>
    <from-view-id>*</from-view-id>  <!-- Depuis quelle page (wildcard = toutes) -->
    <navigation-case>
        <from-outcome>client-list</from-outcome>  <!-- Outcome retourné par le bean -->
        <to-view-id>/WEB-INF/views/client-list.xhtml</to-view-id>  <!-- Page cible -->
        <redirect/>  <!-- Optionnel: fait un redirect HTTP -->
    </navigation-case>
</navigation-rule>
```

## Comment ça fonctionne ?

### 1. Dans le Backing Bean (ClientBean.java)

```java
public String save() {
    // ... logique de sauvegarde ...
    return "client-list?faces-redirect=true";  // ← Retourne un "outcome"
}
```

### 2. JSF cherche la Navigation Rule correspondante

JSF cherche dans `faces-config.xml` une `<navigation-case>` où:
- `<from-outcome>` = "client-list"

### 3. JSF navigue vers la page cible

JSF charge la page définie dans `<to-view-id>`:
- `/WEB-INF/views/client-list.xhtml`

## Pourquoi deux versions du même outcome ?

```xml
<navigation-case>
    <from-outcome>/views/client-list</from-outcome>  <!-- Version avec chemin -->
    <to-view-id>/WEB-INF/views/client-list.xhtml</to-view-id>
</navigation-case>
<navigation-case>
    <from-outcome>client-list</from-outcome>  <!-- Version courte -->
    <to-view-id>/WEB-INF/views/client-list.xhtml</to-view-id>
</navigation-case>
```

Les deux versions permettent d'utiliser soit:
- `return "client-list?faces-redirect=true";` (court)
- `return "/views/client-list?faces-redirect=true";` (avec chemin)
- `<h:link outcome="/views/client-list" />` (dans les vues)

## Élément `<redirect/>`

```xml
<redirect/>
```

Cet élément force un **HTTP redirect** (code 302) au lieu d'un forward interne:
- **Avec `<redirect/>`**: L'URL dans le navigateur change
- **Sans `<redirect/>`**: L'URL reste la même (forward interne)

## Exemple Complet

### Dans ClientBean.java:
```java
public String edit() {
    client = clientService.findById(selectedClientId);
    return "client-form?faces-redirect=true";  // ← Outcome
}
```

### Dans faces-config.xml:
```xml
<navigation-case>
    <from-outcome>client-form</from-outcome>  <!-- ← Correspond à l'outcome -->
    <to-view-id>/WEB-INF/views/client-form.xhtml</to-view-id>  <!-- ← Page cible -->
    <redirect/>  <!-- ← Force le redirect HTTP -->
</navigation-case>
```

### Résultat:
1. La méthode `edit()` retourne "client-form?faces-redirect=true"
2. JSF trouve la navigation rule correspondante
3. JSF redirige vers `/WEB-INF/views/client-form.xhtml`
4. L'URL du navigateur devient: `http://localhost:9080/lab02b-jsf/faces/WEB-INF/views/client-form.xhtml`

## Alternative Moderne: Implicit Navigation

En JSF 2.0+, on peut aussi utiliser la **navigation implicite** sans définir de rules:

```java
// Retourne directement le chemin du fichier
return "/WEB-INF/views/client-list.xhtml?faces-redirect=true";
```

Mais les navigation rules offrent:
- ✅ Centralisation de la configuration
- ✅ Abstraction (on change le chemin sans toucher au code)
- ✅ Meilleure maintenabilité

## Résumé

| Élément | Rôle |
|---------|------|
| `<from-view-id>` | Page source (wildcard `*` = toutes) |
| `<from-outcome>` | Valeur retournée par le backing bean |
| `<to-view-id>` | Chemin de la page cible |
| `<redirect/>` | Force un redirect HTTP (change l'URL) |

---
© 2026 Olivier Planson - Made with IBM Bob