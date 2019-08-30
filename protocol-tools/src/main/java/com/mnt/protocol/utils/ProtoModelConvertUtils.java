package com.mnt.protocol.utils;


import com.mnt.protocol.model.*;
import com.mnt.protocol.vo.BaseCommadVO;
import com.mnt.protocol.vo.BaseProtoVO;
import com.mnt.protocol.vo.CommadReqVO;
import com.mnt.protocol.vo.CommadRespVO;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 协议model转换工具
 * @author jiangbiao
 * @date 2018/8/17 17:01
 */
public class ProtoModelConvertUtils {


    /**
     * 协议数据模型转换
     * @param baseProtoVO
     * @param selectedCommads
     * @return
     */
    public static ProtoModel convert(BaseProtoVO baseProtoVO, List<BaseCommadVO> selectedCommads) {
        ProtoModel result = new ProtoModel();

        result.setUser(UserData.getUserConfig().getUser());

        String type = UserData.getUserConfig().getGenerateCodeType();
        if(StringUtils.isEmpty(type)) {
            throw new NullPointerException("请选择你是干啥的");
        }
        //设置配置相关选项
        GenerateConfigInfo generateConfigInfo = getByType(baseProtoVO, type);

        //为空的情况下抛出异常
        if(null == generateConfigInfo) {
            throw new NullPointerException("缺少指定类型[" + type + "] generate 相关配置");
        }

        result.setGenerateConfigInfo(generateConfigInfo);

        List<String> importPackages = new ArrayList<>();
        result.setImportPackages(importPackages);

        //当前控制层所在controller
        String actionPackage = generateConfigInfo.getPackageName();

        //控制层名称
        String name = ProtoVOUtils.getProtoName(baseProtoVO.getXmlObject());
        result.setControllerName(name + "Controller");

        //请求一级路径
        String path = ProtoVOUtils.getProtoPath(baseProtoVO.getXmlObject());
        result.setRequestMapper(path);

        //注释信息
        String remark = ProtoVOUtils.getProtoRemark(baseProtoVO.getXmlObject());
        result.setRemark(remark);

        //创建日期
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        result.setDate(date);

        //设置详情命令参数
        List<ActionModel> actions = convertAction(name, actionPackage, importPackages, selectedCommads);
        result.setActions(actions);
        return result;
    }

    /**
     * 根据类型获取生成配置
     * @param type
     * @return
     */
    private static GenerateConfigInfo getByType(BaseProtoVO baseProtoVO, String type) {
        Map<String, GenerateConfigInfo> generateConfigInfos = ProtoVOUtils.getGenerateConfigs(baseProtoVO.getXmlObject());
        if(generateConfigInfos.containsKey(type)) {
           return generateConfigInfos.get(type);
        }

        return null;
    }

