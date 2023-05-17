import nl.rhofman.jenkins.utils.logging.Logger

/**
 * Deployment to database
 * <ul>
 *     <li>artifactId       - optional: the artifact to be deployed; if not supplied get it from the pom.xml</li>
 *     <li>destination      - required: destination information (name of the destination and stage)<li>
 *     <li>releaseVersion   - optional: the version to be released; if not supplied get it from the pom.xml</li>
 *     <li>credentials      - required: the username/password or token for destination</li>
 *     <li>pomLocation      - optional: the pom.xml to be used; default value pom.xml
 * </ul>
 * @param params
 * @return
 */
def call(Map params = [:]) {
    def defaultParams = [pomlocation: 'pom.xml']
    def resolvedParams = defaultParams << params

    Logger.init(this)
    def logger = new Logger(this)

    logger.info "params [destination: ${resolvedParams.destination}, credentials: ${resolvedParams.credentials}, pomLocation: ${resolvedParams.pomLocation}]"

    String artifactId
    String version
    if (!resolvedParams.releaseVersion || !resolvedParams.artifactId) {
        def pom = readMavenPom file: resolvedParams.pomLocation
        if (!resolvedParams.releaseVersion) {
            version = pom.version
            logger.info "use pom version: $version"
        }
        if (!resolvedParams.artifactId) {
            artifactId = pom.artifactId
            logger.info "use pom artifactId: $artifactId"
        }
    }

    artifactId = resolvedParams.artifactId ?: artifactId
    version = resolvedParams.releaseVersion ?: version
}

