# deploy-jar-via-ssh pipeline

本Jenkins Pipeline包含如下功能：
1. 获取在其他流水线中构建并归档的制品文件（jar）
2. 将制品文件推送至指定的remote server
3. 在remote server执行部署命令

此流水线涉及服务部署动作，不建议由webhook自动触发（开发环境除外）

## 使用流水线
步骤一：按 [此指引](../README.md) 的说明进行配置

步骤二：配置SSH免密登录
1. （可选）登录需要部署服务的主机，创建部署服务使用的用户
   ```
   useradd <user>
   passwd <user>
   ```
2. 登录Jenkins所在主机，生成公私钥对：
   ```
   ssh-keygen -t rsa
   ```
3. 推送公钥至需要部署服务的主机
   ```
   ssh-copy-id -i id_rsa.pub <user>@<remote_server>
   ```
4. 新增Jenkins Credential
   1. 类型选择 `SSH Username with private key`
   2. Username填写登录远程主机使用的用户名、ID任意（本例中为`remote_server_ssh`）
   3. Private key粘贴第2步中生成的私钥内容

## 流水线说明

环境变量：

| 变量名 | 默认值 | 说明 |
| --- | --- | --- |
| CI_JOB_NAME | ci-demo | 生成制品文件的Jenkins job名称，即从这个job的构建历史中获取归档的jar包 |
| JAR_NAME | trpc-java-demos-1.0.0-SNAPSHOT.jar | jar文件名（不包含路径） |
| DEST_PATH | /data/home/dist/ | 推送jar文件至远程主机的目录 |
| TARGETS | host1,host2 | 远程主机列表，逗号分隔 |
| USERNAME | jenkins | SSH远程主机使用的用户名 |
| SSH_CREDENTIAL_ID | remote_server_ssh | SSH远程主机使用的Jenkins秘钥ID |
| DEPLOY_CMD | sh /data/home/dist/deploy.sh | 在远程主机上执行的部署脚本（即服务重启脚本）|

构建参数：

| 变量名 | 默认值 | 说明 |
| --- | --- | --- |
| BUILD_NO |  | 指定CI_JOB_NAME的build number，留空则使用最近一次的stable build |

注意事项：
- 包含构建参数的pipeline在初次执行之前，Jenkins不会识别其中定义的入参，初次执行之后，就可以在Jenkins界面中看到 `Build with Parameters` 操作了