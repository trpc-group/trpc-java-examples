# trpc-java-demos-jenkins

使用Jenkins Pipeline构建的tRPC-java服务CI/CD流水线demo

包含下列pipeline：

| pipeline | 说明 |
|---|---|
| [maven-build](maven-build) | maven项目单元测试、构建、制品归档 |
| [maven-docker-build](docker-build) | 在[maven-build](maven-build)基础上，增加docker image的制作和发布 |
| [deploy-jar-via-ssh](deploy-jar-via-ssh) | 通过SSH向remote server部署jar |
| [update-k8s-deployment](update-k8s-deployment) | 更新k8s deployment镜像 |

上述pipeline demo展示了使用Jenkins Pipeline构建tRPC-java服务CI/CD流水线的常见用法，请根据实际应用场景对上述pipeline进行调整/组合

## 环境准备

运行本demo需要Jenkins 2.2或以上版本（建议使用最新LTS版本），安装官方推荐插件集，并额外安装以下插件：

- Pipeline Maven Integration (用于集成maven)
- GitLab Plugin (用于配置GitLab webhook自动触发流水线)
- Copy Artifact Plugin（用于访问Jenkins归档制品）
- SSH Agent Plugin (用于通过SSH访问remote server)
- Kubernetes CLI（用于部署k8s workload）

为避免歧义，本篇指引中所有Jenkins名词均使用英文，可通过`Locale`插件调整Jenkins默认语言

### 安装Jenkins

1. 按照官方文档 [Installing Jenkins](https://www.jenkins.io/doc/book/installing/) 的指引安装并启动jenkins，初始化时安装**
   官方推荐的插件集**
2. 安装必要插件（见上），具体方法如下：
    1. Manage Jenkins > Plugin Manager > Available Plugins
    2. 搜索框内输入插件名称，勾选所需安装的插件，点击 `Install without restart`
    3. 插件全部安装完成后，重启Jenkins服务
       > http://{your-jenkins-host}/saferestart

### 配置Jenkins

1. 配置Git秘钥（用于从Git拉取代码）：
    1. 生成SSH公私钥对，向Git提交公钥（可复用已经提交过的公钥）。如果不清楚如何配置SSH访问Git，请先查阅相关资料
    2. Manage Jenkins > Manage Credentials
    3. 选择预设的Domain `(global)`，点击右上角 `Add Credentials`
    4. Kind选择`SSH Username with private key`，填写ID（本demo中命名为`git-ssh-credential`
       ）、Username（可选）、Private Key，点击Save
2. 配置Git Host Key Verification（用于首次通过SSH访问Git时自动信任Host）
    1. Manage Jenkins > Configure Global Security > Git Host Key Verification Configuration
    2. Strategy选择`Accept first connection`，点击Save

## 使用流水线

本节介绍如何使用demo中的流水线。

Jenkins运行pipeline时，pipeline定义有两种来源：

- 在Jenkins Job配置界面中填写pipeline script
- 从指定的Git repo中拉取pipeline script

从Pipeline as Code的理念角度出发，更加推荐后者，便于统一维护pipeline的定义，跟踪变更历史记录等。后续将以【从指定的Git repo中拉取pipeline
script】方式介绍pipeline的使用方法。

综上所述，Jenkins Pipeline的运行中涉及到两种git repo：

- source repo：要执行CI的项目源代码repo
- pipeline repo：保存Jenkins Pipeline脚本的repo

使用本demo中提供的pipeline时，可以将pipeline repo直接设定为本repo，但更推荐将demo中的 `Jenkinsfile` 文件拷贝至自行创建pipeline
repo里，便于随时修改和调整。 source repo则应自行准备.

### 配置Jenkins Pipeline

1. 创建Jenkins Job
   > Dashboard > New Item，填写Job名称，类型选择 `Pipeline`
2. 配置pipeline脚本（从SCM获取，推荐）
    1. 在Job配置的Pipeline段，`Definition`下拉选择 `Pipeline script from SCM`
    2. `SCM` 下拉选择 `Git`，Repository URL填写pipeline repo，Credentials选择之前创建的Git秘钥
    3. `Branch Specifier` 填写 `master`，即使用master分支上的`Jenkinsfile`，也可根据具体需求进行调整
    4. `Script Path` 默认为pipeline repo根目录下的`Jenkinsfile`文件，可根据需要调整（如 `maven-build/Jenkinsfile`）
3. 配置pipeline脚本（存储在Jenkins）
    1. 在Job配置的Pipeline段，`Definition`下拉选择 `Pipeline script`
    2. 在Script输入框中直接编辑pipeline脚本
4. 配置GitLab webhook（可选）
   > 通过GitLab webhook可以实现在git push后自动触发Jenkins流水线
    1. 在 `Build Triggers` 段中勾选 `Build when a change is pushed to GitLab`，把生成的webhook URL拷贝下来
    2. triggers勾选 `Push Events`
    3. 打开 `Advanced` 段，生成Secret Token
    4. 在想要触发此Job的GitLab repo中配置webhook，Secret
       Token填写上面刚刚生成的token，URL填写步骤1中拷贝下来的URL，trigger根据需要选择（比如 `Push Events` 和 `Tag Push Events`)

### 触发Jenkins Pipeline

- 向source repo进行push即会自动触发pipeline
- 具体的pipeline说明，请参考各个pipeline目录下的README.md