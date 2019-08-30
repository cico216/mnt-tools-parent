mnt-tools 项目介绍
    mnt-tools 是一款代码生成（开发辅助）工具合集。
    
    
    
项目结构
mnt-tools-parent                    工具父项目
--------mybatis-generate-tools      mybatis代码生成工具类(目前集成了postgres，mysql代码生成)
--------protocol-tools              通讯协议工具类(目前支持java服务端,vue uniapp客户端代码生成)
--------tools-aggregation           工具聚合类(目前未实现,目的是为了整合我所有开发的工具)         
--------tools-common                工具的公共类
--------tools-dependent             代码生成时用到的一些代码封装(公共类，继承类)