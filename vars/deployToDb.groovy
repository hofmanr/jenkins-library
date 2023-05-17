import nl.rhofman.jenkins.utils.logging.Logger

def call(Map params = [:]) {
    def defaultParams = [pomlocation: 'pom.xml']
    def resolvedParams = defaultParams << params

    Logger.init(this)
    def logger = new Logger(this)

    logger.info "Params [name: ${resolvedParams.name}, stage: ${resolvedParams.stage}, pomLocation: ${resolvedParams.pomLocation}]"

    def pom = readMavenPom file: resolvedParams.pomLocation
    logger.info "Pom [version: ${pom.version}, artitfact: ${pom.artifactId}]"
}

