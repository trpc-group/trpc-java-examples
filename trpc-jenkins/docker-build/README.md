# docker-build pipeline

本Jenkins Pipeline包含如下功能：
1. git push/tag事件自动触发流水线执行
2. 拉取触发流水线的git repo所在分支的代码
3. 通过maven执行单元测试和构建
4. 使用构建产物制作docker镜像，镜像tag的生成逻辑为：
   - push动作触发的，镜像tag为git分支名称
   - git tag动作触发的，镜像tag为git tag名称
5. 将docker镜像推送至docker registry

## 使用流水线
步骤一：按 [此指引](../README.md) 的说明进行配置

步骤二：按 [此指引](../maven-build/README.md) 的说明进行Maven配置

## 流水线说明

环境变量：

| 变量名 | 默认值 | 说明 |
| --- | --- | --- |
| GIT_CREDENTIALS_ID | git-ssh-credential | 访问git所需的jenkins秘钥ID |
| MAVEN_INSTALLATION_NAME | maven | jenkins中配置的Maven installation名称 |
| DOCKERFILE | Dockerfile | 制作镜像使用的Dockerfile文件（含路径） |
| IMAGE | trpc-demo/trpc-docker-demo | 镜像名 |
| DOCKER_REGISTRY | mirrors.tencent.com | 镜像推送的docker registry |
| DOCKER_CREDENTIALS_ID | docker-registry-credential | 访问docker registry所需的Jenkins秘钥ID |

注意事项：
- 此pipeline适用于git webhook触发执行的场景，如果手动触发，需要修改Jenkinsfile中的 `GIT_URL` 和 `GIT_BRANCH`，手工填写其值，同时需要修改 `Docker build` stage中的tag生成逻辑，改为手工填写
- 制作镜像的Dockerfile需自行编写，并提交到git