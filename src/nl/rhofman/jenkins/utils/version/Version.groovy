package nl.rhofman.jenkins.utils.version

import com.cloudbees.groovy.cps.NonCPS
import nl.rhofman.jenkins.utils.pipeline.PomInfo

class Version {
    def logger

    Version(logger) {
        this.logger = logger
    }

    @NonCPS
    String getVersion(String versionStrategy = "GitFlow", String prefix, String pomLocation = "pom.xml") {
        String strategy = versionStrategy.toUpperCase()
        String branch = "${env.BRANCH_NAME}"  // e.g. feature/BSB-3454 (BSB-3454 could be a ticket in Jira) or develop or master or release/1.2.0
        logger.info "determine version of branch $branch"

        // Always use the tag if it's set
        if (branch.contains('master') || branch.contains('main') || branch.contains('develop')) {
            String lastTag = sh(returnStdout: true, script: "git describe --tags --abbrev=0").trim()
            if (!lastTag.toUpperCase().contains("NO NAMES FOUND") && !lastTag.toUpperCase().contains("FATAL")) {
                return lastTag // use last tag
            }
        }

        // Use feature branching strategy
        if (strategy == "FEATUREBRANCH") {
            // Get version from pom.xml and remove '-SNAPSHOT'
            String pomVersion = PomInfo.getVersion(pomLocation).replaceAll('-SNAPSHOT', "")
            String prefixSep = "$prefix-".toUpperCase()  // e.g. BSB-
            // Get total commit count
            if (branch.contains('master') || branch.contains('main') || branch.contains('develop')) {
                String commitCount = sh(returnStdout: true, script: "git rev-list --all --count").trim()
                return "${pomVersion}-${commitCount}"
            }
            if (branch.contains(prefixSep)) {
                def ticketNo = branch.split(prefixSep)[1]
                return "${pomVersion}-${ticketNo}-SNAPSHOT"
            }
            return "${pomVersion}-${branch}-SNAPSHOT"
        }

        if (strategy == "GITFLOW") {
            if (branch.contains('release')) {
                // release; e.g. release/1.2.0
                return branch.replace('release/', '')
            }

            // Get version from pom.xml and remove '-SNAPSHOT'
            String pomVersion = PomInfo.getVersion(pomLocation).replaceAll('-SNAPSHOT', "")
            String prefixSep = "$prefix-"  // e.g. BSB-
            if (branch.contains("feature") || branch.contains("hotfix")) {
                if (branch.contains(prefixSep)) {
                    // Ticket for feature branches e.g. feature/BSB-4536
                    def ticketNo = branch.split(prefixSep)[1]
                    logger.info 'snapshot version with ticket'
                    return "${pomVersion}-${ticketNo}-SNAPSHOT"
                }
                String branchName = branch
                if (branch.contains("feature")) {
                    branchName = branch.replace('feature/', '')
                }
                if (branch.contains("hotfix")) {
                    branchName = branch.replace('hotfix/', '')
                }
                logger.info 'snapshot version with branch name'
                return "${pomVersion}-${branchName}-SNAPSHOT"
            }

            if (branch.contains("develop")) {
                logger.info 'develop version'
                return "${pomVersion}-SNAPSHOT"
            }
            // Get total commit count
            logger.info 'version with commit count'
            String commitCount = sh(returnStdout: true, script: "git rev-list --all --count").trim()
            return "${pomVersion}-${commitCount}"
        }

        logger.info 'default version from pom'
        return PomInfo.getVersion(pomLocation).replaceAll('-SNAPSHOT', "")
    }
}
