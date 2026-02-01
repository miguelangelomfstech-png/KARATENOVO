pipeline {
    agent any

    tools {
        // Certifique-se de que o nome 'maven-3.9' e 'jdk-17' 
        // coincida com o que está configurado em "Global Tool Configuration" no seu Jenkins
        maven 'maven-3.9'
        jdk 'jdk-17'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/miguelangelomfstech-png/KARATENOVO.git'
            }
        }

        stage('Build & Clean') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Execute Tests') {
            steps {
                // Executa os testes e gera o relatório
                sh 'mvn clean test; mvn test-compile exec:java -DargLine="-Dkarate.env=qa"'
            }
            post {
                always {
                    // Publica os resultados do JUnit no Jenkins
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Archive Reports') {
            steps {
                // Arquiva o report HTML e o Markdown do Confluence
                archiveArtifacts artifacts: '**/target/karate-reports/**, **/target/confluence-report.md', allowEmptyArchive: true
            }
        }
    }

    post {
        success {
            echo 'Testes finalizados com sucesso!'
        }
        failure {
            echo 'A automação encontrou falhas.'
        }
    }
}