Comment build le dropin :

1. compiler avec mvn clean install. La compilation échoue, mais ce qu'on veut, c'est simplement que maven copie les dépendences dans target.
2. Vérifier que toutes les dépendences sont présentes dans le fichier build.properties.
3. File > export > Plugin development > Deployable plugin and fragments > JeanClaudeEcplipsePlugin > directory > finish


Tester le dropin :

Le test via run as ne fonctionne pas à cause des dépendences maven manquantes.
Pour le tester, copier export/plugin/JeanClaudeEclipsePlugin.jar dans C:\Users\cedri\eclipse\jee-2023-09\eclipse\dropins