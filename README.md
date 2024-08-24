# Génération de CSP à partir d'un fichier xcore

Ce petit projet montre comment configurer gradle pour générer les sources java correspondantes à un fichier xcore.
Et comment lier ce model a un CSP.

Il y a aussi des contraintes OCL modelisees' en composant les sub-CSP comme navCSP (NavigationOrAttributeCall)

Based on`git clone https://git.kher.nl/cours/base-xtext.git`

## Interesting Files
- model/zoo.xcore
- src/main/java/mde/App.java

## Compilation & exécution

```bash
# build
./gradlew build

# run
./gradlew run
```