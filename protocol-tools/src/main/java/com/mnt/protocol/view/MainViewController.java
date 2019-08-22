package com.mnt.protocol.view;

import com.alibaba.fastjson.JSONObject;
import com.mnt.base.thread.ThreadPoolManager;
import com.mnt.gui.fx.base.BaseController;
import com.mnt.gui.fx.controls.dialog.DialogFactory;
import com.mnt.gui.fx.controls.file.FileChooserFacotry;
import com.mnt.gui.fx.view.anno.MainView;
import com.mnt.protocol.core.TemplateClassLoad;
import com.mnt.protocol.model.ProtoModel;
import com.mnt.protocol.model.UserData;
import com.mnt.protocol.utils.*;
import com.mnt.protocol.vo.BaseCommadVO;
import com.mnt.protocol.vo.BaseProtoVO;
import com.mnt.protocol.vo.CommadReqVO;
import com.mnt.protocol.vo.CommadRespVO;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 通讯协议工具类
 */
@MainView(appName = "通讯协议工具")
public class MainViewController extends BaseController {



    @Override
    public void init() {
        super.init();
        initProtoList();
        initProtoInfo();
        initProtoTest();
        initConsole();
        addListener();
    }

    /**
     * 添加监听
     */
    void addListener() {

    }



    /**
     * 加载最后一次选择的文件夹
     */
    private void loadLastDir() {
        String defaultPath = UserData.getUserConfig().getLastSelectedDir();
        if(null == defaultPath || "".equals(defaultPath)) {
            return;
        }

        File dir = new File(defaultPath);
        if(null != dir && dir.isDirectory()) {
            ConsoleLogUtils.log("协议文件夹路径 : " + dir.getAbsolutePath());
            loadProto(defaultPath);
        }
    }



    @FXML
    void processMenuAbout(ActionEvent event) {

    }

    @FXML
    void processMenuOpenFile(ActionEvent event) {
        String defaultPath = UserData.getUserConfig().getLastSelectedDir();
        final File dir = FileChooserFacotry.chooserDirectorControl(getMainStage(), defaultPath);
        if(null == dir) {
            return;
        }

        UserData.getUserConfig().setLastSelectedDir(dir.getAbsolutePath());
        if(null != dir && dir.isDirectory()) {
            ConsoleLogUtils.log(dir.getAbsolutePath());
            loadProto(defaultPath);
        }
    }

    @FXML
    void processMenuRequestSetting(ActionEvent event) {

    }

    @FXML
    void processMenuSetting(ActionEvent event) {

    }

    @FXML
    void processMenuGenerateCode(ActionEvent event) {
        generateCode();
    }

