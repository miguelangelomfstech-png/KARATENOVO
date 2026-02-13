pipeline {
    agent any

    tools {
        maven 'maven-3.9'
        jdk 'jdk-17'
        // Add dotnet tool if configured in Jenkins, otherwise assume it's in PATH
        // dotnet 'dotnet-8.0' 
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/miguelangelomfstech-png/KARATENOVO.git'
            }
        }

        stage('Test All Frameworks') {
            parallel {
                stage('Karate (Java)') {
                    steps {
                        dir('KARATENOVO') {
                             // Assuming root is KARATENOVO, but the git repo IS KARATENOVO. 
                             // So we are already in the root. 
                             // Wait, the checkout checks out the repo. 
                             // The existing Jenkinsfile assumes we are in root.
                             // But wait, the existing Jenkinsfile has "dir('KARATENOVO')"? No.
                             // It just says "sh 'mvn clean compile'".
                             // So the root of the repo is the Karate project.
                             sh 'mvn clean test -Dtest=TestRunner'
                        }
                    }
                }

                stage('Playwright (Java)') {
                    steps {
                        dir('KARATENOVO_PLAYWRIGHT') {
                            sh 'mvn clean test'
                        }
                    }
                }

                stage('Selenium (Java)') {
                    steps {
                        dir('KARATENOVO_SELENIUM') {
                            sh 'mvn clean test'
                        }
                    }
                }
                
                stage('Selenium (C#)') {
                    steps {
                        dir('KARATENOVO_SELENIUM_CSHARP') {
                            // Using bat for Windows agents, sh for Linux.
                            // Assuming 'dotnet' is in the PATH
                            script {
                                if (isUnix()) {
                                    sh 'dotnet test'
                                } else {
                                    bat 'dotnet test'
                                }
                            }
                        }
                    }
                }
            }
        }

        stage('Benchmark & Spec') {
            steps {
                // These artifacts are already generated and committed, 
                // but in a real CI they should be generated during the build.
                // For now we just archive them if they exist.
                script {
                    if (fileExists('benchmark_report.html')) {
                        archiveArtifacts artifacts: 'benchmark_report.html'
                    }
                    if (fileExists('TECHNICAL_FUNCTIONAL_SPEC.pdf')) {
                        archiveArtifacts artifacts: 'TECHNICAL_FUNCTIONAL_SPEC.pdf'
                    }
                }
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml, **/TestResults/*.trx'
        }
        success {
            echo 'Todos os projetos foram executados com sucesso!'
        }
        failure {
            echo 'Houve falhas na execução de um ou mais projetos.'
        }
    }
}