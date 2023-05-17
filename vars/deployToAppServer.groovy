import nl.rhofman.jenkins.utils.logging.Logger

def call(params = [:]) {
    def defaultParams = [pomlocation: 'pom.xml']
    def resolvedParams = params << defaultParams

    Logger.init(this)
    def logger = new Logger(this)

    logger.info "Params [name: ${resolvedParams.name}, stage: ${resolvedParams.stage}, pomLocation: ${resolvedParams.pomLocation}]"
}