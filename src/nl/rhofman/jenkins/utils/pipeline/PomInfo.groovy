package nl.rhofman.jenkins.utils.pipeline

import com.cloudbees.groovy.cps.NonCPS

class PomInfo {

    @NonCPS
    static HashMap getPomInfo() {
        return [:]
    }
}
