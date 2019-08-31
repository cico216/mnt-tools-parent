mnt-tools 项目介绍
    mnt-tools 是一款代码生成（开发辅助）工具合集（目前主要支持java语言,后期考虑加入其它语言的支持）。我的初衷
是在达到编码工作的效果的情况下尽可能的降低工作量，根据平时积累的开发经验抽象出重复性的编码工作，可以一键生
成大量机械化的代码，让开发人员更专注复杂需要人为干预的功能开发。
    已经实现的功能介绍:
        1.通过数据库表结构逆向生成mybatis dao, service层, entity相关代码。
        2.通过我设计的协议文件生成java action层代码,vue uniapp http请求网络协议代码。
        
    
    
项目结构
mnt-tools-parent                    工具父项目
--------mybatis-generate-tools      mybatis代码生成工具类(目前集成了postgres，mysql代码生成)
--------protocol-tools              通讯协议工具类(目前支持java服务端,vue uniapp客户端代码生成)
--------tools-aggregation           工具聚合类(目前未实现,目的是为了整合我所有开发的工具)         
--------tools-common                工具的公共类
--------tools-dependent             代码生成时用到的一些代码封装(公共类，继承类)


使用说明