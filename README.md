# Génération de code à partir d'un fichier xcore

Ce petit projet montre comment configurer gradle pour générer les sources java correspondantes à un fichier xcore.

## Récupération de ce dépôt

`git clone https://git.kher.nl/cours/base-xtext.git`

## Dépendances

- JDK >= 11, installation [ici](https://adoptium.net/)

## Compilation & exécution

```bash
# build
./gradlew build

# run
./gradlew run
```

## Réutilisation pour un autre projet

1- copier la structure des dossiers `/model`, `/src/main/java/<package>`, `build.gradle`
2- créer un fichier xcore pour le métamodèle et reprendre les deux annotation `@Ecore` et `@GenModel` de `book.xcore`
3- mettre votre code dans `/src/main/java/<package>`
4- ajouter les dépendances spécifiques à votre projet dans la section `dependencies` du `build.gradle`