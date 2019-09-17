package generate;

import com.mnt.common.utils.VelocityUtils;
import com.mnt.protocol.core.ProtoCodeGenerateTemplate;
import com.mnt.protocol.model.*;
import com.mnt.protocol.utils.ConsoleLogUtils;
import com.mnt.protocol.utils.NameUtils;
import com.mnt.protocol.utils.PathUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * java 接收 controller 生成
 */
public class JavaReceiveControllerGenerate extends ProtoCodeGenerateTemplate {


    @Override
    public String getType() {
        return "java.receive";
    }

    @Override
    protected void generateImpl(ProtoModel protoModel) {

        String generatePath = getGeneratePath(protoModel);

        //创建路径
        checkAndCreateDir(generatePath);
        checkAndCreateDir(generatePath + PathUtils.getSeparator() +
                PathUtils.packageToPath(protoModel.getGenerateConfigInfo().getPackageName())
                + PathUtils.getSeparator());

        //生成controller
        String controllerPath = generatePath + PathUtils.getSeparator() +
                PathUtils.packageToPath(protoModel.getGenerateConfigInfo().getPackageName())
                + PathUtils.getSeparator() + protoModel.getControllerName() + ".java";

        Map<String, Object> controllerParams = new HashMap<>();

        //获取保留代码
        String controllerHoldCode = getHoldCode(controllerPath);

        controllerParams.put("importPackages", protoModel.getImportPackages());
        controllerParams.put("package", protoModel.getGenerateConfigInfo().getPackageName());
        controllerParams.put("remark", protoModel.getRemark());
        controllerParams.put("user", protoModel.getUser());
        controllerParams.put("generateValid", protoModel.isGenerateValid());
        controllerParams.put("date", protoModel.getDate());
        controllerParams.put("requestMapper", protoModel.getRequestMapper());
        controllerParams.put("controllerName", protoModel.getControllerName());
        //只创建新增的controller
        controllerParams.put("actions", getHoldControllerCode(controllerPath, protoModel.getActions(), protoModel.getImportPackages()));
        controllerParams.put("holdCode", controllerHoldCode);

        VelocityUtils.getInstance().parseTemplate(getControllerTemplateName(), controllerPath, controllerParams);

        //兼容 cloud api的情况
        generatePath = getApiGeneratePath(protoModel);

        for (ActionModel actionModel : protoModel.getActions()) {
            try{
                Map<String, Object> reqParams = new HashMap<>();
                //生成请求参数实体
                reqParams.put("reqParamName", actionModel.getReqClass());
                reqParams.put("user", protoModel.getUser());
                reqParams.put("date", protoModel.getDate());
                reqParams.put("remark", actionModel.getRemark());
                reqParams.put("reqParams", actionModel.getCommadReqParams());
                reqParams.put("package", actionModel.getReqPackage());
                reqParams.put("importPackages", actionModel.getReqImprotClass());
                reqParams.put("body", actionModel.getBody());

                //创建内部类代码
                String innerReqClassStr = convertReqInnerClass(actionModel.getInnerReqParams());
                reqParams.put("innerClassStr", innerReqClassStr);

                String reqParamPath = generatePath + PathUtils.packageToPath(actionModel.getReqPackage())  + PathUtils.getSeparator() + actionModel.getReqClass() + ".java";
                String reqParaHoldCode = getHoldCode(reqParamPath);
                reqParams.put("holdCode", reqParaHoldCode);

                //创建路径
                checkAndCreateDir( generatePath + PathUtils.packageToPath(actionModel.getReqPackage()));

                VelocityUtils.getInstance().parseTemplate(getReqParamTemplateName(), reqParamPath, reqParams);





                //生成答复参数实体
                Map<String, Object> respParams = new HashMap<>();
                respParams.put("respParamName", actionModel.getRespClass());
                respParams.put("user", protoModel.getUser());
                respParams.put("date", protoModel.getDate());
                respParams.put("remark", actionModel.getRemark());
                respParams.put("respParams", getResponeDataParam(actionModel.getCommadRespParams()));
                respParams.put("package", actionModel.getRespPackage());
                respParams.put("importPackages", actionModel.getRespImprotClass());

                //创建内部类代码
                String innerRespClassStr = convertRespInnerClass(actionModel.getInnerRespParams());
                respParams.put("innerClassStr", innerRespClassStr);

                String respParamPath = generatePath + PathUtils.packageToPath(actionModel.getRespPackage()) + PathUtils.getSeparator() + actionModel.getRespClass() + ".java";

                String resqParaHoldCode = getHoldCode(respParamPath);
                respParams.put("holdCode", resqParaHoldCode);
                //创建路径
                checkAndCreateDir(generatePath + PathUtils.packageToPath(actionModel.getRespPackage()));

                VelocityUtils.getInstance().parseTemplate(getRespParamemplateName(), respParamPath, respParams);




            } catch (Exception e) {
                e.printStackTrace();
                ConsoleLogUtils.log("[" + actionModel.getActionName() + "] - 参数类生成错误 : Exception ---  " + e);
            }


        }








    }


