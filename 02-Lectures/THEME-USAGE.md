<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Guide d'utilisation du thème Marp ESIPE

## Configuration du thème

Le thème personnalisé `esipe` est défini dans `esipe-theme.css` et est automatiquement chargé par Marp.

### Utilisation dans un fichier .md

```yaml
---
marp: true
theme: esipe
paginate: true
backgroundColor: #fff
header: 'Votre en-tête'
footer: 'Votre pied de page'
---
```

## Mise en page en colonnes

Le thème inclut plusieurs classes CSS pour créer des mises en page en colonnes.

### Deux colonnes égales

```markdown
<div class="columns">
<div>

### Colonne 1
Contenu de la première colonne
- Point 1
- Point 2

</div>
<div>

### Colonne 2
Contenu de la deuxième colonne
- Point A
- Point B

</div>
</div>
```

### Trois colonnes égales

```markdown
<div class="columns-3">
<div>

Colonne 1

</div>
<div>

Colonne 2

</div>
<div>

Colonne 3

</div>
</div>
```

### Colonnes avec ratio 2:1

```markdown
<div class="columns-2-1">
<div>

Colonne large (2/3)

</div>
<div>

Colonne étroite (1/3)

</div>
</div>
```

### Colonnes avec ratio 1:2

```markdown
<div class="columns-1-2">
<div>

Colonne étroite (1/3)

</div>
<div>

Colonne large (2/3)

</div>
</div>
```

## Classes CSS disponibles

- `.columns` - Deux colonnes égales
- `.columns-3` - Trois colonnes égales
- `.columns-2-1` - Deux colonnes avec ratio 2:1
- `.columns-1-2` - Deux colonnes avec ratio 1:2

## Notes importantes

1. **Espaces vides** : Laissez une ligne vide après `<div>` et avant `</div>` pour que Markdown soit correctement interprété
2. **HTML activé** : Le thème nécessite que `markdown.marp.enableHtml` soit activé dans les paramètres VSCode
3. **Réutilisabilité** : Tous les fichiers .md dans ce dossier peuvent utiliser `theme: esipe`

## Conversion en PDF/PPTX

### Avec le script automatique (Recommandé)

Le script `convert-slides.sh` détecte et utilise automatiquement le thème `esipe-theme.css` :

```bash
cd esipe-javaee
./convert-slides.sh
```

Le script :
- ✅ Détecte automatiquement `esipe-theme.css`
- ✅ Convertit tous les fichiers .md en PPTX
- ✅ Ne reconvertit que les fichiers modifiés
- ✅ Applique le thème personnalisé

### Conversion manuelle

Pour convertir manuellement avec le thème :

```bash
# PPTX
marp --theme esipe-theme.css --pptx 01-intro-jakartaee-microprofile.md --allow-local-files

# PDF
marp --theme esipe-theme.css --pdf 01-intro-jakartaee-microprofile.md --allow-local-files

# HTML
marp --theme esipe-theme.css --html 01-intro-jakartaee-microprofile.md --allow-local-files
```

**Note:** L'option `--allow-local-files` est nécessaire pour charger les images locales.

## Modification du thème

Pour modifier le thème pour tous les fichiers .md, éditez simplement `esipe-theme.css`. Les changements seront automatiquement appliqués à tous les fichiers utilisant `theme: esipe`.