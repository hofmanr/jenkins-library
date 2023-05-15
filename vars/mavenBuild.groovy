import nl.rhofman.jenkins.utils.logging.Logger

def call(Map<String, Object> params = [:]) {
    Map<String, Object> resolvedParams = [
            pomLocation: 'pom.xml',
            mavenSettingsFile: 'default-maven-settings',
            arguments: 'clean package'
    ] << params

    Logger.init(this)
    def logger = new Logger(this)

    logger.info "build with arguments ${resolvedParams.arguments}"
    // withMaven(mavenSettingsConfig: resolvedParams.mavenSettingsFile) {sh "mvn clean package"}
    sh "mvn -f ${resolvedParams.pomLocation} ${resolvedParams.arguments}"
}