    /**
     * 获取是否生成内部data数据
     * @param commadRespParams
     * @return
     */
    private List<CommadRespParam> getResponeDataParam(List<CommadRespParam> commadRespParams) {
        boolean isGenerateData = false;

        if(isGenerateData) {
            return commadRespParams;
        } else {
            for (CommadRespParam commadRespParam : commadRespParams) {
                if("data".equals(commadRespParam.getName())) {
                    return commadRespParam.getChildrens();
                }
            }
        }
        return commadRespParams;
    }

    /**
     * 转换内部类请求参数
     * @param innerReqParamsMap
     * @return
     */
    private String convertReqInnerClass(Map<CommadReqParam, List<CommadReqParam>> innerReqParamsMap) {

        StringBuilder resultSB = new StringBuilder();

        //请求参数内部类
        for (Map.Entry<CommadReqParam, List<CommadReqParam>> innerReqParamEntry : innerReqParamsMap.entrySet()) {
            Map<String, Object> innerReqParams = new HashMap<>();

            String innerClassName = NameUtils.buildInnerClassName(innerReqParamEntry.getKey().getName());

            //生成请求参数实体
            innerReqParams.put("reqParamName", innerClassName);
            innerReqParams.put("remark", innerReqParamEntry.getKey().getRemark());
            innerReqParams.put("reqParams", innerReqParamEntry.getValue());

            String classCode = VelocityUtils.getInstance().parseTemplate(getInnerReqParamTemplateName(), innerReqParams);

            resultSB.append(classCode);
            resultSB.append("\n");
            resultSB.append("\n");
        }

        return resultSB.toString();
    }

    /**
     * 转换内部类答复参数
     * @param innerRespParamsMap
     * @return 生成代码
     */
    private String convertRespInnerClass(Map<CommadRespParam, List<CommadRespParam>> innerRespParamsMap) {

        StringBuilder resultSB = new StringBuilder();

        //请求参数内部类
        for (Map.Entry<CommadRespParam, List<CommadRespParam>> innerRespParamEntry : innerRespParamsMap.entrySet()) {
            Map<String, Object> innerRespParams = new HashMap<>();

            String innerClassName = NameUtils.buildInnerClassName(innerRespParamEntry.getKey().getName());
            //生成请求参数实体
            innerRespParams.put("respParamName", innerClassName);
            innerRespParams.put("remark", innerRespParamEntry.getKey().getRemark());
            innerRespParams.put("respParams", innerRespParamEntry.getValue());

//            System.err.println("classCode = " + innerRespParams);
            String classCode = VelocityUtils.getInstance().parseTemplate(getInnerRespParamemplateName(), innerRespParams);

//            System.err.println("classCode = " + classCode);
            resultSB.append(classCode);
            resultSB.append("\n");
            resultSB.append("\n");

        }
        return resultSB.toString();

    }



