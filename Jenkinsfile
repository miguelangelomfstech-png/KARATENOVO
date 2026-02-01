pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/miguelangelomfstech-png/KARATENOVO.git'
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn clean test'
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            cucumber '**/target/karate-reports/*.json'
            archiveArtifacts artifacts: 'target/karate-reports/**/*', allowEmptyArchive: true
        }
    }
}
