import nl.rhofman.jenkins.utils.logging.Logger

def call(Map params = [:]) {
    def defaultParams = [deployEnv: 'Develop', isSuccess: true]
    def resolvedParams = defaultParams << params

    Logger.init(this)
    def logger = new Logger(this)

    def result = resolvedParams.isSuccess ? "successful" : "unsuccessful"
    if (resolvedParams.isSuccess) {
        logger.info "Build ${env.BRANCH_NAME} with build #${env.BUILD_UD} was $result"
    } else {
        logger.fatal "Build ${env.BRANCH_NAME} with build #${env.BUILD_UD} was $result"
    }
    logger.info "URL for checking the result: ${env.BUILD_URL}"
}
