pipeline {
    agent { 
        label 'camp' 
    }

    parameters {
        choice choices: ['dev1', 'qa1', 'qa2', 'azureqa1', 'azureqa2'], description: 'Publish environment name(ASPNETCORE_ENVIRONMENT in web.config)', name: 'ASPNETCORE_ENVIRONMENT'
        booleanParam(name: 'BUILD_ACR_IMAGE', defaultValue: false, description: 'Whether need to build/upload docker image to Azure')
        string defaultValue: 'velo/pm-simulator', description:'Image name. Default image tag pattern is <yyyyMMdd>.<git short commit id>. Use : to provide custom tag.', name: 'ACR_IMAGE_NAME'
        credentials defaultValue:'velo-poc-acr', description: 'Jenkins credential id that configured with azure service principal', name: 'ACR_CREDENTIAL_ID'
        string defaultValue: 'velopoc', description:'ACR registry name', name: 'ACR_REGISTRY_NAME'
        string defaultValue: 'Crown-PoC', description:'ACR resource group', name: 'ACR_RESOURCE_GROUP_NAME'
    }

    stages {
        stage('Prepare') {
            steps {
                cleanWs()
                checkout scm
            }
        }

        stage('ACR image build'){
            when {
                environment name: 'BUILD_ACR_IMAGE', value: 'true'
            }

            steps{
                script {
                    if ("${params.ACR_IMAGE_NAME}".contains(':')) { 
                        echo "Tag is supplied in the docker image name: ${params.ACR_IMAGE_NAME}"
                        finalDockerImageName = "${params.ACR_IMAGE_NAME}"
                    } else {
                        echo "Use default tag pattern for docker image. Git commit id: ${GIT_COMMIT}"
                        gitCommitId = "${GIT_COMMIT}".substring(0, 6)
                        currentDate = new Date().format("yyyyMMdd")
                        
                        finalDockerImageName = "${params.ACR_IMAGE_NAME}:${currentDate}.${gitCommitId}"
                    }
                    echo "Final image name: ${finalDockerImageName}"
                }

                dir('.') {
                    acrQuickTask azureCredentialsId: "${params.ACR_CREDENTIAL_ID}", 
                        dockerfile: "Dockerfile", 
                        imageNames: [[image: "${finalDockerImageName}"]], 
                        registryName: "${params.ACR_REGISTRY_NAME}", 
                        resourceGroupName: "${params.ACR_RESOURCE_GROUP_NAME}"
                }
            }
        }
        
        stage('Build PatronManagement') {
            steps {
                dir('PatronManagement.Core') {
                    powershell(script: "dotnet clean --configuration Release", encoding: "UTF-8")
                    powershell(script: "dotnet build --configuration Release", encoding: "UTF-8")
                    powershell(script: "dotnet publish --configuration Release /p:EnvironmentName=${params.ASPNETCORE_ENVIRONMENT} --output ${WORKSPACE}/publish_${BUILD_NUMBER}/VeloPatronManagement", encoding: "UTF-8")
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