    /**
     * 转换每一个请求
     * @param importPackages
     * @param baseCommadVOs
     * @return
     */
    private static List<ActionModel> convertAction(String actionName, String actionPackage, List<String> importPackages, List<BaseCommadVO> baseCommadVOs) {
        List<ActionModel> result = new ArrayList<>(baseCommadVOs.size());
        ActionModel actionModel;
        for (BaseCommadVO baseCommadVO : baseCommadVOs) {
            actionModel = new ActionModel();
            //首字母大写请求路径
            String upperName = NameUtils.upperFristStr(baseCommadVO.getPath());
            actionModel.setActionName(baseCommadVO.getPath());
            actionModel.setRequestMapper(baseCommadVO.getPath());
            actionModel.setRemark(baseCommadVO.getRemark());
            actionModel.setMethod(actionModel.getMethod());
            actionModel.setBody(baseCommadVO.isBody());
            //请求参数类
            String requestParamClass = NameUtils.upperFristStr(actionName) + upperName + "RequestParam";
            actionModel.setReqClass(requestParamClass);
            //请求参数名
            String requestParamName = NameUtils.lowerFristStr(actionName) + upperName + "RequestParam";
            actionModel.setReqName(requestParamName);

            //答复参数类
            String responseParamClass = NameUtils.upperFristStr(actionName) + upperName + "ResponseParam";
            actionModel.setRespClass(responseParamClass);

            //答复参数名
            String responseParamName = NameUtils.lowerFristStr(actionName) + upperName + "ResponseParam";
            actionModel.setRespName(responseParamName);

            //参数所在包
            String reqParamPackage = actionPackage + ".param.req";
            String respParamPackage = actionPackage + ".param.resp";

            //引入class
            importPackages.add(reqParamPackage + "." + requestParamClass);
            importPackages.add(respParamPackage + "." + responseParamClass);

            actionModel.setReqPackage(reqParamPackage);
            actionModel.setRespPackage(respParamPackage);

            List<String> actionReqImportsClass = new ArrayList<>();
            actionModel.setReqImprotClass(actionReqImportsClass);

            Map<CommadReqParam, List<CommadReqParam>> innerReqParams = new HashMap<>();
            actionModel.setInnerReqParams(innerReqParams);
            //请求参数设置
            List<CommadReqVO> commadReqVOs = ProtoVOUtils.parseCommadReqVOsToCommadVO(baseCommadVO);
            List<CommadReqParam> commadReqParams = convertToReqParam(commadReqVOs, innerReqParams, actionReqImportsClass, baseCommadVO.isBody());
            actionModel.setCommadReqParams(commadReqParams);

            //返回参数导入的包
            List<String> actionRespImportsClass = new ArrayList<>();
            actionModel.setRespImprotClass(actionRespImportsClass);

            Map<CommadRespParam, List<CommadRespParam>> innerRespParams = new HashMap<>();
            actionModel.setInnerRespParams(innerRespParams);
            //答复参数设置
            List<CommadRespVO> commadRespVOs = ProtoVOUtils.parseCommadRespVOsToCommadVO(baseCommadVO);
            List<CommadRespParam> commadRespParams = convertToRespParam(commadRespVOs, innerRespParams, actionRespImportsClass);
            actionModel.setCommadRespParams(commadRespParams);


            result.add(actionModel);
        }

        return result;
    }


    /**
     * 转换为请求参数列表
     * @param commadReqVOs
     * @return
     */
    private static List<CommadReqParam>  convertToReqParam(List<CommadReqVO> commadReqVOs,  Map<CommadReqParam, List<CommadReqParam>> innerReqParams, List<String> actionReqImportsClass, boolean isBody) {

        List<CommadReqParam> result = new ArrayList<>();

        CommadReqParam commadReqParam;
        //设置请求参数
        for (CommadReqVO commadReqVO : commadReqVOs) {
            commadReqParam = new CommadReqParam();
            commadReqParam.setLength(commadReqVO.getLength());
            commadReqParam.setMust(commadReqVO.isMust());
            commadReqParam.setName(commadReqVO.getName());
            commadReqParam.setRemark(commadReqVO.getRemark());
            commadReqParam.setType(commadReqVO.getTest());

            String type = ParamTypeUtils.convertType(commadReqVO.getType());

            String typeClass = ParamTypeUtils.convertType(commadReqVO.getTypeClass());

            DefaultLoadClassEnums defaultLoadClassEnums =  DefaultLoadClassEnums.getByName(type);
            if(null != defaultLoadClassEnums) {

                checkAndAdd(actionReqImportsClass, defaultLoadClassEnums.getImportClass());
                //集合泛型处理
                if(defaultLoadClassEnums == DefaultLoadClassEnums.LIST) {

                    if(null == typeClass) {
                        type = type + "<" + NameUtils.buildInnerClassName(commadReqVO.getName()) + ">";
                    } else {
                        type = type + "<" + typeClass + ">";
                    }


                } else if(defaultLoadClassEnums == DefaultLoadClassEnums.DATE) {
                    checkAndAdd(actionReqImportsClass, "org.springframework.format.annotation.DateTimeFormat");
                    if(isBody) {
                        checkAndAdd(actionReqImportsClass, "com.fasterxml.jackson.annotation.JsonFormat");
                    }
                }
            }

            DefaultLoadClassEnums typeClassLoadClassEnums =  DefaultLoadClassEnums.getByName(typeClass);

            if(null != typeClassLoadClassEnums) {

                checkAndAdd(actionReqImportsClass, typeClassLoadClassEnums.getImportClass());
            }

            commadReqParam.setType(type);
            commadReqParam.setTypeClass(commadReqVO.getTypeClass());
            commadReqParam.setMethodName(NameUtils.upperFristStr(commadReqVO.getName()));
            commadReqParam.setFormat(commadReqVO.getFormat());

            //递归获取子集
            if(!commadReqVO.getChildrens().isEmpty()) {
                List<CommadReqParam> commadReqParams = convertToReqParam(commadReqVO.getChildrens(), innerReqParams, actionReqImportsClass, isBody);
//                String innerClassName = NameUtils.upperFristStr(commadReqVO.getName());
                innerReqParams.put(commadReqParam, commadReqParams);
                commadReqParam.getChildrens().addAll(commadReqParams);
            }

            result.add(commadReqParam);
        }

        return result;
    }


