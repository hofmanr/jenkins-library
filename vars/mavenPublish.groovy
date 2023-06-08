import nl.rhofman.jenkins.utils.FileUtils
import nl.rhofman.jenkins.utils.logging.Logger
i
/**
 * Publish artifacts to Repository (e.g. Nexus)
 * The settings.xml must contain the credentials (for Nexus)
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

    Logger.init(this)
    def logger = new Logger(this)

    logger.info "params ${resolvedParams}"

    // Search for artifacts in de pom location and it's subdirectories
    def directory = FileUtils.getPomDirectory(resolvedParams.pomLocation)
    def pathname = "${env.WORKSPACE}$directory"  // full path
    def extension = ".${resolvedParams.packaging}"
    logger.info("look for artifacts in subdirectories $pathname with extension $extension")
    def fileList = FileUtils.getFiles(pathname, extension)
    if (fileList.size() == 0) {
        logger.warn("No artifacts found")
        return
    }

    // Get version from pom-file
    def pom = readMavenPom file: resolvedParams.pomLocation
    String version = pom.version
    // Get repository URL (property in settings.xml
    def repoUrl = "${env.nexus.repo.releases}"
    if (version.toUpperCase().endsWith("-SNAPSHOT")) {
        repoUrl = "${env.nexus.repo.snapshots}"
    }
    logger.info("Repo URL $repoUrl")

    if (repoUrl == null || repoUrl =="") {
        repoUrl = "${resolvedParams.url}"
    }

    // Show the maven settings
    configFileProvider([configFile(fileId: resolvedParams.mavenSettingsFile, variable: 'MAVEN_GLOBAL_SETTINGS')]) {
        sh "mvn -gs $MAVEN_GLOBAL_SETTINGS help:effective-settings"
    }

    fileList.each { file ->
        def artifact = file.absolutePath
        logger.info("publish artifact $artifact")

        configFileProvider([configFile(fileId: resolvedParams.mavenSettingsFile, variable: 'MAVEN_GLOBAL_SETTINGS')]) {
            sh "mvn -gs $MAVEN_GLOBAL_SETTINGS deploy:deploy-file -Durl=${resolvedParams.url} -Dfile=$artifact -Dpackaging=${resolvedParams.packaging} -DrepositoryId=${resolvedParams.repositoryId} -DpomFile=${resolvedParams.pomLocation}"
        }

//        withMaven(globalMavenSettingsConfig: resolvedParams.mavenSettingsFile) {
//            sh "mvn deploy:deploy-file -Durl=${resolvedParams.url} -Dfile=$artifact -Dpackaging=${resolvedParams.packaging} -DrepositoryId=${resolvedParams.repositoryId} -DpomFile=${resolvedParams.pomLocation}"
//        }
    }
}
