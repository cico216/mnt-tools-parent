package com.mnt.protocol.utils;


import com.mnt.protocol.model.*;
import com.mnt.protocol.vo.BaseCommandVO;
import com.mnt.protocol.vo.BaseProtoVO;
import com.mnt.protocol.vo.CommandParamVO;
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
    public static ProtoModel convert(BaseProtoVO baseProtoVO, List<BaseCommandVO> selectedCommads) {
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

        result.setModuleName(baseProtoVO.getModuleName());

        //注释信息
        result.setRemark(baseProtoVO.getRemark());

        //创建日期
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        result.setDate(date);

        //设置详情命令参数
        List<CommandModel> commands = convertCommand(selectedCommads);
        result.setCommands(commands);
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
     * 转换每一个命令
     * @param baseCommandVOs 命令集合
     * @return
     */
    private static List<CommandModel> convertCommand(List<BaseCommandVO> baseCommandVOs) {
        List<CommandModel> result = new ArrayList<>(baseCommandVOs.size());
        CommandModel commandModel;
        for (BaseCommandVO baseCommadVO : baseCommandVOs) {
            commandModel = new CommandModel();
            //首字母大写请求路径
//            String upperName = NameUtils.upperFristStr(baseCommadVO.getPath());
            commandModel.setName(baseCommadVO.getName());
            commandModel.setOpCode(baseCommadVO.getOpCode());
            commandModel.setRemark(baseCommadVO.getRemark());
            commandModel.setSrc(baseCommadVO.getSrc());


            //引入class
            List<String> commandImportClass = new ArrayList<>();
            commandModel.setCommandImportClass(commandImportClass);

            Map<CommandParam, List<CommandParam>> innerReqParams = new HashMap<>();
            commandModel.setInnerParams(innerReqParams);


            //请求参数设置
            List<CommandParamVO> commandVOs = ProtoVOUtils.parseCommandParamVOsToCommadVO(baseCommadVO);
            List<CommandParam> commandParams = convertToReqParam(commandVOs, innerReqParams, commandImportClass);
            commandModel.setCommandParams(commandParams);


            result.add(commandModel);
        }

        return result;
    }


    /**
     * 转换为请求参数列表
     * @param commadReqVOs
     * @return
     */
    private static List<CommandParam>  convertToReqParam(List<CommandParamVO> commadReqVOs,  Map<CommandParam, List<CommandParam>> innerReqParams, List<String> commandImportClass) {

        List<CommandParam> result = new ArrayList<>();

        CommandParam commandParam;
        //设置请求参数
        for (CommandParamVO commadReqVO : commadReqVOs) {
            commandParam = new CommandParam();
            commandParam.setName(commadReqVO.getName());
            commandParam.setRemark(commadReqVO.getRemark());
            commandParam.setType(commadReqVO.getType());
            commandParam.setTypeClass(commadReqVO.getTypeClass());




            String type = ParamTypeUtils.convertType(commadReqVO.getType());

            String typeClass = ParamTypeUtils.convertType(commadReqVO.getTypeClass());

            DefaultLoadClassEnums defaultLoadClassEnums =  DefaultLoadClassEnums.getByName(type);
            if(null != defaultLoadClassEnums) {

                checkAndAdd(commandImportClass, defaultLoadClassEnums.getImportClass());
                //集合泛型处理
                if(defaultLoadClassEnums == DefaultLoadClassEnums.LIST) {

                    if(null == typeClass) {
                        type = type + "<" + NameUtils.buildInnerClassName(commadReqVO.getName()) + ">";
                    } else {
                        type = type + "<" + typeClass + ">";
                    }


                } else if(defaultLoadClassEnums == DefaultLoadClassEnums.DATE) {
                    checkAndAdd(commandImportClass, "org.springframework.format.annotation.DateTimeFormat");
                }
            }

            DefaultLoadClassEnums typeClassLoadClassEnums =  DefaultLoadClassEnums.getByName(typeClass);

            if(null != typeClassLoadClassEnums) {

                checkAndAdd(commandImportClass, typeClassLoadClassEnums.getImportClass());
            }

            commandParam.setType(type);
            commandParam.setTypeClass(commadReqVO.getTypeClass());

            //递归获取子集
            if(!commadReqVO.getChildrens().isEmpty()) {
                List<CommandParam> commandParams = convertToReqParam(commadReqVO.getChildrens(), innerReqParams, commandImportClass);
//                String innerClassName = NameUtils.upperFristStr(commadReqVO.getName());
                innerReqParams.put(commandParam, commandParams);
                commandParam.getChildrens().addAll(commandParams);
            }

            result.add(commandParam);
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
