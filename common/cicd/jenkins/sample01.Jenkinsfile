pipeline {
    agent { 
        label 'camp' 
    }

    parameters {
        string defaultValue: 'develop', description: 'git branch', name: 'BRANCH', trim: false
        choice choices: ['dev1', 'qa1', 'qa2', 'azureqa1', 'azureqa2'], description: 'Publish environment name(ASPNETCORE_ENVIRONMENT in web.config)', name: 'ASPNETCORE_ENVIRONMENT'
    }

    triggers {
        // once every two hours at 45 minutes past the hour starting at 9:45 AM and finishing at 3:45 PM every weekday.
        pollSCM 'H 9-20/1 * * 1-5'
    }

    stages {
        stage('Clean') {
            steps {
                cleanWs()

                echo "Branch: ${params.BRANCH}"
                echo "Environment: ${params.ASPNETCORE_ENVIRONMENT}"
            }
        }

        stage('Build Velo Connector') {
            steps {
                dir('VeloCasinoDataConnector') {
                    git branch: "${params.BRANCH}", url: 'git@git.augmentum.com.cn:velo/velo-casino-data-connector.git', credentialsId: "velo-ci-git"
                }

                dir('VeloCasinoDataConnector/src/VeloConnector.Background') {
                    powershell(script: "dotnet clean --configuration Release", encoding: "UTF-8")
                    powershell(script: "dotnet build --configuration Release", encoding: "UTF-8")
                    powershell(script: "dotnet publish --configuration Release /p:EnvironmentName=${params.ASPNETCORE_ENVIRONMENT} --output ${WORKSPACE}/publish_${BUILD_NUMBER}/VeloCasinoDataConnector" , encoding: "UTF-8")
                }

                dir('VeloCasinoDataConnector/src/VeloConnector.Scheduler') {
                    powershell(script: "dotnet clean --configuration Release", encoding: "UTF-8")
                    powershell(script: "dotnet build --configuration Release", encoding: "UTF-8")
                    powershell(script: "dotnet publish --configuration Release /p:EnvironmentName=${params.ASPNETCORE_ENVIRONMENT} --output ${WORKSPACE}/publish_${BUILD_NUMBER}/VeloCasinoDataConnectorScheduler", encoding: "UTF-8")
                }
            }
        }

        stage('Build Terminal Notification Service') {
            steps {
                dir('VeloTerminalNotificationService') {
                    git branch: "${params.BRANCH}", url: 'git@git.augmentum.com.cn:velo/velo-terminal-notification-service.git', credentialsId: "velo-ci-git"
                }

                dir('VeloTerminalNotificationService/src/NotificationService.Background') {
                    powershell(script: "dotnet clean --configuration Release", encoding: "UTF-8")
                    powershell(script: "dotnet build --configuration Release", encoding: "UTF-8")
                    powershell(script: "dotnet publish --configuration Release /p:EnvironmentName=${params.ASPNETCORE_ENVIRONMENT} --output ${WORKSPACE}/publish_${BUILD_NUMBER}/VeloTerminalNotificationService", encoding: "UTF-8")
                }
            }
        }

        stage('Build MM Casino Data Service') {
            steps {
                dir('MobileManagerCasinoDataService') {
                    git branch: "${params.BRANCH}", url: 'git@git.augmentum.com.cn:velo/mobile-manager-casino-data-service.git', credentialsId: "velo-ci-git"
                }

                dir('MobileManagerCasinoDataService/src/CasinoDataService.Api') {
                    powershell(script: "dotnet clean --configuration Release", encoding: "UTF-8")
                    powershell(script: "dotnet build --configuration Release", encoding: "UTF-8")
                    powershell(script: "dotnet publish --configuration Release /p:EnvironmentName=${params.ASPNETCORE_ENVIRONMENT} --output ${WORKSPACE}/publish_${BUILD_NUMBER}/MobileManagerCasinoDataService", encoding: "UTF-8")
                }
            }
        }

        stage('Build Velo PM Simulator') {
            steps {
                dir('VeloSimulator') {
                    git branch: "${params.BRANCH}", url: 'git@git.augmentum.com.cn:velo/velo-simulator.git', credentialsId: "velo-ci-git"
                }

                dir('VeloSimulator/PatronManagement.Core') {
                    powershell(script: "dotnet clean --configuration Release", encoding: "UTF-8")
                    powershell(script: "dotnet build --configuration Release", encoding: "UTF-8")
                    powershell(script: "dotnet publish --configuration Release /p:EnvironmentName=${params.ASPNETCORE_ENVIRONMENT} --output ${WORKSPACE}/publish_${BUILD_NUMBER}/VeloSimulator", encoding: "UTF-8")
                }
            } 
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: "publish_${BUILD_NUMBER}/**/*.*"
            }
        }
    }
}