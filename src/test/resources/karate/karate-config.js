function fn() {
    var username = karate.properties['username'];  // Récupère la propriété 'username'
    var password = karate.properties['password'];  // Récupère la propriété 'password'
    var expiresInMins = karate.properties['expiresInMins'];  // Récupère la propriété 'expiresInMins'

    karate.log('Configuration de test:', username, password, expiresInMins);  // Vérification des propriétés dans les logs

    karate.configure('report', {
        "dir": "C:/Users/Lenovo/Desktop/karate-reports"  // Specify your custom reports folder
    });

    return {
        username: username,
        password: password,
        expiresInMins: expiresInMins,
        outputDir : "C:/Users/Lenovo/Desktop/karate-reports"
    };
}
