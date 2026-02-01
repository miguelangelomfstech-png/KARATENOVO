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
                // Executa os testes e gera os arquivos de saída para o report
                // O Karate geralmente gera relatórios em target/karate-reports
                sh 'mvn test -DargLine="-Dkarate.env=qa"'
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
                // Arquiva o report HTML do Karate ou Cucumber para visualização posterior
                archiveArtifacts artifacts: '**/target/karate-reports/**', allowEmptyArchive: true
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