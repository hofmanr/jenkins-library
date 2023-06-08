import nl.rhofman.jenkins.utils.logging.Logger

def call(Map<String, Object> params = [:]) {
    Map<String, Object> resolvedParams = [
            pomLocation: 'pom.xml',
            deleteBuildOnBump: false,
            renameJenkinsBuild: false
    ] << params

    Logger.init(this)
    def logger = new Logger(this)
    logger.info "init build job from ${resolvedParams.pomLocation}"

    // Multibranch pipeline jobs possesses the environment variable BRANCH_NAME; normal pipeline jobs not
    if (!env.BRANCH_NAME) {
        //env.BRANCH_NAME = sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD 2> /dev/null').trim()
        env.BRANCH_NAME = "${GIT_BRANCH.split("/")[1]}"  // e.g. origin/main
    }

    def authors = currentBuild.changeSets.collectMany {
        it.toList().collect {
            it.getAuthor().getId()
        }
    }
    // Remove the Jenkins user (commits by jenkins, e.g. for a new release, don't count)
    // This prevents that builds get into a loop
    authors -= 'jenkins'

    env.IS_AUTOMATED_BUILD = true
    def causedByUser = 'Jenkins_Scheduled_TriggeredByGit' // e.g. scheduled or triggered by Git via a hook
    // Check if the build is initiated by a user
    if (currentBuild.rawBuild.getCause(Cause.UserIdCause)) {
        // The user who has started the job:
        causedByUser = currentBuild.rawBuild.getCause(Cause.UserIdCause).getUserId().toString()
        env.IS_AUTOMATED_BUILD = false
    }

    // if authors is empty (no GIT-commit by user) and the job is not started by a user, then abort the job
    if (authors.empty && Boolean.valueOf(env.IS_AUTOMATED_BUILD) && (Integer.valueOf(env.BUILD_NUMBER) > 1)) {
        sh "Abort pipeline job"
        if (resolvedParams.deleteOnBump) {
            currentBuild.rawBuild.delete()
        }
        currentBuild.result = Result.ABORTED
        currentBuild.displayName = "#${env.BUILD_NUMBER} aborted. No new commits"
        error("Last commit bumped the version, aborting build to prevent looping")
    }

    env.LAST_COMMITTER = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%an'").trim()
    logger.info "logger fullProjectName='${currentBuild.fullProjectName}', build='${currentBuild.displayName}', automated=${env.IS_AUTOMATED_BUILD}, lastCommitter='${env.LAST_COMMITTER}', authors='${authors.unique()}'"
}

