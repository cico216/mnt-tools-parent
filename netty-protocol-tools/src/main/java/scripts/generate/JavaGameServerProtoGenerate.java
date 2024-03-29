package scripts.generate;

import com.mnt.common.utils.VelocityUtils;
import com.mnt.protocol.core.ProtoCodeGenerateTemplate;
import com.mnt.protocol.model.CommandModel;
import com.mnt.protocol.model.CommandParam;
import com.mnt.protocol.model.ProtoModel;
import com.mnt.protocol.model.UserData;
import com.mnt.protocol.utils.NameUtils;
import com.mnt.protocol.utils.PathUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * vue uniapp 协议代码生成
 */
public class JavaGameServerProtoGenerate extends ProtoCodeGenerateTemplate {


    @Override
    public String getType() {
        return "java.game.server";
    }

    @Override
    protected void generateImpl(ProtoModel protoModel) {
        String generatePath = getGeneratePath(protoModel);

        generatePath = generatePath + PathUtils.packageToPath(protoModel.getGenerateConfigInfo().getPackageName()) +  PathUtils.getSeparator();

        //创建路径
        checkAndCreateDir(generatePath);


        for (CommandModel commandModel : protoModel.getCommands()) {
            //模板引擎替换的参数
            Map<String, Object> protosParams = new HashMap<>();

            String protosJavaFilePath;
            //模板名称
            String tmpName;
            //生成代码的文件名
            String className;
            //生成类的包名
            String packagePath;

            //判断是发送代码还是接收代码
            if("s".equals(commandModel.getSrc())) {
                packagePath = protoModel.getGenerateConfigInfo().getPackageName() + ".packets.sendpacks." + protoModel.getModuleName();
                className = commandModel.getName() + "SendablePacket";
                String javaClassDir = generatePath + "packets" + PathUtils.getSeparator() + "sendpacks" + PathUtils.getSeparator() + protoModel.getModuleName() +  PathUtils.getSeparator();
                checkAndCreateDir(javaClassDir);

                protosJavaFilePath =  javaClassDir + className + ".java";
                tmpName = getSendProtoTemplateName();
                parseSendParams(commandModel.getCommandParams(), commandModel.getInnerParams());

                String sendDecrParams = "";
                for (CommandParam commandParam : commandModel.getCommandParams()) {
                    sendDecrParams += ", " + commandParam.getUnboxType() + " " + commandParam.getName();
                }
                //发送的参数声明
                protosParams.put("sendDecrParams", sendDecrParams);

            } else {
                packagePath = protoModel.getGenerateConfigInfo().getPackageName() + ".packets.receivpacks." + protoModel.getModuleName();
                className = commandModel.getName() + "ReceivablePacket";
                String javaClassDir = generatePath + "packets" + PathUtils.getSeparator() + "receivpacks" + PathUtils.getSeparator() + protoModel.getModuleName() +  PathUtils.getSeparator();
                checkAndCreateDir(javaClassDir);
                protosJavaFilePath =  javaClassDir + className + ".java";

                tmpName = getReceiveProtoTemplateName();
                parseReceiveParams(commandModel.getCommandParams(), commandModel.getInnerParams());


            }
            //公共模块时
            if(protoModel.getGenerateConfigInfo().getProjectName().contains("game-server/game-common")) {
                //添加连接包
                commandModel.getCommandImportClass().add(protoModel.getGenerateConfigInfo().getPackageName() + ".entitys.BaseGameClientConnection");

                protosParams.put("connection", "BaseGameClientConnection");
            } else {
                //添加连接包
                commandModel.getCommandImportClass().add(protoModel.getGenerateConfigInfo().getPackageName() + ".entitys.GameClientConnection");

                protosParams.put("connection", "GameClientConnection");
            }



            //获取保留代码
            String holdCode = getHoldCode(protosJavaFilePath);
            getImportClass(protosJavaFilePath, commandModel.getCommandImportClass());

            protosParams.put("user", protoModel.getUser());
            protosParams.put("date", protoModel.getDate());

            protosParams.put("packagePath", packagePath);
            protosParams.put("remark", commandModel.getRemark());
            protosParams.put("opCode", commandModel.getOpCode());

            protosParams.put("params", commandModel.getCommandParams());
            protosParams.put("className", className);
            protosParams.put("importPackages", commandModel.getCommandImportClass());
            protosParams.put("holdCode", holdCode);

            try{
                VelocityUtils.getInstance().parseTemplate(tmpName, protosJavaFilePath, protosParams);
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
            codeTmp = "writeString(buffer, #{name});";
        } else if("Long".equals(typeName)) {
            codeTmp = "writeLong(buffer, #{name});";
        } else if("Integer".equals(typeName)) {
            codeTmp = "writeInt(buffer, #{name});";
        } else if("Boolean".equals(typeName)) {
            codeTmp = "writeBoolean(buffer, #{name});";
        } else if("Float".equals(typeName)) {
            codeTmp = "writeFloat(buffer, #{name});";
        } else if("Double".equals(typeName)) {
            codeTmp = "writeDouble(buffer, #{name});";
        } else if("Charset".equals(typeName)) {
            codeTmp = "writeChar(buffer, #{name});";
        } else if("Byte".equals(typeName)) {
            codeTmp = "writeByte(buffer, #{name});";
        } else if("Short".equals(typeName)) {
            codeTmp = "writeShort(buffer, #{name});";
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
                String code = TAB + TAB + TAB + getSendCodeTmp(commandParam.getType()).replace("#{name}", commandParam.getName());
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
        String result = TAB + TAB + TAB + "int #{name}Size = #{name}.size();\n";
        result += TAB + TAB + TAB + "writeShort(buffer, #{name}Size);\n";
        result += TAB + TAB + TAB + "for(#{type} #{name}Inner : #{name}) { \n".replace("#{type}", commandParam.getTypeClass());
        if(innerParams.isEmpty()) {
            String innerParamName = commandParam.getName() + "Inner";
            String code = getSendCodeTmp(commandParam.getTypeClass()).replace("#{name}", innerParamName);
            result += TAB  + TAB + TAB  + TAB + code + "\n";
        } else {
            for (CommandParam innerCommandParam : innerParams) {
                if(StringUtils.isBlank(innerCommandParam.getTypeClass())) {
                    String innerParamName = commandParam.getName() + "Inner.";
                    if("Boolean".equals(innerCommandParam.getType())) {
                        innerParamName += "is" ;
                    } else {
                        innerParamName += "get" ;
                    }
                    innerParamName += NameUtils.upperFristStr(innerCommandParam.getName()) + "()";
                    String code = getSendCodeTmp(innerCommandParam.getType()).replace("#{name}", innerParamName);
                    result += TAB + TAB + TAB + TAB + code + " //" + innerCommandParam.getRemark() + "\n";
                } else {
                    result += parseInnerSendParam(innerCommandParam, innerCommandParam.getChildrens());
                }
            }
        }
        result += TAB + TAB + TAB + "}\n";
        return result.replace("#{name}", commandParam.getName());
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
            codeTmp = "#{name} = readString();";
        } else if("Long".equals(typeName)) {
            codeTmp = "#{name} = readLong();";
        } else if("Integer".equals(typeName)) {
            codeTmp = "#{name} = readInt();";
        } else if("Boolean".equals(typeName)) {
            codeTmp = "#{name} = readBoolean();";
        } else if("Float".equals(typeName)) {
            codeTmp = "#{name} = readFloat();";
        } else if("Double".equals(typeName)) {
            codeTmp = "#{name} = readDouble();";
        } else if("Charset".equals(typeName)) {
            codeTmp = "#{name} = readChar();";
        } else if("Byte".equals(typeName)) {
            codeTmp = "#{name} = readByte();";
        } else if("Short".equals(typeName)) {
            codeTmp = "#{name} = readShort();";
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
            if(StringUtils.isBlank(commandParam.getTypeClass())) {
                String code = TAB + TAB + getReceiveCodeTmp(commandParam).replace("#{name}", commandParam.getName());
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
            codeTmp = "#{name}(readString());";
        } else if("Long".equals(typeName)) {
            codeTmp = "#{name}(readLong());";
        } else if("Integer".equals(typeName)) {
            codeTmp = "#{name}(readInt());";
        } else if("Boolean".equals(typeName)) {
            codeTmp = "#{name}(readBoolean());";
        } else if("Float".equals(typeName)) {
            codeTmp = "#{name}(readFloat());";
        } else if("Double".equals(typeName)) {
            codeTmp = "#{name}(readDouble());";
        } else if("Charset".equals(typeName)) {
            codeTmp = "#{name}(readChar());";
        } else if("Byte".equals(typeName)) {
            codeTmp = "#{name}(readByte());";
        } else if("Short".equals(typeName)) {
            codeTmp = "#{name}(readShort());";
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
        String result = TAB  + TAB + TAB + "int #{name}Size = readShort();\n";
        result += TAB  + TAB + TAB + "#{name} = new ArrayList<>(#{name}Size );\n";
        result += TAB  + TAB + TAB + "for(int i = 0; i < #{name}Size; i++) {\n";
        if(innerParams.isEmpty()) {
            String innerParamName = commandParam.getName() + ".add";
            String code = getInnerReceiveCodeTmp(commandParam.getTypeClass()).replace("#{name}", innerParamName);
            result += TAB + TAB + TAB + code + "\n";
        } else {
            result += TAB  + TAB + TAB + TAB + commandParam.getTypeClass() + " #{name}Inner = new " + commandParam.getTypeClass() +  "();\n";
            for (CommandParam innerCommandParam : innerParams) {
                if(StringUtils.isBlank(innerCommandParam.getTypeClass())) {
                    String innerParamName = "#{name}Inner.set" +  NameUtils.upperFristStr(innerCommandParam.getName());
                    String code = getInnerReceiveCodeTmp(innerCommandParam.getType()).replace("#{name}", innerParamName);
                    result += TAB + TAB + TAB + TAB + code + " //" + innerCommandParam.getRemark() + "\n";
                } else {
                    result += parseInnerReceiveParam(innerCommandParam, innerCommandParam.getChildrens());
                }
            }
            result += TAB  + TAB + TAB + TAB + "#{name}.add(#{name}Inner);\n";
        }

        result += TAB + TAB + TAB + "}\n";
        return result.replace("#{name}", commandParam.getName());
    }



    @Override
    public String getGeneratePath(ProtoModel protoModel) {
        return UserData.getUserConfig().getProjectPath() + PathUtils.getSeparator() +
                protoModel.getGenerateConfigInfo().getProjectName() + PathUtils.getSeparator() + PathUtils.getSeparator() + "src"+ PathUtils.getSeparator() +
                "main" + PathUtils.getSeparator() + "java" +
                 PathUtils.getSeparator();
    }

    /**
     * 获取保留代码
     * @return 保留的代码块
     */
    public void getImportClass(String filePath, List<String> importPackages) {
        File file = new File(filePath);

        if(file.exists()) {
            try {
                FileReader fr;

                fr = new FileReader(file);
                BufferedReader br=new BufferedReader(fr);
                String line ;
                while ((line = br.readLine()) != null) {

                    if(line.startsWith("import ") && line.endsWith(";")) {

                        if(!line.contains("import net.zy.common.network.socket.packet.MMOSendablePacket;")
                                &&!line.contains("import io.netty.buffer.ByteBuf;")
                                &&!line.contains("import net.zy.common.network.socket.packet.MMOReceivablePacket;")

                        ) {
                            String packageStr = line.trim().replace("import ", "").replace(";", "");
                            if(!importPackages.contains(packageStr)) {
                                importPackages.add(packageStr);
                            }
                        }


                    }


                }
                br.close();
                fr.close();



            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    /**
     * 基础模板路径
     * @return 基础模板前缀
     */
    private String baseTmpPath() {
        return "java/java.game.server.";
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