    /**
     * 获取保留代码
     * @return 保留的代码块
     */
    public List<ActionModel> getHoldControllerCode(String filePath, List<ActionModel> actionModels, List<String> importPackages) {
        File file = new File(filePath);

        List<ActionModel> result = new ArrayList<>(actionModels);
        if(file.exists()) {
            try {

                List<ActionModel> removeActions = new ArrayList<>(actionModels.size());

                FileReader fr;

                fr = new FileReader(file);
                BufferedReader br=new BufferedReader(fr);
                String line ;
                while ((line = br.readLine()) != null) {

                    if(line.startsWith("import ") && line.endsWith(";")) {

                        if(!line.contains("import org.springframework.web.bind.annotation.RequestMapping;") &&
                                !line.contains("import org.springframework.web.bind.annotation.RestController;") &&
                                !line.contains("import com.mnt.tools.dep.BaseController;") &&
                                !line.contains("import com.mnt.tools.dep.AjaxResult;") &&
                                !line.contains("import org.springframework.web.bind.annotation.RequestBody;")
                                ) {
                            String packageStr = line.trim().replace("import ", "").replace(";", "");
                            if(!importPackages.contains(packageStr)) {
                                importPackages.add(packageStr);
                            }
                        }


                    }


                    if(line.contains("@RequestMapping")) {
                        for (ActionModel actionUrl : actionModels) {
                            if(line.contains("@RequestMapping(\"" + actionUrl.getRequestMapper() + "\")")) {
                                removeActions.add(actionUrl);
                            }
                            //request body 包引入
                            if(actionUrl.getBody()) {
                                if(!importPackages.contains("org.springframework.web.bind.annotation.RequestBody")) {
                                    importPackages.add("org.springframework.web.bind.annotation.RequestBody");
                                }
                            }
                        }

                    }

                }
                br.close();
                fr.close();

                result.removeAll(removeActions);

                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return actionModels;
    }


    @Override
    public String getGeneratePath(ProtoModel protoModel) {
        return UserData.getUserConfig().getProjectPath() + PathUtils.getSeparator() +
                protoModel.getGenerateConfigInfo().getProjectName() + PathUtils.getSeparator() + "src"+ PathUtils.getSeparator() +
                "main" + PathUtils.getSeparator() + "java"
                + PathUtils.getSeparator();
    }

    private String getApiGeneratePath(ProtoModel protoModel) {
        if(StringUtils.isEmpty(protoModel.getGenerateConfigInfo().getApiProjectName())) {
            return getGeneratePath(protoModel);
        }

        return UserData.getUserConfig().getProjectPath() + PathUtils.getSeparator() +
                protoModel.getGenerateConfigInfo().getApiProjectName() + PathUtils.getSeparator() + "src"+ PathUtils.getSeparator() +
                "main" + PathUtils.getSeparator() + "java"
                + PathUtils.getSeparator();
    }

    /**
     * 基础模板路径
     * @return 基础模板前缀
     */
    private String baseTmpPath() {
        return "java/java.receive.";
    }

    /**
     * 获取controller模板路径
     * @return 模板文件名
     */
    private String getControllerTemplateName() {
        return baseTmpPath() + "controller.vm";
    }

    /**
     * 获取请求参数模板路径
     * @return 模板文件名
     */
    private String getReqParamTemplateName() {
        return baseTmpPath() + "reqparam.vm";
    }

    /**
     * 获取答复参数模板路径
     * @return 模板文件名
     */
    private String getRespParamemplateName() {
        return baseTmpPath() + "respparam.vm";
    }

    /**
     * 获取请求嵌套参数模板路径
     * @return 模板文件名
     */
    private String getInnerReqParamTemplateName() {
        return baseTmpPath() + "innerreqparam.vm";
    }

    /**
     * 获取答复嵌套参数模板路径
     * @return 模板文件名
     */
    private String getInnerRespParamemplateName() {
        return baseTmpPath() + "innerrespparam.vm";
    }

}
