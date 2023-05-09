def call(Map<String, Object> params = [:]) {
    Map<String, Object> resolvedParams = [
            pomLocation: 'pom.xml',
            mavenSettingsFile: 'default-maven-settings',
            arguments: 'clean package'
    ] << params

    // withMaven(mavenSettingsConfig: resolvedParams.mavenSettingsFile) {sh "mvn clean package"}
    sh "mvn -f ${resolvedParams.pomLocation} ${resolvedParams.arguments}"
}