    /**
     * 生成代码
     */
    void generateCode() {

        BaseProtoVO baseProtoVO = getSelectedProto();

        List<BaseCommadVO> baseCommadVOs = getCreateSelected();

        if(null == baseProtoVO) {
            DialogFactory.getInstance().showFaildMsg("生成代码错误", "请选择协议", ()->{});
            return;
        }

        if(null == baseCommadVOs || baseCommadVOs.isEmpty()) {
            DialogFactory.getInstance().showFaildMsg("生成代码错误", "请选择生成的命令", ()->{});
            return;
        }

        String selectType = "java";

        try {
            ProtoModel protoModel = ProtoModelConvertUtils.convert(baseProtoVO, baseCommadVOs);

            TemplateClassLoad.PROTO_CODE_GENERATE_TEMPLATE.getScripts().forEach((baseCodeGenerate)-> {

                if(selectType.equals(baseCodeGenerate.getType())) {
                    baseCodeGenerate.generate(protoModel);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            ConsoleLogUtils.log("代码生成失败!");
            ConsoleLogUtils.log(e);
            DialogFactory.getInstance().showFaildMsg("生成代码错误", "生成异常", ()->{});
            return;
        }



        DialogFactory.getInstance().showSuccessMsg("生成成功", "代码生成成功", ()->{});

    }


    /************************************************************************************************* 协议列表start *****************************************************************************************************/

    @FXML
    private Button btnTopDir;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnNext;

    @FXML
    private ListView<BaseProtoVO> listViewProtos;

    @FXML
    private ListView<BaseCommadVO> listViewCommad;

    private ObservableList<BaseProtoVO> itemProtos = FXCollections.observableArrayList();

    private ObservableList<BaseCommadVO> itemCommads = FXCollections.observableArrayList();

    /**
     * 上一步栈
     */
    private Stack<String> backStack = new Stack<>();

    /**
     * 下一步栈
     */
    private Stack<String> nextStack = new Stack<>();



    /**
     * 初始化协议列表
     */
    private void initProtoList() {
        checkCanBack();
        checkCanNext();

        listViewProtos.setItems(itemProtos);
        listViewCommad.setItems(itemCommads);

        listViewProtos.setCellFactory(new Callback<ListView<BaseProtoVO>, ListCell<BaseProtoVO>>() {
            @Override
            public ListCell<BaseProtoVO> call(ListView<BaseProtoVO> param) {
                return new ListCell<BaseProtoVO>() {
                    @Override
                    protected void updateItem(BaseProtoVO item, boolean empty) {
//                        super.updateItem(item, empty);
                        if(!empty) {
                            if(item.isDir()) {
                                HBox hbox = new HBox();
                                hbox.setAlignment(Pos.CENTER_LEFT);
                                Image imgDir = new Image(getClass().getResourceAsStream("res/fileDir.png"));
                                Label label = new Label(item.getRemark());
                                ImageView imgv = new ImageView(imgDir);
                                imgv.setFitWidth(20);
                                imgv.setFitHeight(20);
                                hbox.getChildren().add(imgv);
                                hbox.getChildren().add(label);
                                hbox.setSpacing(3);
                                setGraphic(hbox);
                            } else {
                                Label label = new Label(item.getRemark());
                                setGraphic(label);
                            }

                        } else {
                            setGraphic(null);
                        }
                        super.updateItem(item, empty);
                    }
                };
            }
        });

        listViewCommad.setCellFactory(new Callback<ListView<BaseCommadVO>, ListCell<BaseCommadVO>>() {
            @Override
            public ListCell<BaseCommadVO> call(ListView<BaseCommadVO> param) {
                return new ListCell<BaseCommadVO>(){
                    @Override
                    protected void updateItem(BaseCommadVO item, boolean empty) {

                        if(!empty) {
                            String text = item.getRemark();//item.getRemark() + "(" + item.getPath() + ")";
                            boolean mulitChooseCommad = true;
                            if(mulitChooseCommad) {

                                HBox hbox ;
                                CheckBox checkBox;
                                Label lbl;
//                                if(null == getGraphic()) {
                                hbox = new HBox();
                                hbox.setAlignment(Pos.CENTER_LEFT);
                                checkBox = new CheckBox();
                                lbl = new Label();
                                hbox.getChildren().add(checkBox);
                                hbox.getChildren().add(lbl);
                                setGraphic(hbox);
//                                } else {
//                                    hbox = ((HBox)getGraphic());
//                                    checkBox = (CheckBox) hbox.getChildren().get(0);
//                                    lbl = (Label) hbox.getChildren().get(1);
//                                }

                                lbl.setText(text);
                                item.chooseProperty().bindBidirectional(checkBox.selectedProperty());

                            } else {

//                                if(null == getGraphic()) {
                                Label label = new Label(text);
                                setGraphic(label);
//                                } else {
//                                    ((Label)getGraphic()).setText(text);
//                                }

                            }

                        } else {
                            setGraphic(null);
                        }

                        super.updateItem(item, empty);
                    }
                };
            }
        });

        listViewProtos.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BaseProtoVO>() {
            @Override
            public void changed(ObservableValue<? extends BaseProtoVO> observable, BaseProtoVO oldValue, BaseProtoVO newValue) {
                itemCommads.clear();

                if(null != newValue && !newValue.isDir()) {
                    List<BaseCommadVO> baseCommadVOs = ProtoVOUtils.getCommads(newValue.getXmlObject());
                    itemCommads.addAll(baseCommadVOs);
                }

            }
        });

        listViewProtos.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() == 2) {
                    BaseProtoVO baseProtoVO = listViewProtos.getSelectionModel().getSelectedItem();
                    if(baseProtoVO.isDir()) {
                        nextStack.clear();
                        checkCanNext();
                        backStack.add(UserData.getUserConfig().getLastSelectedDir());
                        selDir(baseProtoVO.getFilePath());
                    }
                }
            }
        });

        listViewCommad.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BaseCommadVO>() {
            @Override
            public void changed(ObservableValue<? extends BaseCommadVO> observable, BaseCommadVO oldValue, BaseCommadVO newValue) {
                selectCommad(newValue);
            }
        });
    }

    /**
     * 判断是否可以上一步
     */
    private void checkCanBack() {
        if(backStack.empty()) {
            btnBack.setDisable(true);
        } else {
            btnBack.setDisable(false);
        }
    }

    /**
     * 判断是否可以下一步
     */
    private void checkCanNext() {
        if(nextStack.empty()) {
            btnNext.setDisable(true);
        } else {
            btnNext.setDisable(false);
        }
    }

    /**
     * 选择文件夹操作
     * @param dirPath
     */
    private void selDir(String dirPath) {

        UserData.getUserConfig().setLastSelectedDir(dirPath);
        UserData.saveUserConfig();
        loadProto(dirPath);
        checkCanBack();
    }

    /**
     * 加载协议
     * @param dirPath
     */
    private void loadProto(String dirPath) {
        File file =  new File(dirPath);
        File[] xmlFiles = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml") || new File(dir.getPath() + "/" + name).isDirectory();
            }
        });

        for(File xmlFile : xmlFiles){
            try {

                if(xmlFile.isDirectory()) {
                    BaseProtoVO baseProtoVO = new BaseProtoVO();
                    baseProtoVO.setFilePath(xmlFile.getAbsolutePath());
                    baseProtoVO.setRemark(xmlFile.getName());
                    baseProtoVO.setDir(true);
                    itemProtos.add(baseProtoVO);
                } else {
                    XMLParseUtils.XMLObject xmlObject = XMLParseUtils.parseXML(xmlFile);

                    BaseProtoVO baseProtoVO = ProtoVOUtils.getProto(xmlObject);

                    itemProtos.add(baseProtoVO);
                }

            } catch (Exception e) {
                e.printStackTrace();
                ConsoleLogUtils.log("文件加载失败 : " + file.getAbsolutePath() + " | " + e.getLocalizedMessage());
            }
        }

    }

    /**
     * 清除所有文件关联
     */
    private void clean() {
        if(!itemProtos.isEmpty()) {
            for (BaseProtoVO baseProtoVO : itemProtos) {
                baseProtoVO.clean();
            }
        }
        itemProtos.clear();

    }

    /**
     * 获取当前协议
     * @return
     */
    private BaseProtoVO getSelectedProto() {
        return listViewProtos.getSelectionModel().getSelectedItem();
    }

    /**
     * 获取所有选中
     * @return
     */
    private List<BaseCommadVO> getCreateSelected() {
        List<BaseCommadVO> result = new ArrayList<>();

        itemCommads.forEach((itemCommad) -> {
            if(itemCommad.isChoose()) {
                result.add(itemCommad);
            }
        });

        return Collections.unmodifiableList(result);
    }

    /**
     * 获取选择的命令
     * @return
     */
    private BaseCommadVO getSelectedCommad() {
        return listViewCommad.getSelectionModel().getSelectedItem();
    }


    @FXML
    void processBack(ActionEvent event) {
        String dir = backStack.pop();
        nextStack.push(UserData.getUserConfig().getLastSelectedDir());
        selDir(dir);
        checkCanNext();
        checkCanBack();
    }

    @FXML
    void processNext(ActionEvent event) {
        String dir = nextStack.pop();
        backStack.push(UserData.getUserConfig().getLastSelectedDir());
        selDir(dir);
        checkCanBack();
        checkCanNext();
    }

    @FXML
    void processOpenDir(ActionEvent event) {
        try {
            Desktop.getDesktop().open(new File(UserData.getUserConfig().getLastSelectedDir()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void processTopDir(ActionEvent event) {
        backStack.clear();
        nextStack.clear();
        checkCanBack();
        checkCanNext();
        File currDir = new File(UserData.getUserConfig().getLastSelectedDir());
        selDir(currDir.getParent());
    }

    @FXML
    void processUpdate(ActionEvent event) {
        String dirPath = UserData.getUserConfig().getLastSelectedDir();
        final File dir = new File(dirPath);
        if(null != dir && dir.isDirectory()) {
            loadProto(dirPath);

        }
    }

    /************************************************************************************************* 协议列表end *****************************************************************************************************/


    /************************************************************************************************* 协议信息start ****************************************************************************************************/

    @FXML
    private TextField txtUrlPathFrist;

    @FXML
    private TextField txtUrlPathScoend;

    @FXML
    private CheckBox cbIsBody;

    @FXML
    private TreeTableView<CommadReqVO> treeTableRequest;

    @FXML
    private TreeTableColumn<CommadReqVO, String> trclumReqName;

    @FXML
    private TreeTableColumn<CommadReqVO, String> trclumReqRemark;

    @FXML
    private TreeTableColumn<CommadReqVO, String> trclumReqType;

    @FXML
    private TreeTableColumn<CommadReqVO, Integer> trclumReqLength;

    @FXML
    private TreeTableColumn<CommadReqVO, Boolean> trclumReqMust;

    @FXML
    private TreeTableColumn<CommadReqVO, String> trclumReqTest;

    /**
     * 返回参数相关
     */
    @FXML
    private TreeTableView<CommadRespVO> treeTableResponse;

    @FXML
    private TreeTableColumn<CommadRespVO, String> trclumRespName;

    @FXML
    private TreeTableColumn<CommadRespVO, String> trclumRespRemark;

    @FXML
    private TreeTableColumn<CommadRespVO, String> trclumRespType;

    @FXML
    private TreeTableColumn<CommadRespVO, String> trclumRespTypeClass;

    @FXML
    private TreeTableColumn<CommadRespVO, String> trclumRespTest;

    /**
     * 初始化协议信息
     */
    private void initProtoInfo() {
        initReqTree();
    }
    /**
     * 初始化请求树
     */
    private void initReqTree() {
        //req
        treeTableRequest.getSelectionModel().setCellSelectionEnabled(true);
        trclumReqName.setCellValueFactory(new TreeItemPropertyValueFactory("name"));
        trclumReqRemark.setCellValueFactory(new TreeItemPropertyValueFactory("remark"));
        trclumReqType.setCellValueFactory(new TreeItemPropertyValueFactory("type"));
        trclumReqLength.setCellValueFactory(new TreeItemPropertyValueFactory("length"));
        trclumReqMust.setCellValueFactory(new TreeItemPropertyValueFactory("must"));
        trclumReqTest.setCellValueFactory(new TreeItemPropertyValueFactory("test"));

        treeTableRequest.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.isControlDown() && event.getCode() == KeyCode.C) {
                    ObservableList<TreeTablePosition<CommadReqVO,?>> selectCells = treeTableRequest.getSelectionModel().getSelectedCells();
                    if(null != selectCells && !selectCells.isEmpty()) {
                        TreeItem<CommadReqVO> item = selectCells.get(0).getTreeItem();
                        int selectIndex = selectCells.get(0).getColumn();
                        String selectVal = null;
                        if(selectIndex == 0) {
                            selectVal = item.getValue().getName();
                        } else if(selectIndex == 1) {
                            selectVal = item.getValue().getRemark();
                        } else if(selectIndex == 2) {
                            selectVal = item.getValue().getType();
                        } else if(selectIndex == 3) {
                            selectVal = String.valueOf(item.getValue().getLength());
                        } else if(selectIndex == 4) {
                            selectVal = String.valueOf(item.getValue().isMust());
                        } else if(selectIndex == 5) {
                            selectVal = item.getValue().getTest();
                        } else {

                        }

                        if(null != selectVal) {
                            setSysClipboardText(selectVal);
                        }


                    }


                }
            }
        });

        //resp
        treeTableResponse.getSelectionModel().setCellSelectionEnabled(true);
        trclumRespName.setCellValueFactory(new TreeItemPropertyValueFactory("name"));
        trclumRespRemark.setCellValueFactory(new TreeItemPropertyValueFactory("remark"));
        trclumRespType.setCellValueFactory(new TreeItemPropertyValueFactory("type"));
        trclumRespTest.setCellValueFactory(new TreeItemPropertyValueFactory("test"));
        trclumRespTypeClass.setCellValueFactory(new TreeItemPropertyValueFactory("typeClass"));

        treeTableResponse.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.isControlDown() && event.getCode() == KeyCode.C) {
                    ObservableList<TreeTablePosition<CommadRespVO,?>> selectCells = treeTableResponse.getSelectionModel().getSelectedCells();
                    if(null != selectCells && !selectCells.isEmpty()) {
                        TreeItem<CommadRespVO> item = selectCells.get(0).getTreeItem();
                        int selectIndex = selectCells.get(0).getColumn();
                        String selectVal = null;
                        if(selectIndex == 0) {
                            selectVal = item.getValue().getName();
                        } else if(selectIndex == 1) {
                            selectVal = item.getValue().getRemark();
                        } else if(selectIndex == 2) {
                            selectVal = item.getValue().getType();
                        } else if(selectIndex == 3) {
                            selectVal = item.getValue().getTest();
                        }  else {

                        }

                        if(null != selectVal) {
                            setSysClipboardText(selectVal);
                        }
                    }


                }
            }
        });

