package nl.rhofman.jenkins.utils.pipeline

import com.cloudbees.groovy.cps.NonCPS

class PomInfo {

    @NonCPS
    static HashMap getPomInfo() {
        return [:]
    }

    @NonCPS
    static String getVersion(String pomLocation) {
        def pom = readMavenPom file: pomLocation
        return pom.version
    }
}
