pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox'], description: 'Browser to run tests on')
        choice(name: 'SUITE', choices: ['regression', 'smoke'], description: 'Test suite to run')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean test-compile'
            }
        }

        stage('Test') {
            steps {
                script {
                    def runner = params.SUITE == 'smoke' ? 'SmokeRunner' : 'RegressionRunner'
                    sh "mvn -B test -Dtest=${runner} -Dbrowser=${params.BROWSER} -Dheadless=true"
                }
            }
        }

        stage('Publish Reports') {
            steps {
                junit 'target/surefire-reports/*.xml'
                archiveArtifacts artifacts: 'target/cucumber-reports/*.html, target/logs/*.log',
                        allowEmptyArchive: true
            }
        }
    }

    post {
        always {
            // Requires the Jenkins Allure plugin
            allure includeProperties: false,
                   results: [[path: 'target/allure-results']]
            cleanWs()
        }
        failure {
            echo 'Build failed — check the Cucumber and Allure reports in the archived artifacts.'
        }
    }
}