//        trclumReqTest.setCellFactory(new Callback<TreeTableColumn<CommadReqVO, String>, TreeTableCell<CommadReqVO, String>>() {
//            @Override
//            public TreeTableCell<CommadReqVO, String> call(TreeTableColumn<CommadReqVO, String> param) {
//                return new TreeTableCell<CommadReqVO, String>() {
//                    @Override
//                    public void updateItem(String item, boolean empty) {
//                        if(!empty) {
//
//                            TextField textField = new TextField(item);
//                            textField.textProperty().bindBidirectional(getTreeTableRow().getTreeItem().getValue().testProperty());
//                            setGraphic(textField);
//                        } else {
//                            setGraphic(null);
//                        }
//                        super.updateItem(item, empty);
//                    }
//                };
//            }
//        });
    }

    /**
     * 选择协议
     * @param baseCommad
     */
    private void selectCommad(BaseCommadVO baseCommad) {

        if(null == baseCommad) {
            return;
        }

        if(null != treeTableRequest.getRoot()) {
            //清除当前选项
            treeTableRequest.getRoot().getChildren().clear();
        }

        BaseProtoVO baseProtoVO = getSelectedProto();
        //请求一级路径
        String path = ProtoVOUtils.getProtoPath(baseProtoVO.getXmlObject());

        txtUrlPathFrist.setText(path);
        txtUrlPathScoend.setText(baseCommad.getPath());

//        //设置请求方法
//        String method = baseCommad.getMethod();
//        lblReqMethod.setText(method);

        //是否为body
        boolean isBody = baseCommad.isBody();
        cbIsBody.setSelected(isBody);


        //解析请求参数
        List<CommadReqVO> commadReqVOs = ProtoVOUtils.parseCommadReqVOsToCommadVO(baseCommad);

//        for (CommadReqVO commadReqVO : commadReqVOs) {
//
//
//            treeTableRequest.getRoot().getChildren().add();
//        }
        treeTableRequest.setRoot(new TreeItem<>());
        setReqChildren(treeTableRequest.getRoot(), commadReqVOs);

        //解析答复参数
        treeTableResponse.setRoot(new TreeItem<>());
        List<CommadRespVO> commadResp = ProtoVOUtils.parseCommadRespVOsToCommadVO(baseCommad);
        setRespChildren(treeTableResponse.getRoot(), commadResp);



    }

    /**
     * 递归设置子节点
     * @param baseCommadItem
     * @param commadReqVOs
     */
    private void setReqChildren(TreeItem<CommadReqVO> baseCommadItem, List<CommadReqVO> commadReqVOs) {
        TreeItem<CommadReqVO> treeItem;
        for (CommadReqVO commadReqVO : commadReqVOs) {
            treeItem =  new TreeItem<>(commadReqVO);
            baseCommadItem.getChildren().add(treeItem);
            if(!commadReqVO.getChildrens().isEmpty()) {
                setReqChildren(treeItem, commadReqVO.getChildrens());
            }

        }
    }

    /**
     * 递归设置返回子节点
     * @param baseCommadItem
     * @param commadRespVOs
     */
    private void setRespChildren(TreeItem<CommadRespVO> baseCommadItem, List<CommadRespVO> commadRespVOs) {
        TreeItem<CommadRespVO> treeItem;
        for (CommadRespVO commadRespVO : commadRespVOs) {
            treeItem =  new TreeItem<>(commadRespVO);
            treeItem.setExpanded(true);
            baseCommadItem.getChildren().add(treeItem);
            if(!commadRespVO.getChildrens().isEmpty()) {
                setRespChildren(treeItem, commadRespVO.getChildrens());
            }

        }
    }


    /************************************************************************************************* 协议信息end *****************************************************************************************************/



    /************************************************************************************************* 测试协议start ****************************************************************************************************/

    @FXML
    private TextField txtRequestUrl;

    @FXML
    private TextField txtXCount;

    @FXML
    private TextArea txtAreaRequest;

    @FXML
    private TextArea txtAreaResonse;

    /**
     * 请求路径
     */
    private String copyRequestUrl;

    /**
     * 初始化协议测试
     */
    private void initProtoTest() {


    }



    @FXML
    void processCopyURL(ActionEvent event) {
        if(null == copyRequestUrl) {
            DialogFactory.getInstance().showFaildMsg("复制失败", "请选择请求的的命令", ()->{});
            return;
        }

        setSysClipboardText(copyRequestUrl);
        DialogFactory.getInstance().showSuccessMsg("复制成功", copyRequestUrl, ()->{});

    }

    /**
     * 请求测试
     * @param testUrl
     */
    private void requestTest(String testUrl) {

        //是否为body请求
        if(cbIsBody.isSelected()) {
            bodyTest(testUrl);
        } else {
            notEncryptTest(testUrl);
        }

    }


    @FXML
    void processTest(ActionEvent event) {
        String testUrl = txtRequestUrl.getText();

        if("" == testUrl || "".equals(testUrl)) {
            DialogFactory.getInstance().showFaildMsg("请求失败", "请选择请求的的命令", ()->{});
            return;
        }

        requestTest(testUrl);
    }

    @FXML
    void processTest100(ActionEvent event) {
        String testUrl = txtRequestUrl.getText();

        if("" == testUrl || "".equals(testUrl)) {
            DialogFactory.getInstance().showFaildMsg("请求失败", "请选择请求的的命令", ()->{});
            return;
        }

        for (int i = 0; i < 20 ; i ++) {
            for (int j = 0; j < 5 ; j ++) {
                ThreadPoolManager.getInstance().schedule(()->{

                    requestTest(testUrl);

                }, 500 * i);
            }

        }
    }

    @FXML
    void processTestX(ActionEvent event) {
        String testUrl = txtRequestUrl.getText();

        if("" == testUrl || "".equals(testUrl)) {
            DialogFactory.getInstance().showFaildMsg("请求失败", "请选择请求的的命令", ()->{});
            return;
        }

        for (int i = 0; i < 20 ; i ++) {
            for (int j = 0; j < 5 ; j ++) {
                ThreadPoolManager.getInstance().schedule(()->{

                    requestTest(testUrl);

                }, 500 * i);
            }

        }
    }


    /**
     * 不加密请求
     * @param testUrl
     */
    private void notEncryptTest(String testUrl) {

        StringBuilder paramUrlSB = new StringBuilder();
        final SimpleBooleanProperty isFrist = new SimpleBooleanProperty(false);

        treeTableRequest.getRoot().getChildren().forEach(commadReqVOTreeItem ->{
            if(!StringUtils.isEmpty(commadReqVOTreeItem.getValue().getTest())) {
                if(isFrist.get()) {
                    isFrist.set(false);
                } else {
                    paramUrlSB.append("&");
                }
                paramUrlSB.append(commadReqVOTreeItem.getValue().getName());
                paramUrlSB.append("=");
                paramUrlSB.append(commadReqVOTreeItem.getValue().getTest());
            }

//            paramsStr += paramEntry.getKey() + "=" + String.valueOf(paramEntry.getValue());

        });

        //http请求的地址
        String requestParamUrl = testUrl + "?" + paramUrlSB.toString();

        ConsoleLogUtils.log("请求URL : [" + requestParamUrl + "]");

        String requestResult = HttpRequestUtils.getHttpResult(requestParamUrl, "GET");

        if(null == requestResult || "".equals(requestResult)) {
            txtAreaResonse.setText("请求异常或超时!");
            //请求返回超时
            return;
        }

        String formatRequestResult = JSONFormatUtils.formatJson(requestResult);

        Platform.runLater(()-> {
            //设置返回数据
            txtAreaResonse.setText(formatRequestResult);
        });

    }

    /**
     * body请求
     * @param testUrl
     */
    private void bodyTest(String testUrl) {


        JSONObject jsonObject = new JSONObject();

        treeTableRequest.getRoot().getChildren().forEach(commadReqVOTreeItem ->{
            if(!commadReqVOTreeItem.getValue().getChildrens().isEmpty()) {
                final Map<String, Object> paramMap = new HashMap<>(commadReqVOTreeItem.getValue().getChildrens().size());

                buildInnerJson(commadReqVOTreeItem.getValue().getChildrens(), paramMap);
                List<Map<String, Object>> paramList = new ArrayList<>();
                paramList.add(paramMap);
                jsonObject.put(commadReqVOTreeItem.getValue().getName(), paramList);
            } else {
                if(!StringUtils.isEmpty(commadReqVOTreeItem.getValue().getTest())) {
                    jsonObject.put(commadReqVOTreeItem.getValue().getName(), commadReqVOTreeItem.getValue().getTest());
                }
            }

        });
        String json = jsonObject.toJSONString();

        String requestResult = HttpRequestUtils.getHttpBodyResult(testUrl, json);

        if(null == requestResult || "".equals(requestResult)) {
            //请求返回超时
            txtAreaResonse.setText("请求异常或超时!");
            return;
        }

        String formatRequestResult = JSONFormatUtils.formatJson(requestResult);

        //设置返回数据
        txtAreaResonse.setText(formatRequestResult);

    }

    /**
     * 递归构建子类
     * @param commadReqVOs
     * @param paramMap
     */
    private void buildInnerJson(List<CommadReqVO> commadReqVOs, Map<String, Object> paramMap) {
        for (CommadReqVO commadReqVO : commadReqVOs) {
            if(!commadReqVO.getChildrens().isEmpty()) {
                Map<String, Object> innerParamMap = new HashMap<>(commadReqVO.getChildrens().size());
                List<Map<String, Object>> paramList = new ArrayList<>();
                paramList.add(innerParamMap);
                paramMap.put(commadReqVO.getName(), paramList);
                buildInnerJson(commadReqVO.getChildrens(), innerParamMap);
                continue;
            }
            if(!StringUtils.isEmpty(commadReqVO.getTest())) {
                paramMap.put(commadReqVO.getName(), commadReqVO.getTest());


            }

        }
    }

    /************************************************************************************************* 测试协议end ****************************************************************************************************/



    /************************************************************************************************* 控制台start ****************************************************************************************************/


    @FXML
    private TextArea txtAreaConsole;

    private static Queue<String> logQueue = new ArrayBlockingQueue<String>(10000);

    /**
     * 初始化控制台
     */
    private void initConsole() {
        initLogQueue();
        loadLastDir();
    }

    /**
     * 初始化log队列
     */
    private void initLogQueue() {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(()-> {
            String log = logQueue.poll();
            if(null != log) {
                Platform.runLater(()-> {
                    txtAreaConsole.appendText(log + "\n");
                });

            }

        }, 300, 50);
    }




    @FXML
    void processClean(ActionEvent event) {
        txtAreaConsole.clear();
    }

    @FXML
    void processCopy(ActionEvent event) {
        String context = txtAreaConsole.getText();

        if(!"".equals(context) && null != context) {
            setSysClipboardText(context);
        }
    }


    /**
     * 设置复制内容到剪切板
     * @param writeMe
     */
    private void setSysClipboardText(String writeMe) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(writeMe);
        clipboard.setContents(tText, null);
    }


    /**
     * 添加log信息到控制台
     * @param log
     */
    public static void addConsloeLog(String log, Object level) {
        logQueue.add(log);
    }

    /************************************************************************************************* 控制台end ****************************************************************************************************/

}
