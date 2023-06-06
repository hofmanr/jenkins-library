package nl.rhofman.jenkins.utils

import com.cloudbees.groovy.cps.NonCPS

import static groovy.io.FileType.FILES

class FileUtils {
    @NonCPS
    def static getPomDirectory(String pomFile) {
        def pos = pomFile.indexOf('pom.xml')
        def directory = '/'
        if (pos > 0) {
            return "/${pomFile.substring(0, pos)}"  // .e.g. /bsb-ejb/
        }
        return "/"
    }

    @NonCPS
    def static getFiles(String pathname, String extension) {
        def list = []

        // def scriptDir = getClass().protectionDomain.codeSource.location.path // the script directory
        // String currentDir = new File(".").getAbsolutePath() // current directory
        def dir = new File(pathname)
        dir.eachFileRecurse (FILES) { file ->
            if (file.name.endsWith(extension)) {
                list << file
            }
        }
        return list
    }
}
