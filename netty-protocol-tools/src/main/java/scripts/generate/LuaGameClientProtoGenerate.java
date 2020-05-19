package scripts.generate;

import com.mnt.common.utils.VelocityUtils;
import com.mnt.protocol.core.ProtoCodeGenerateTemplate;
import com.mnt.protocol.model.CommandModel;
import com.mnt.protocol.model.CommandParam;
import com.mnt.protocol.model.ProtoModel;
import com.mnt.protocol.model.UserData;
import com.mnt.protocol.utils.PathUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * vue uniapp 协议代码生成
 */
public class LuaGameClientProtoGenerate extends ProtoCodeGenerateTemplate {


    @Override
    public String getType() {
        return "lua.game.client";
    }

    @Override
    protected void generateImpl(ProtoModel protoModel) {
        String generatePath = getGeneratePath(protoModel);

        //创建路径
        checkAndCreateDir(generatePath);

        for (CommandModel commandModel : protoModel.getCommands()) {




            Map<String, Object> protosParams = new HashMap<>();

            /**
             * lua代码文件
             */
            String protosLuaFilePath;
            //模板名称
            String tmpName;
            //生成代码的文件名
            String className = "";
            //判断是发送代码还是接收代码
            if("c".equals(commandModel.getSrc())) {
                tmpName = getSendProtoTemplateName();
                className = commandModel.getName() + "SendablePacket";
                String luaClassDir = generatePath + "Sendpacks" + PathUtils.getSeparator() + protoModel.getGenerateConfigInfo().getProjectName() +  PathUtils.getSeparator();
                checkAndCreateDir(luaClassDir);
                protosLuaFilePath = luaClassDir + PathUtils.getSeparator() + className + ".lua";
                parseSendParams(commandModel.getCommandParams(), commandModel.getInnerParams());

            } else {
                tmpName = getReceiveProtoTemplateName();

                className = commandModel.getName() + "ReceivablePacket";
                String luaClassDir = generatePath + "Receivpacks" + PathUtils.getSeparator() + protoModel.getGenerateConfigInfo().getProjectName() +  PathUtils.getSeparator();
                checkAndCreateDir(luaClassDir);
                protosLuaFilePath = luaClassDir + PathUtils.getSeparator() + className + ".lua";
                parseReceiveParams(commandModel.getCommandParams(), commandModel.getInnerParams());

            }

            String sendDecrParams = "";
            boolean start = true;
            for (CommandParam commandParam : commandModel.getCommandParams()) {
                if(start) {
                    start = false;
                    sendDecrParams +=  commandParam.getName();

                } else {
                    sendDecrParams += ", " + commandParam.getName();
                }

            }
            //获取保留代码
            String holdCode = getHoldCode(protosLuaFilePath);

            //发送的参数声明
            protosParams.put("sendDecrParams", sendDecrParams);

            protosParams.put("user", protoModel.getUser());
            protosParams.put("date", protoModel.getDate());

            protosParams.put("remark", commandModel.getRemark());
            protosParams.put("opCode", commandModel.getOpCode());

            protosParams.put("params", commandModel.getCommandParams());
            protosParams.put("className", className);
            protosParams.put("holdCode", holdCode);

            try{
                VelocityUtils.getInstance().parseTemplate(tmpName, protosLuaFilePath, protosParams);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 获取发送的代码
     * @param typeName
     * @return
     */
    private String getSendCodeTmp(String typeName) {
        String codeTmp = "";
        if("String".equals(typeName)) {
            codeTmp = "buf:WriteString(#{name})";
        } else if("Long".equals(typeName)) {
            codeTmp = "buf:WriteLong(#{name})";
        } else if("Integer".equals(typeName)) {
            codeTmp = "buf:WriteInt(#{name})";
        } else if("Boolean".equals(typeName)) {
            codeTmp = "buf:WriteBoolean(#{name})";
        } else if("Float".equals(typeName)) {
            codeTmp = "buf:WriteBoolean(#{name})";
        } else if("Double".equals(typeName)) {
            codeTmp = "buf:WriteDouble(#{name})";
        } else if("Charset".equals(typeName)) {
            codeTmp = "buf:WriteChar(#{name})";
        } else if("Byte".equals(typeName)) {
            codeTmp = "buf:WriteByte(#{name})";
        } else if("Short".equals(typeName)) {
            codeTmp = "buf:WriteShort(#{name})";
        }

        return codeTmp;
    }



    /**
     *  解析发送参数代码
     * @param commandParams
     * @param innerParamsMap
     */
    private void parseSendParams(List<CommandParam> commandParams, Map<CommandParam, List<CommandParam>> innerParamsMap) {
        for (CommandParam commandParam : commandParams) {
            if(StringUtils.isBlank(commandParam.getTypeClass())) {
                String code = TAB  + getSendCodeTmp(commandParam.getType()).replace("#{name}", commandParam.getName());
                commandParam.setCode(code);
            } else {
                commandParam.setCode(parseInnerSendParam(commandParam, commandParam.getChildrens()));
            }
        }
    }


    /**
     * 解析内部list参数发送
     * @param commandParam
     * @param innerParams
     * @return
     */
    private String parseInnerSendParam(CommandParam commandParam, List<CommandParam> innerParams) {
        String result = TAB  + "local #{name}Size = ##{name}\n";
        result += TAB  + "buf:WriteShort(#{name}Size)\n";
        result += TAB  + "for index, #{name}Inner in pairs(#{name}) do\n";

        //为基础类型时
        if(innerParams.isEmpty()) {
            String innerParamName = commandParam.getName() + "Inner";
            String code = getSendCodeTmp(commandParam.getTypeClass()).replace("#{name}", innerParamName);
            result += TAB  + TAB + code + "\n";
        } else {
            for (CommandParam innerCommandParam : innerParams) {
                if(StringUtils.isBlank(innerCommandParam.getTypeClass())) {
                    String innerParamName = commandParam.getName() + "Inner." + innerCommandParam.getName();
                    String code = getSendCodeTmp(innerCommandParam.getType()).replace("#{name}", innerParamName);
                    result += TAB  + TAB + code + " --" + innerCommandParam.getRemark() + "\n";
                } else {
                    result += parseInnerSendParam(innerCommandParam, innerCommandParam.getChildrens());
                }
            }
        }


        result += TAB  + "end\n";
        return result.replace("#{name}", commandParam.getName());
    }


    /**
     * 获取接收的代码
     * @param typeName
     * @return
     */
    private String getReceiveCodeTmp(String typeName) {
        String codeTmp = "";
        if("String".equals(typeName)) {
            codeTmp = "local #{name} = buf:ReadString()";
        } else if("Long".equals(typeName)) {
            codeTmp = "local #{name} = buf:ReadLong()";
        } else if("Integer".equals(typeName)) {
            codeTmp = "local #{name} = buf:ReadInt()";
        } else if("Boolean".equals(typeName)) {
            codeTmp = "local #{name} = buf:ReadBoolean()";
        } else if("Float".equals(typeName)) {
            codeTmp = "local #{name} = buf:ReadFloat()";
        } else if("Double".equals(typeName)) {
            codeTmp = "local #{name} = buf:ReadDouble()";
        } else if("Charset".equals(typeName)) {
            codeTmp = "local #{name} = buf:ReadChar()";
        } else if("Byte".equals(typeName)) {
            codeTmp = "local #{name} = buf:ReadByte()";
        } else if("Short".equals(typeName)) {
            codeTmp = "local #{name} = buf:ReadShort()";
        }

        return codeTmp;
    }

    /**
     *  解析发送参数代码
     * @param commandParams
     * @param innerParams
     */
    private void parseReceiveParams(List<CommandParam> commandParams, Map<CommandParam, List<CommandParam>> innerParams) {
        for (CommandParam commandParam : commandParams) {
            if(commandParam.getChildrens().isEmpty() && StringUtils.isBlank(commandParam.getTypeClass())) {
                String code = getReceiveCodeTmp(commandParam.getType()).replace("#{name}", commandParam.getName());
                commandParam.setCode(code);
            } else {
                commandParam.setCode(parseInnerReceiveParam(commandParam, commandParam.getChildrens()));
            }
        }
    }


    /**
     * 获取内部接收的代码
     * @param typeName
     * @return
     */
    private String getInnerReceiveCodeTmp(String typeName) {
        String codeTmp = "";
        if("String".equals(typeName)) {
            codeTmp = "#{name} = buf:ReadString()";
        } else if("Long".equals(typeName)) {
            codeTmp = "#{name} = buf:ReadLong()";
        } else if("Integer".equals(typeName)) {
            codeTmp = "#{name} = buf:ReadInt()";
        } else if("Boolean".equals(typeName)) {
            codeTmp = "#{name} = buf:ReadBoolean()";
        } else if("Float".equals(typeName)) {
            codeTmp = "#{name} = buf:ReadFloat()";
        } else if("Double".equals(typeName)) {
            codeTmp = "#{name} = buf:ReadDouble()";
        } else if("Charset".equals(typeName)) {
            codeTmp = "#{name} = buf:ReadChar()";
        } else if("Byte".equals(typeName)) {
            codeTmp = "#{name} = buf:ReadByte()";
        } else if("Short".equals(typeName)) {
            codeTmp = "#{name} = buf:ReadShort()";
        }

        return codeTmp;
    }
    /**
     * 解析内部list参数发送
     * @param commandParam
     * @param innerParams
     * @return
     */
    private String parseInnerReceiveParam(CommandParam commandParam, List<CommandParam> innerParams) {
        String result = "local  #{name}Size = buf:ReadShort()\n";
        result += TAB + TAB + "local #{name} = {}\n";
        result += TAB + TAB + "for i = 1, #{name}Size do\n";

        if(innerParams.isEmpty()) {
            String innerParamName = commandParam.getName() + "[i]";
            String code = getInnerReceiveCodeTmp(commandParam.getTypeClass()).replace("#{name}", innerParamName);
            result += TAB + TAB + TAB + code + "\n";
        } else {
            result += TAB + TAB + TAB + "#{name}[i] = {}\n";
            for (CommandParam innerCommandParam : innerParams) {
                if(StringUtils.isBlank(innerCommandParam.getTypeClass())) {
                    String innerParamName = commandParam.getName() + "[i]." + innerCommandParam.getName();
                    String code = getInnerReceiveCodeTmp(innerCommandParam.getType()).replace("#{name}", innerParamName);
                    result += TAB + TAB + TAB + code + " --" + innerCommandParam.getRemark() + "\n";
                } else {
                    result += parseInnerReceiveParam(innerCommandParam, innerCommandParam.getChildrens());
                }
            }
        }

        result += TAB + TAB + "end\n";
        return result.replace("#{name}", commandParam.getName());
    }



    @Override
    public String getGeneratePath(ProtoModel protoModel) {
        return UserData.getUserConfig().getProjectPath() + PathUtils.getSeparator() +
                protoModel.getGenerateConfigInfo().getPackageName() + PathUtils.getSeparator();
    }


    /**
     * 基础模板路径
     * @return 基础模板前缀
     */
    private String baseTmpPath() {
        return "lua/lua.game.client.";
    }

    /**
     * 获取发送协议模板路径
     * @return 模板文件名
     */
    private String getSendProtoTemplateName() {
        return baseTmpPath() + "send.proto.vm";
    }

    /**
     * 获取接收协议模板路径
     * @return 模板文件名
     */
    private String getReceiveProtoTemplateName() {
        return baseTmpPath() + "receive.proto.vm";
    }
}
