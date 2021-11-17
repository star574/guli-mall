pipeline {
  agent {
    node {
      label 'maven'
    }

  }
  stages {
    stage('拉取代码并编译') {
      steps {
           container('maven') {
              git(url: 'https://github.com/star574/guli-mall.git', credentialsId: 'github-token', branch: 'dev', changelog: true, poll: false)
              sh 'echo 正在构建:  $PROJECT_NAME  版本号:  $PROJECT_VERSION'
              sh 'echo 开始编译 ! && mvn clean install -gs `pwd`/mvn-settings.xml -Dmaven.test.skip=true'
        }
      }
    }
    stage('sonarqube 代码质量分析') {
          steps {
            container('maven') {
              withCredentials([string(credentialsId: "$SONAR_CREDENTIAL_ID", variable: 'SONAR_TOKEN')]) {
                withSonarQubeEnv('sonar') {
                  sh "mvn sonar:sonar  -gs `pwd`/mvn-settings.xml -Dsonar.login=$SONAR_TOKEN"
                }
              }
              timeout(time: 1, unit: 'HOURS') {
                waitForQualityGate true
              }
            }
          }
        }

      stage('构建 & 推送最新镜像') {
      steps {
        container('maven') {
          sh 'mvn -o -Dmaven.test.skip=true -gs `pwd`/mvn-settings.xml clean package'
          sh 'cd $PROJECT_NAME && docker build --no-cache -f Dockerfile -t $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER .'
          withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$DOCKER_CREDENTIAL_ID" ,)]) {
            sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
            sh 'docker tag  $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:latest '
            sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:latest '
            }
          }
        }
      }

      stage('部署到到k8s') {
          steps {
            input(id: "deploy-to-dev-$PROJECT_NAME", message: "是否发布$PROJECT_NAME?")
            kubernetesDeploy(configs: "$PROJECT_NAME/deploy/**", enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
          }
      }
      stage('发布当前版本'){
          when{
            expression{
              return params.PROJECT_VERSION =~ /v.*/
            }
          }
          steps {
              container ('maven') {
                input(id: 'release-image-with-tag', message: '发布当前版本镜像到git?')
                  withCredentials([usernamePassword(credentialsId: "$GITHUB_CREDENTIAL_ID", passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                    sh 'git config --global user.email "shihengluo574@gmail.com" '
                    sh 'git config --global user.name "star574" '
                    sh 'git tag -a $PROJECT_VERSION -m "$PROJECT_VERSION" '
                    sh 'git push http://$GIT_USERNAME:$GIT_PASSWORD@github.com/$GITHUB_ACCOUNT/guli-mall.git --tags --ipv4'
                  }
                sh 'docker tag  $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:$PROJECT_VERSION '
                sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:$PROJECT_VERSION '
          }
          }
        }

  }
  environment {
    DOCKER_CREDENTIAL_ID = 'dockerhub-id'
    GITHUB_CREDENTIAL_ID = 'github-id'
    KUBECONFIG_CREDENTIAL_ID = 'demo-kubeconfig'
    REGISTRY = 'docker.io'
    DOCKERHUB_NAMESPACE = 'star574'
    GITHUB_ACCOUNT = 'star574'
    SONAR_CREDENTIAL_ID = 'sonar-qube'
    BRANCH_NAME='dev'
  }
  parameters {
    string(name: 'PROJECT_VERSION', defaultValue: 'V0.0-BETA', description: '')
    string(name: 'PROJECT_NAME', defaultValue: 'gulimall-gateway', description: '')
  }
}