    /**
     * 转换为答复参数列表
     * @param commadRespVOs
     * @return
     */
    private static List<CommadRespParam>  convertToRespParam(List<CommadRespVO> commadRespVOs, Map<CommadRespParam, List<CommadRespParam>> innerRespParams, List<String> actionRespImportsClass) {

        List<CommadRespParam> result = new ArrayList<>();

        CommadRespParam commadRespParam;
        //设置请求参数
        for (CommadRespVO commadRespVO : commadRespVOs) {
            commadRespParam = new CommadRespParam();
            commadRespParam.setName(commadRespVO.getName());
            commadRespParam.setRemark(commadRespVO.getRemark());

            String type = ParamTypeUtils.convertType(commadRespVO.getType());

            String typeClass = ParamTypeUtils.convertType(commadRespVO.getTypeClass());

            DefaultLoadClassEnums defaultLoadClassEnums =  DefaultLoadClassEnums.getByName(type);
            if(null != defaultLoadClassEnums) {

                checkAndAdd(actionRespImportsClass, defaultLoadClassEnums.getImportClass());

                //集合泛型处理
                if(defaultLoadClassEnums == DefaultLoadClassEnums.LIST) {

                    if(null == typeClass) {
                        type = type + "<" + NameUtils.buildInnerClassName(commadRespVO.getName()) + ">";
                    } else {
                        type = type + "<" + typeClass + ">";
                    }
                } else if(defaultLoadClassEnums == DefaultLoadClassEnums.DATE) {
                    checkAndAdd(actionRespImportsClass, "com.fasterxml.jackson.annotation.JsonFormat");
                }

            }

            //泛型类是否需要加载
            DefaultLoadClassEnums typeClassLoadClassEnums =  DefaultLoadClassEnums.getByName(typeClass);

            if(null != typeClassLoadClassEnums) {

                checkAndAdd(actionRespImportsClass, typeClassLoadClassEnums.getImportClass());
            }

            commadRespParam.setType(type);
            commadRespParam.setTypeClass(commadRespVO.getTypeClass());
            commadRespParam.setMethodName(NameUtils.upperFristStr(commadRespVO.getName()));
            commadRespParam.setFormat(commadRespVO.getFormat());

            //递归获取子集
            if(!commadRespVO.getChildrens().isEmpty()) {

                List<CommadRespParam> commadRespParams = convertToRespParam(commadRespVO.getChildrens(), innerRespParams, actionRespImportsClass);
//                String innerClassName = NameUtils.upperFristStr(commadRespVO.getName());
                //不生成data子类
                if(!"data".equals(commadRespVO.getName())) {
                    innerRespParams.put(commadRespParam, commadRespParams);
                }

                commadRespParam.getChildrens().addAll(commadRespParams);
            }

            result.add(commadRespParam);
        }

        return result;
    }


    /**
     * 判断和添加类名 不允许重复
     * @param classList
     * @param classPackage
     */
    private static void checkAndAdd(List<String> classList, String classPackage) {

        if(!classList.contains(classPackage)) {
            classList.add(classPackage);
        }

    }


}
