# maven-build pipeline

本Jenkins Pipeline包含如下功能：
1. git push/tag事件自动触发流水线执行
2. 拉取触发流水线的git repo所在分支的代码
3. 通过maven执行单元测试和构建
4. 将构建产物归档至Jenkins

## 使用流水线
步骤一：按 [此指引](../README.md) 的说明进行配置

步骤二：配置Maven
1. 配置Maven Installation
    1. Manage Jenkins > Global Tool Configuration > Maven
    2. 添加Maven Installation，填写名称（本demo中命名为`maven`），选择需要的版本，勾选`Install automatically`
2. 配置Maven settings.xml（用于设置全局maven repository，可选）
    1. Manage Jenkins > Managed files
    2. 点击Add a new config，类型选择Maven settings.xml
    3. 填写ID、Name，Content中粘贴 [settings.xml](../settings.xml) 的内容，点击Submit
    4. Manage Jenkins > Global Tool Configuration > Maven Configuration
    5. Default settings provider选择`Provided settings.xml`，Provided Settings选择刚刚配置的settings.xml文件，Save


## 流水线说明

环境变量：

| 变量名 | 默认值 | 说明 |
| --- | --- | --- |
| GIT_CREDENTIALS_ID | git-ssh-credential | 访问git所需的jenkins秘钥ID |
| MAVEN_INSTALLATION_NAME | maven | jenkins中配置的Maven installation名称 |

注意事项：
- 此pipeline适用于git webhook触发执行的场景，如果手动触发，需要修改Jenkinsfile中的 `GIT_URL` 和 `GIT_BRANCH`，手工填写其值
- pipeline中执行的单元测试、构建等操作的具体逻辑由实际项目的maven配置（pom.xml）定义。例如可以通过调整pom.xml实现在Build环节中将制品发布到maven仓库