package com.mnt.protocol.utils;


import com.mnt.protocol.model.GenerateConfigInfo;
import com.mnt.protocol.vo.BaseCommandVO;
import com.mnt.protocol.vo.BaseProtoVO;
import com.mnt.protocol.vo.CommandParamVO;
import org.dom4j.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 协议vo转换工具类
 */
public class ProtoVOUtils {


    /**
     * 获取测试地址
     * @param xmlObject
     * @return
     */
    public static String getProtoAttr(XMLParseUtils.XMLObject xmlObject, String attrName) {

        return xmlObject.getRoot().attributeValue(attrName);

    }

    /**
     * 获取协议文件名
     * @param xmlObject
     * @return
     */
    public static String getProtoRemark(XMLParseUtils.XMLObject xmlObject) {

       return getProtoAttr(xmlObject,"remark");

    }


    /**
     * 获取总路径
     * @param xmlObject
     * @return
     */
    public static String getProtoModuleName(XMLParseUtils.XMLObject xmlObject) {

        return getProtoAttr(xmlObject,"moduleName");

    }

    /**
     * 获取协议类名
     * @param xmlObject
     * @return
     */
    public static String getProtoCodeLimit(XMLParseUtils.XMLObject xmlObject) {

        return getProtoAttr(xmlObject,"codeLimit");

    }

    /**
     * 获取测试路径
     * @param xmlObject
     * @return
     */
    public static String getProtoTestUrl(XMLParseUtils.XMLObject xmlObject) {

        return getProtoAttr(xmlObject,"testUrl");

    }

    /**
     * 获取测试地址
     * @param xmlObject
     * @return
     */
    public static String getProtoTestAddr(XMLParseUtils.XMLObject xmlObject) {

        return getProtoAttr(xmlObject,"testAddr");

    }

//    /**
//     * 获取请求方法
//     * @param xmlObject
//     * @return
//     */
//    public static String getProtoReqMethod(XMLParseUtils.XMLObject xmlObject) {
//        String result = getProtoAttr(xmlObject,"method");
//        if(null != result) {
//            return result.toLowerCase();
//        }
//        return result;
//    }
//
//    /**
//     * 获取是否为body请求
//     * @param xmlObject
//     * @return
//     */
//    public static boolean getProtoIsBody(XMLParseUtils.XMLObject xmlObject) {
//
//        return "true".equals(getProtoAttr(xmlObject,"body"));
//
//    }

    /**
     * 获取基础协议名称
     * @param xmlObject
     * @return
     */
    public static BaseProtoVO getProto(XMLParseUtils.XMLObject xmlObject) {
        BaseProtoVO result = new BaseProtoVO();
        result.setRemark(getProtoRemark(xmlObject));
        result.setCodeLimit(getProtoCodeLimit(xmlObject));
        result.setModuleName(getProtoModuleName(xmlObject));
        result.setXmlObject(xmlObject);
        return result;
    }

    /**
     * 获取协议命令列表
     * @param xmlObject
     * @return
     */
    public static List<BaseCommandVO> getCommads(XMLParseUtils.XMLObject xmlObject) {
        List<Node> nodes = xmlObject.findEle("protos/cmd");
        List<BaseCommandVO> result = new ArrayList<>(nodes.size());
        BaseCommandVO vo;
        for (Node node : nodes) {
            vo = new BaseCommandVO();
            vo.setName(node.valueOf("@name"));
            vo.setRemark(node.valueOf("@remark"));
            vo.setOpCode(Integer.parseInt(node.valueOf("@opCode")));
            vo.setSrc(node.valueOf("@src"));
            vo.setCurrNode(node);
            result.add(vo);
        }

        return result;
    }

    /**
     * 获取协议命令列表
     * @param xmlObject
     * @return
     */
    public static Map<String, GenerateConfigInfo> getGenerateConfigs(XMLParseUtils.XMLObject xmlObject) {
        List<Node> nodes = xmlObject.findEle("protos/generates/generate");
        Map<String, GenerateConfigInfo> result = new HashMap<>(nodes.size());
        GenerateConfigInfo generateConfigInfo;
        for (Node node : nodes) {
            generateConfigInfo = new GenerateConfigInfo();
            generateConfigInfo.setPackageName(node.valueOf("@actionPackage"));
            generateConfigInfo.setApiProjectName(node.valueOf("@apiProjectName"));
            generateConfigInfo.setProjectName(node.valueOf("@projectName"));
            generateConfigInfo.setType(node.valueOf("@type"));
            result.put(generateConfigInfo.getType(), generateConfigInfo);
        }

        return result;
    }

    /**
     * 解析详细命令
     * @return
     */
    public static List<CommandParamVO> parseCommandParamVOsToCommadVO(BaseCommandVO BaseCommandVO) {
        Node requestNode = BaseCommandVO.getCurrNode();
        List<CommandParamVO> result = new ArrayList<>();
        setCommandChildrenCommandVO(requestNode, result);
        return result;
    }

    private static void setCommandChildrenCommandVO(Node requestNode, List<CommandParamVO> result) {
        List<Node> paramNodes = requestNode.selectNodes("param");

        CommandParamVO commandParamVO;
        for (Node node : paramNodes) {
            commandParamVO = new CommandParamVO();
            commandParamVO.setName(node.valueOf("@name"));
            commandParamVO.setRemark(node.valueOf("@remark"));
            commandParamVO.setType(node.valueOf("@type"));
            commandParamVO.setTypeClass(node.valueOf("@typeClass"));


            result.add(commandParamVO);

            List<Node> innerParamNodes = requestNode.selectNodes("param");
            if(!innerParamNodes.isEmpty()) {
                setCommandChildrenCommandVO(node, commandParamVO.getChildrens());
            }

        }

    }



    public static void main(String[] args) {
        XMLParseUtils.XMLObject xmlObject = XMLParseUtils.parseXML(new File(System.getProperty("user.dir") + "/protos/test.xml"));
        System.err.println(getProtoCodeLimit(xmlObject));
    }

}
