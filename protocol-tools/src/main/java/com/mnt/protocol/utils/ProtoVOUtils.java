package com.mnt.protocol.utils;


import com.mnt.protocol.model.GenerateConfigInfo;
import com.mnt.protocol.vo.BaseCommadVO;
import com.mnt.protocol.vo.BaseProtoVO;
import com.mnt.protocol.vo.CommadReqVO;
import com.mnt.protocol.vo.CommadRespVO;
import org.apache.commons.lang.StringUtils;
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
    public static String getProtoPath(XMLParseUtils.XMLObject xmlObject) {

        return getProtoAttr(xmlObject,"path");

    }

    /**
     * 获取协议类名
     * @param xmlObject
     * @return
     */
    public static String getProtoName(XMLParseUtils.XMLObject xmlObject) {

        return getProtoAttr(xmlObject,"name");

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
        result.setXmlObject(xmlObject);
        return result;
    }

    /**
     * 获取协议命令列表
     * @param xmlObject
     * @return
     */
    public static List<BaseCommadVO> getCommads(XMLParseUtils.XMLObject xmlObject) {
        List<Node> nodes = xmlObject.findEle("protos/action");
        List<BaseCommadVO> result = new ArrayList<>(nodes.size());
        BaseCommadVO vo;
        for (Node node : nodes) {
            vo = new BaseCommadVO();
            vo.setPath(node.valueOf("@path"));
            vo.setRemark(node.valueOf("@remark"));
            vo.setMethod(node.valueOf("@method").toUpperCase());
            String body = node.valueOf("@body");
            vo.setBody(Boolean.valueOf(body));

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
    public static List<CommadReqVO> parseCommadReqVOsToCommadVO(BaseCommadVO baseCommadVO) {
        Node requestNode = baseCommadVO.getCurrNode().selectSingleNode("request");
        List<CommadReqVO> result = new ArrayList<>();
        setCommadReqChildrenCommadVO(requestNode, result);
        return result;
    }

    private static void setCommadReqChildrenCommadVO(Node requestNode, List<CommadReqVO> result) {
        List<Node> paramNodes = requestNode.selectNodes("param");

        CommadReqVO commadReqVO;
        for (Node node : paramNodes) {
            commadReqVO = new CommadReqVO();
            commadReqVO.setName(node.valueOf("@name"));
            commadReqVO.setRemark(node.valueOf("@remark"));
            commadReqVO.setType(node.valueOf("@type"));
            commadReqVO.setValid(node.valueOf("@valid"));
            commadReqVO.setTypeClass(node.valueOf("@typeClass"));
            commadReqVO.setValMsg(node.valueOf("@valMsg"));
            String length = node.valueOf("@length");

            String limit = length;
            String min =  node.valueOf("@min");
            String max =  node.valueOf("@max");

            if(!StringUtils.isEmpty(min) || !StringUtils.isEmpty(max)) {
                limit = "[" + (StringUtils.isEmpty(min) ? "- ∞" : min) + "," + (StringUtils.isEmpty(max) ? "+ ∞" : max) + "]";
                //如果为string类型 则最小值为0
                if("string".equals(String.valueOf(commadReqVO.getType()).toLowerCase())) {
                    limit.replace("- ∞", "0");
                }

            }

            commadReqVO.setLimit(limit);
            try {
                if(!StringUtils.isEmpty(length)) {
                    commadReqVO.setLength(Integer.parseInt(length));
                }
                if(!StringUtils.isEmpty(min)) {
                    commadReqVO.setMin(min);
                }
                if(!StringUtils.isEmpty(max)) {
                    commadReqVO.setMax(max);
                }
            } catch (Exception e) {
                ConsoleLogUtils.log( "[" + commadReqVO.getName() + "]length, max, min必须为数字");
                ConsoleLogUtils.log(e);
            }

            String must = node.valueOf("@must");
            commadReqVO.setMust(Boolean.valueOf(must));
            commadReqVO.setTest(node.valueOf("@test"));
            commadReqVO.setFormat(node.valueOf("@format"));
            result.add(commadReqVO);

            List<Node> innerParamNodes = requestNode.selectNodes("param");
            if(!innerParamNodes.isEmpty()) {
                setCommadReqChildrenCommadVO(node, commadReqVO.getChildrens());
            }

        }

    }


    /**
     * 解析详细命令
     * @return
     */
    public static List<CommadRespVO> parseCommadRespVOsToCommadVO(BaseCommadVO baseCommadVO) {
        Node requestNode = baseCommadVO.getCurrNode().selectSingleNode("response");
        List<CommadRespVO> result = new ArrayList<>();


        boolean isDataStart = false;
        if(isDataStart) {
            List<Node> paramNodes = requestNode.selectNodes("param");
            //答复参数从data开始解析
            for (Node dataNode : paramNodes) {
                if("data".equals(dataNode.valueOf("@name"))) {
                    setCommadRespChildrenCommadVO(dataNode, result);
                }
            }
        } else {
            setCommadRespChildrenCommadVO(requestNode, result);
        }


        return result;
    }

    private static void setCommadRespChildrenCommadVO(Node requestNode, List<CommadRespVO> result) {
        List<Node> paramNodes = requestNode.selectNodes("param");

        CommadRespVO commadRespVO;
        for (Node node : paramNodes) {
            commadRespVO = new CommadRespVO();
            commadRespVO.setName(node.valueOf("@name"));
            commadRespVO.setRemark(node.valueOf("@remark"));
            commadRespVO.setType(node.valueOf("@type"));
            commadRespVO.setTypeClass(node.valueOf("@typeClass"));
            commadRespVO.setTest(node.valueOf("@test"));
            commadRespVO.setFormat(node.valueOf("@format"));
            result.add(commadRespVO);

            List<Node> innerParamNodes = requestNode.selectNodes("param");
            if(!innerParamNodes.isEmpty()) {
                setCommadRespChildrenCommadVO(node, commadRespVO.getChildrens());
            }

        }

    }



    public static void main(String[] args) {
        XMLParseUtils.XMLObject xmlObject = XMLParseUtils.parseXML(new File(System.getProperty("user.dir") + "/protos/test.xml"));
        System.err.println(getProtoName(xmlObject));
    }

}
