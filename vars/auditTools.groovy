def call(Map config) {
    echo "${config.message}"
    sh '''
        git version
        mvn --version
        echo "M2_HOME = ${M2_HOME}"
     '''
}
