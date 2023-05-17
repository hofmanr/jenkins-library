import nl.rhofman.jenkins.utils.logging.Logger

def call(params = [:]) {
    def defaultParams = [deployEnv: 'Develop', isSuccess: true]
    def resolvedParams = params << defaultParams

    Logger.init(this)
    def logger = new Logger(this)

    logger.info "Params [deployEnv: ${resolvedParams.deployEnv}, isSuccess: ${resolvedParams.isSuccess}]"
}
