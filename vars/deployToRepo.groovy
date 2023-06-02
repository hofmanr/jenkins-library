import nl.rhofman.jenkins.utils.logging.Logger
import static groovy.io.FileType.FILES

/**
 * Deployment to Repository
 * The settings.xml must contain the credentials
 * <ul>
 *     <li>pomLocation       - optional: the pom.xml to be used; default value pom.xml
 *     <li>groupId           - optional: the groupId of the deployed artifact; if not supplied get it from the pom.xml</li>
 *     <li>artifactId        - optional: the artifact of the deployed artifact; if not supplied get it from the pom.xml</li>
 *     <li>url               - required: the repository URL (e.g. Nexus repo URL</li>
 *     <li>packaging         - required: jar, war, ear or zip; artifact must have this extension and located in target directory</li>
 *     <li>mavenSettingsFile - optional: the maven settings file to be used; must be registered in Jenkins</li>
 *     <li>repositoryId      - optional: the repository ID to be used; default nexus-local</li>
 * </ul>
 * @param params
 * @return
 */
def call(Map params = [:]) {
    Map<String, Object> resolvedParams = [
            pomLocation: 'pom.xml',
            mavenSettingsFile: 'default-maven-settings',
            repositoryId: 'nexus-local'
    ] << params

    def logger = new Logger(this)

    logger.info "deploy with arguments ${resolvedParams.arguments}"

    // Search for the artifact
    String pomLocation = resolvedParams.pomLocation
    def pos = pomLocation.indexOf('pom.xml')
    def directory = '.'
    if (pos > 0) {
        directory = "./${pomLocation.substring(0, pos)}"  // .e.g. ./bsb-ejb/
    }
    logger.info("look for artifacts in directory $directory")
    def extension = ".${resolvedParams.packaging}"
    String artifact = ''
    new File(directory).eachFileRecurse(FILES) {
        if (it.name.endsWith(extension)) {
            artifact = it.absolutePath
        }
    }
    logger.info("artifact $artifact")

    withMaven(globalMavenSettingsConfig: resolvedParams.mavenSettingsFile) {
        sh "mvn deploy:deploy-file -Durl=${resolvedParams.url} -Dfile=$artifact -Dpackaging=${resolvedParams.packaging} -DrepositoryId=${resolvedParams.repositoryId} -DpomFile=$pomLocation"
    }
}
