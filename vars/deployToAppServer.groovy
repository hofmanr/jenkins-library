import nl.rhofman.jenkins.utils.logging.Logger

def call(Map params = [:]) {
    def defaultParams = [pomlocation: 'pom.xml']
    def resolvedParams = defaultParams << params

    Logger.init(this)
    def logger = new Logger(this)

    logger.info "Params [destination: ${resolvedParams.destination}, credentials: ${resolvedParams.credentials}, pomLocation: ${resolvedParams.pomLocation}]"
}
