package scripts.generate;

import com.mnt.common.utils.VelocityUtils;
import com.mnt.protocol.core.ProtoCodeGenerateTemplate;
import com.mnt.protocol.model.ProtoModel;
import com.mnt.protocol.model.UserData;
import com.mnt.protocol.utils.ConsoleLogUtils;
import com.mnt.protocol.utils.PathUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * lua client 协议代码生成
 */
public class LuaLogicClientProtoGenerate extends ProtoCodeGenerateTemplate {


    @Override
    public String getType() {
        return "lua.client";
    }

    @Override
    protected void generateImpl(ProtoModel protoModel) {
        String generatePath = getGeneratePath(protoModel);

        //创建路径
        checkAndCreateDir(generatePath);

        String protosLuaFilePath =  generatePath + PathUtils.getSeparator() + protoModel.getControllerName() + ".lua";

        Map<String, Object> protosParams = new HashMap<>();

        protosParams.put("controllerName", protoModel.getControllerName());
        protosParams.put("remark", protoModel.getRemark());
        protosParams.put("user", protoModel.getUser());
        protosParams.put("date", protoModel.getDate());
        protosParams.put("moduleName", protoModel.getGenerateConfigInfo().getModuleName());
        protosParams.put("actions", protoModel.getActions());

        protosParams.put("protoName", protoModel.getRequestMapper());


        //获取保留代码
        String holdCode = getHoldCode(protosLuaFilePath);
        protosParams.put("holdCode", holdCode);

        try{
            VelocityUtils.getInstance().parseTemplate(getSendProtoTemplateName(), protosLuaFilePath, protosParams);
        } catch (Exception e) {
            e.printStackTrace();
            ConsoleLogUtils.log("[" + protoModel.getControllerName() + "] - 协议文件生成错误 : Exception ---  " + e);
        }
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
        return "lua/lua.client.";
    }

    /**
     * 获取发送协议模板路径
     * @return 模板文件名
     */
    private String getSendProtoTemplateName() {
        return baseTmpPath() + "send.proto.vm";
    }
}
