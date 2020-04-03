package scripts.generate;

import com.mnt.common.utils.VelocityUtils;
import com.mnt.protocol.core.ProtoCodeGenerateTemplate;
import com.mnt.protocol.model.CommandModel;
import com.mnt.protocol.model.CommandParam;
import com.mnt.protocol.model.ProtoModel;
import com.mnt.protocol.model.UserData;
import com.mnt.protocol.utils.PathUtils;

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

            String protosLuaFilePath =  generatePath + PathUtils.getSeparator() + commandModel.getName() + ".lua";

            //获取保留代码
            String holdCode = getHoldCode(protosLuaFilePath);

            Map<String, Object> protosParams = new HashMap<>();



            //模板名称
            String tmpName;
            //生成代码的文件名
            String className = "";
            //判断是发送代码还是接收代码
            if("s".equals(commandModel.getSrc())) {
                tmpName = getSendProtoTemplateName();
                className = commandModel.getName() + "SendablePacket";
                String luaClassDir = generatePath + "Sendpacks" + PathUtils.getSeparator() + protoModel.getModuleName() +  PathUtils.getSeparator();
                checkAndCreateDir(luaClassDir);

                parseSendParams(commandModel.getCommandParams(), commandModel.getInnerParams());

            } else {
                tmpName = getReceiveProtoTemplateName();

                className = commandModel.getName() + "ReceivablePacket";
                String luaClassDir = generatePath + "Receivpacks" + PathUtils.getSeparator() + protoModel.getModuleName() +  PathUtils.getSeparator();
                checkAndCreateDir(luaClassDir);

                parseReceiveParams(commandModel.getCommandParams(), commandModel.getInnerParams());

            }

            String sendDecrParams = "";
            for (CommandParam commandParam : commandModel.getCommandParams()) {
                sendDecrParams += ", " + commandParam.getName();
            }
            //发送的参数声明
            protosParams.put("sendDecrParams", sendDecrParams);

            protosParams.put("user", protoModel.getUser());
            protosParams.put("date", protoModel.getDate());

            protosParams.put("remark", commandModel.getRemark());
            protosParams.put("opCode", commandModel.getOpCode());

            protosParams.put("params", commandModel.getCommandParams());
            protosParams.put("name", className);
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
     * @param commandParam
     * @return
     */
    private String getSendCodeTmp(CommandParam commandParam) {
        String typeName = commandParam.getType();
        String codeTmp = "";
        if("String".equals(typeName)) {
            codeTmp = "buf:writeString(#{name})";
        } else if("Long".equals(typeName)) {
            codeTmp = "buf:writeLong(#{name})";
        } else if("Integer".equals(typeName)) {
            codeTmp = "buf:writeInt( #{name})";
        } else if("Boolean".equals(typeName)) {
            codeTmp = "buf:writeBoolean(#{name})";
        } else if("Float".equals(typeName)) {
            codeTmp = "buf:writeBoolean(#{name})";
        } else if("Double".equals(typeName)) {
            codeTmp = "buf:writeDouble(#{name})";
        } else if("Charset".equals(typeName)) {
            codeTmp = "buf:writeChar(#{name})";
        } else if("Byte".equals(typeName)) {
            codeTmp = "buf:writeByte( #{name})";
        } else if("Short".equals(typeName)) {
            codeTmp = "buf:writeShort( #{name})";
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
            if(commandParam.getChildrens().isEmpty()) {
                String code = getSendCodeTmp(commandParam).replace("#{name}", commandParam.getName());
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
        String result = "buf:writeInt(#{name})\n";
        result += "for(Name name : #{name}) { \n";
        for (CommandParam innerCommandParam : innerParams) {
            if(innerCommandParam.getChildrens().isEmpty()) {
                result += parseInnerSendParam(innerCommandParam, innerCommandParam.getChildrens());
            } else {
                String innerParamName = "";
                String code = getSendCodeTmp(commandParam).replace("#{name}", innerParamName);
                result += code;
            }
        }
        result += "}\n";
        return result;
    }


    /**
     * 获取接收的代码
     * @param commandParam
     * @return
     */
    private String getReceiveCodeTmp(CommandParam commandParam) {
        String typeName = commandParam.getType();
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
            if(commandParam.getChildrens().isEmpty()) {
                String code = getReceiveCodeTmp(commandParam).replace("#{name}", commandParam.getName());
                commandParam.setCode(code);
            } else {
                commandParam.setCode(parseInnerReceiveParam(commandParam, commandParam.getChildrens()));
            }
        }
    }


    /**
     * 获取内部接收的代码
     * @param commandParam
     * @return
     */
    private String getInnerReceiveCodeTmp(CommandParam commandParam) {
        String typeName = commandParam.getType();
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
        String result = "local  #{name}Size = readInt()\n";
        result += "#{name} = {}\n";
        result += "for(int i = 0; i++; i < #{name}Size) {\n";
        for (CommandParam innerCommandParam : innerParams) {
            if(innerCommandParam.getChildrens().isEmpty()) {
                result += parseInnerReceiveParam(innerCommandParam, innerCommandParam.getChildrens());
            } else {
                String innerParamName = "";
                String code = getInnerReceiveCodeTmp(commandParam).replace("#{name}", innerParamName);
                result += code;
            }
        }
        result += "}";
        return result;
    }



    @Override
    public String getGeneratePath(ProtoModel protoModel) {
        return UserData.getUserConfig().getProjectPath() + PathUtils.getSeparator() +
                protoModel.getGenerateConfigInfo().getProjectName() + PathUtils.getSeparator() +
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
