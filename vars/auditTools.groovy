def call(Map config) {
    node {
        echo "${config.message}"
        sh '''
         git version
         mvn --version
         echo "M2_HOME = ${M2_HOME}"
      '''
    }
}
