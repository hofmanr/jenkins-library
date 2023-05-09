def call(Map config) {
    node {
        echo "${config.message}"
        sh '''
         git version
         java -version
         echo "M2_HOME = ${M2_HOME}"
         ${M2_HOME}/bin/mvn --version
      '''
    }
}
