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
            script {
                 // Generate Confluence Report
                 try {
                     sh 'mvn org.codehaus.mojo:exec-maven-plugin:3.1.0:java -Dexec.mainClass="com.api.framework.helpers.ConfluenceReportGenerator" -Dexec.classpathScope="test"'
                     archiveArtifacts artifacts: 'target/confluence-report.md', allowEmptyArchive: true
                 } catch (Exception e) {
                     echo 'Failed to generate Confluence Report'
                 }
            }
            archiveArtifacts artifacts: 'target/karate-reports/**/*', allowEmptyArchive: true
        }
    }
}
