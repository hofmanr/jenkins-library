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

//    configFileProvider(
//            [configFile(fileId: jenkinsSettings, variable: 'MAVEN_GLOBAL_SETTINGS')]) {
//        sh 'mvn -gs $MAVEN_GLOBAL_SETTINGS deploy'
//    }

    withMaven(globalMavenSettingsConfig: resolvedParams.mavenSettingsFile) {
        sh "mvn -f ${resolvedParams.pomLocation} ${resolvedParams.arguments}"
    }
//    sh "mvn -f ${resolvedParams.pomLocation} ${resolvedParams.arguments}"
}
