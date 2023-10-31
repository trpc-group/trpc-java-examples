# update-k8s-deployment pipeline

本Jenkins Pipeline包含如下功能：
1. 更新指定k8s deployment使用的镜像tag

此流水线涉及服务部署动作，不建议由webhook自动触发（开发环境除外）

## 使用流水线
步骤一：按 [此指引](../README.md) 的说明进行配置

步骤二：配置kubeconfig秘钥
- 新增Jenkins credential，类型为 `Secret file`，填写ID（本demo中使用`k8s_kubeconfig`），内容为k8s集群的kubeconfig文件

## 流水线说明

环境变量：

| 变量名 | 默认值 | 说明 |
| --- | --- | --- |
| KUBECONFIG_ID | k8s_kubeconfig | Jenkins中配置的kubeconfig秘钥ID |
| DEPLOYMENT_NAME | trpc-demo | k8s deployment名称 |
| CONTAINER_NAME | java | k8s container名称 |
| IMAGE | mirrors.tencent.com/trpc-demo/trpc-docker-demo | 使用的docker image |

构建参数：

| 变量名 | 默认值 | 说明 |
| --- | --- | --- |
| IMAGE_TAG | latest | 要更新镜像到的目标tag |

注意事项：
- 包含构建参数的pipeline在初次执行之前，Jenkins不会识别其中定义的入参，初次执行之后，就可以在Jenkins界面中看到 `Build with Parameters` 操作了