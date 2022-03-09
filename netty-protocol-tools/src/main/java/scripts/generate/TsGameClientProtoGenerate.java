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
public class TsGameClientProtoGenerate extends ProtoCodeGenerateTemplate {


    @Override
    public String getType() {
        return "ts.game.client";
    }

    @Override
    protected void generateImpl(ProtoModel protoModel) {
        String generatePath = getGeneratePath(protoModel);

        //创建路径
        checkAndCreateDir(generatePath);

        for (CommandModel commandModel : protoModel.getCommands()) {




            Map<String, Object> protosParams = new HashMap<>();

            /**
             * ts代码文件
             */
            String protosTsFilePath;
            //模板名称
            String tmpName;
            //生成代码的文件名
            String className = "";
            //判断是发送代码还是接收代码
            if("c".equals(commandModel.getSrc())) {
                tmpName = getSendProtoTemplateName();
                className = commandModel.getName() + "SendablePacket";
                String luaClassDir = generatePath + "Sends" + PathUtils.getSeparator() + protoModel.getGenerateConfigInfo().getProjectName() +  PathUtils.getSeparator();
                checkAndCreateDir(luaClassDir);
                protosTsFilePath = luaClassDir + PathUtils.getSeparator() + className + ".ts";
                parseSendParams(commandModel.getCommandParams(), commandModel.getInnerParams());

            } else {
                tmpName = getReceiveProtoTemplateName();

                className = commandModel.getName() + "ReceivablePacket";
                String tsClassDir = generatePath + "Receives" + PathUtils.getSeparator() + protoModel.getGenerateConfigInfo().getProjectName() +  PathUtils.getSeparator();
                checkAndCreateDir(tsClassDir);
                protosTsFilePath = tsClassDir + PathUtils.getSeparator() + className + ".ts";
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
            String holdCode = getHoldCode(protosTsFilePath);

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
                VelocityUtils.getInstance().parseTemplate(tmpName, protosTsFilePath, protosParams);
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
            codeTmp = "this.writeString(buff, this.#{name});";
        } else if("Long".equals(typeName)) {
            codeTmp = "this.writeLong(buff, this.#{name});";
        } else if("Integer".equals(typeName)) {
            codeTmp = "this.writeInt(buff, this.#{name});";
        } else if("Boolean".equals(typeName)) {
            codeTmp = "this.writeBoolean(buff, this.#{name});";
        } else if("Float".equals(typeName)) {
            codeTmp = "this.writeFloat(buff, this.#{name});";
        } else if("Double".equals(typeName)) {
            codeTmp = "this.writeDouble(buff, this.#{name});";
        } else if("Charset".equals(typeName)) {
            codeTmp = "this.writeChar(buff, this.#{name});";
        } else if("Byte".equals(typeName)) {
            codeTmp = "this.writeByte(buff, this.#{name});";
        } else if("Short".equals(typeName)) {
            codeTmp = "this.writeShort(buff, this.#{name});";
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
        String result = TAB   + "let #{name}Size : number = #{name}.length;\n";
        result += TAB   + "writeShort(buff, #{name}Size);\n";
        result += TAB  + "for(let #{name}Inner of #{name}) { \n".replace("#{type}", commandParam.getTypeClass());

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
                    result += TAB  + TAB + code + " //" + innerCommandParam.getRemark() + "\n";
                } else {
                    result += parseInnerSendParam(innerCommandParam, innerCommandParam.getChildrens());
                }
            }
        }


        result += TAB  + "}\n";
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
            codeTmp = "this.#{name} = this.readString();";
        } else if("Long".equals(typeName)) {
            codeTmp = "this.#{name} = this.readLong();";
        } else if("Integer".equals(typeName)) {
            codeTmp = "this.#{name} = this.readInt();";
        } else if("Boolean".equals(typeName)) {
            codeTmp = "this.#{name} = this.readBoolean();";
        } else if("Float".equals(typeName)) {
            codeTmp = "this.#{name} = this.readFloat();";
        } else if("Double".equals(typeName)) {
            codeTmp = "this.#{name} = this.readDouble();";
        } else if("Charset".equals(typeName)) {
            codeTmp = "this.#{name} = this.readChar();";
        } else if("Byte".equals(typeName)) {
            codeTmp = "this.#{name} = this.readByte();";
        } else if("Short".equals(typeName)) {
            codeTmp = "this.#{name} = this.readShort();";
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
            codeTmp = "this.#{name} = this.readString()";
        } else if("Long".equals(typeName)) {
            codeTmp = "this.#{name} = this.readLong()";
        } else if("Integer".equals(typeName)) {
            codeTmp = "this.#{name} = this.readInt()";
        } else if("Boolean".equals(typeName)) {
            codeTmp = "this.#{name} = this.readBoolean()";
        } else if("Float".equals(typeName)) {
            codeTmp = "this.#{name} = this.readFloat()";
        } else if("Double".equals(typeName)) {
            codeTmp = "this.#{name} = this.readDouble()";
        } else if("Charset".equals(typeName)) {
            codeTmp = "this.#{name} = this.readChar()";
        } else if("Byte".equals(typeName)) {
            codeTmp = "this.#{name} = this.readByte()";
        } else if("Short".equals(typeName)) {
            codeTmp = "this.#{name} = this.readShort()";
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
        String result = TAB  + TAB + TAB + "let #{name}Size: number = this.readShort();\n";
        result += TAB  + TAB + TAB + "this.#{name} = new Array(#{name}Size );\n";
        result += TAB  + TAB + TAB + "for(let i = 0; i < #{name}Size; i++) {\n";

        if(innerParams.isEmpty()) {
            String innerParamName = commandParam.getName() + "[i]";
            String code = getInnerReceiveCodeTmp(commandParam.getTypeClass()).replace("#{name}", innerParamName);
            result += TAB + TAB + TAB + code + "\n";
        } else {
            result += TAB + TAB + TAB + "this.#{name}[i] = {}\n";
            for (CommandParam innerCommandParam : innerParams) {
                if(StringUtils.isBlank(innerCommandParam.getTypeClass())) {
                    String innerParamName = commandParam.getName() + "[i]." + innerCommandParam.getName();
                    String code = getInnerReceiveCodeTmp(innerCommandParam.getType()).replace("#{name}", innerParamName);
                    result += TAB + TAB + TAB + code + " //" + innerCommandParam.getRemark() + "\n";
                } else {
                    result += parseInnerReceiveParam(innerCommandParam, innerCommandParam.getChildrens());
                }
            }
        }

        result += TAB + TAB + "}\n";
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
        return "ts/ts.game.client.";
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
