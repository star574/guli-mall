pipeline {
    agent {
    node {
        label 'maven'
    }
    }

    parameters {
          string(name:'PROJECT_VERSION',defaultValue: 'V0.0BETA',description:'')
          string(name:'PROJECT_NAME',defaultValue: '',description:'')
    }
    environment {
            DOCKER_CREDENTIAL_ID = 'dockerhub-id'
            GITHUB_CREDENTIAL_ID = 'github-id'
            KUBECONFIG_CREDENTIAL_ID = 'demo-kubeconfig'
            REGISTRY = 'docker.io'
            DOCKERHUB_NAMESPACE = 'star574'
            GITHUB_ACCOUNT = 'star574'
            SONAR_CREDENTIAL_ID = 'sonar-qube'
    }
    stages {
    stage('拉取代码') {
      steps {
        git(url: 'https://github.com/star574/guli-mall.git', credentialsId: 'github-token', branch: 'dev', changelog: true, poll: false)
        sh 'echo 正在构建:  $PROJECT_VERSION  版本号:  $PROJECT_VERSION'
      }
    }
    stage('sonarqube代码质量分析') {
      steps {
        container ('maven') {
          withCredentials([string(credentialsId: "$SONAR_CREDENTIAL_ID", variable: 'SONAR_TOKEN')]) {
            withSonarQubeEnv('sonar') {
             sh "mvn sonar:sonar -o -gs `pwd`/guli-mall/mvn-setting.xml -Dsonar.login=$SONAR_TOKEN"
            }
          }
          timeout(time: 1, unit: 'HOURS') {
            waitForQualityGate abortPipeline: true
          }
        }
      }
    }
    }
}