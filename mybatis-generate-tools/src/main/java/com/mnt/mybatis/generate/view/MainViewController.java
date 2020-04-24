package com.mnt.mybatis.generate.view;

import com.mnt.common.utils.GenerateDataTypeUtils;
import com.mnt.common.utils.GenerateNameUtils;
import com.mnt.gui.fx.base.BaseController;
import com.mnt.gui.fx.controls.dialog.DialogFactory;
import com.mnt.gui.fx.controls.dialog.confirm.ConfirmDialog;
import com.mnt.gui.fx.loader.FXMLLoaderUtil;
import com.mnt.gui.fx.table.TabelCellFactory;
import com.mnt.gui.fx.table.TableViewSupport;
import com.mnt.gui.fx.view.anno.MainView;
import com.mnt.mybatis.generate.component.DBConfigController;
import com.mnt.mybatis.generate.component.GenerateConfigController;
import com.mnt.mybatis.generate.core.BaseDBLoadTemplate;
import com.mnt.mybatis.generate.core.load.TemplateClassLoad;
import com.mnt.mybatis.generate.model.UserData;
import com.mnt.mybatis.generate.model.db.DBCloumn;
import com.mnt.mybatis.generate.model.db.DBModel;
import com.mnt.mybatis.generate.model.db.JDBCInfo;
import com.mnt.mybatis.generate.model.generate.GenerateConfig;
import com.mnt.mybatis.generate.vo.TableColumnVO;
import com.mnt.mybatis.generate.vo.TableNameVO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Mybatis代码生成工具
 */
@MainView(appName = "Mybatis代码生成工具")
public class MainViewController extends BaseController {

    @FXML
    private ComboBox<JDBCInfo> combDB;

    @FXML
    private CheckBox cbChoice;

    @FXML
    private TextField txtFilter;

    @FXML
    private ListView<TableNameVO> listTables;

    @FXML
    private TableView<TableColumnVO> tableFields;

    @FXML
    private TableColumn<TableColumnVO, String> tcolCloumnName;

    @FXML
    private TableColumn<TableColumnVO, String> tcolRemark;

    @FXML
    private TableColumn<TableColumnVO, String> tcolCloumnType;

    @FXML
    private TableColumn<TableColumnVO, Integer> tcolLength;

    /**
     * 字段表格初始化
     */
    private TableViewSupport<TableColumnVO> tableViewSupport;

    /**
     * 表格名称元素
     */
    private ObservableList<TableNameVO> itemTableNames;

    /**
     * 可选择db列表
     */
    private ObservableList<JDBCInfo> itemDBConfigs = FXCollections.observableArrayList();

    /**
     * db加载模板
     */
    private  BaseDBLoadTemplate dbLoadTemplate;

    /**
     * 搜索时的元素
     */
    private ObservableList<TableNameVO> searchItemTableNames = FXCollections.observableArrayList();

    @Override
    public void init() {
        super.init();
        initList();
        initTable();
        initSearch();
        initComb();
        initChoice();


        addListener();

        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        String lastSelJDBCInfo =  UserData.getUserConfig().getLastSelectDb();
        JDBCInfo lastJdbcInfo = null;

        //初始化列表数据
        List<JDBCInfo> jdbcInfos = UserData.getJDBCInfos();
        for (JDBCInfo jdbcInfo : jdbcInfos) {
            itemDBConfigs.add(jdbcInfo);
            if(jdbcInfo.getConfigName().equals(lastSelJDBCInfo)) {
                lastJdbcInfo = jdbcInfo;
            }
        }

        if(null != lastSelJDBCInfo && lastJdbcInfo != null){
            combDB.getSelectionModel().select(lastJdbcInfo);
        }


    }

    /**
     * 添加事件
     */
    private void addListener() {
        this.setOnKeyPressed((event) -> {

            KeyCode keyCode = event.getCode();
            if(event.isControlDown() && keyCode == KeyCode.S) {
                //save code
                processGenerateCode(null);

            }
            if(keyCode == KeyCode.F5) {
                //update db
                selJDBCInfo(getSelJDBCInfo());
                DialogFactory.getInstance().showSuccessMsg("刷新成功", "重新加载数据成功", ()->{});
            }
        });


    }

    /**
     * 初始化选择事件
     */
    private void initChoice() {
        cbChoice.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            listTables.getItems().forEach((item)-> item.setCheck(newValue));
        }));
    }

    /**
     * 初始化name列表
     */
    private void initList() {
        listTables.setItems(searchItemTableNames);

        listTables.setCellFactory(cell -> {
            return new ListCell<TableNameVO>(){
                @Override
                protected void updateItem(TableNameVO vo, boolean empty) {
                    super.updateItem(vo, empty);
                    if(!empty)
                    {
                        HBox hbox = new HBox(2);
                        CheckBox checkBox = new CheckBox();
                        checkBox.selectedProperty().bindBidirectional(vo.checkProperty());
                        Label lbl = new Label(vo.getTableName() + "(" + vo.getRemark() +  ")");
                        hbox.getChildren().add(checkBox);
                        hbox.getChildren().add(lbl);
                        setGraphic(hbox);
                    }
                    else
                    {
                        setGraphic(null);
                    }
                }
            };
        });

        //添加选择监听事件
        listTables.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TableNameVO>() {

            @Override
            public void changed(ObservableValue<? extends TableNameVO> observable, TableNameVO oldValue,
                                TableNameVO newValue) {

                selTableName(newValue);

            }
        });
    }
    /**
     * 初始化表格
     */
    private void initTable() {
        tableViewSupport = TabelCellFactory.createTableSupport(tableFields, TableColumnVO.class);


    }

    /**
     * 初始化下拉框选择
     */
    private void initComb() {
        combDB.setItems(itemDBConfigs);
        combDB.setCellFactory(new Callback<ListView<JDBCInfo>, ListCell<JDBCInfo>>() {
            @Override
            public ListCell<JDBCInfo> call(ListView<JDBCInfo> param) {
                return new ListCell<JDBCInfo>(){
                    @Override
                    protected void updateItem(JDBCInfo item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(new Label(item.getConfigName()));
                        }
                    }
                };
            }
        });
        combDB.setConverter(new StringConverter<JDBCInfo>() {
            @Override
            public String toString(JDBCInfo object) {

                return object.getConfigName();
            }

            @Override
            public JDBCInfo fromString(String string) {
                JDBCInfo jdbcInfo = new JDBCInfo();
                jdbcInfo.setConfigName(string);
                return jdbcInfo;
            }
        });
        combDB.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if(null != newValue) {
                selJDBCInfo(newValue);
            }
        }));

    }


    /**
     * 初始化搜索栏
     */
    private void initSearch() {
        txtFilter.textProperty().addListener((observable,  oldValue, newValue) -> {
                if(null != newValue)
                {
                    if(newValue.equals(""))
                    {
                        searchItemTableNames.clear();
                        listTables.setItems(itemTableNames);
                    }
                    else
                    {
                        searchItemTableNames.clear();
                        searchItemTableNames.addAll(itemTableNames.filtered(t -> {
                                if(t.getTableName().contains(newValue) || (null != t.getRemark() && t.getRemark().contains(newValue))) {
                                    return true;
                                }
                                return false;
                            }
                        ));
                        listTables.setItems(searchItemTableNames);
                    }
                }
                else
                {
                    searchItemTableNames.clear();
                    listTables.setItems(itemTableNames);
                }

            }
        );
    }

    /**
     *  选择数据库
     * @param jdbcInfo
     */
    private void selJDBCInfo(JDBCInfo jdbcInfo) {
        if(null == jdbcInfo){
            return;
        }
        for (BaseDBLoadTemplate baseDBLoadTemplate : TemplateClassLoad.BASE_DB_INFO_LOAD_TEMPLATE) {
            if(baseDBLoadTemplate.getKey().equals(jdbcInfo.getDbType())) {
                dbLoadTemplate = baseDBLoadTemplate;
                break;
            }
        }

        if(null != dbLoadTemplate) {
            //初始化列表数据
            itemTableNames = dbLoadTemplate.listTableName(getSelJDBCInfo());
            listTables.setItems(itemTableNames);
            UserData.getUserConfig().setLastSelectDb(jdbcInfo.getConfigName());
            UserData.saveUserConfig();
        }
    }

    /**
     * 当前选择的数据库信息
     * @return
     */
    private JDBCInfo getSelJDBCInfo() {
        return  combDB.getSelectionModel().getSelectedItem();
    }

    /**
     * 选择表格名称
     * @param selTableValue
     */
    private void selTableName(TableNameVO selTableValue) {
        if(null != selTableValue)
        {
            tableViewSupport.clear();
            tableViewSupport.addItems(dbLoadTemplate.listTableColumn(getSelJDBCInfo(), selTableValue.getTableName()));
        }

    }

    /**
     * 关于我们
     * @param event
     */
    @FXML
    void processAbout(ActionEvent event) {

    }

    /**
     * 导出设置
     * @param event
     */
    @FXML
    void processExportSetting(ActionEvent event) {

    }

    /**
     * 生成代码
     * @param event
     */
    @FXML
    void processGenerateCode(ActionEvent event) {
        GenerateConfig generateConfig = UserData.getSelConfig();

        if(null == generateConfig) {
            DialogFactory.getInstance().showFaildMsg("生成错误", "未选择代码生成配置", ()->{});
            return;
        }

        if(itemTableNames != null)
        {
            if(itemTableNames.isEmpty()) {
                DialogFactory.getInstance().showFaildMsg("生成错误", "当前数据库没有表", ()->{});
            } else {
                List<TableNameVO> selectTables = new ArrayList<>();
                itemTableNames.forEach(vo -> {
                    if(vo.getCheck()) {
                        selectTables.add(vo);
                    }
                });

                if(selectTables.isEmpty()) {
                    DialogFactory.getInstance().showFaildMsg("生成错误", "请选择生成的数据库表格", ()->{});
                } else {

                    List<DBModel> dbModels = new ArrayList<>();
                    String dbTtype = getSelJDBCInfo().getDbType();
                    DBModel dbModel ;
                    for (TableNameVO tableNameVO : selectTables) {
                        dbModel = new DBModel();
                        dbModel.setTableName(tableNameVO.getTableName());
                        dbModel.setRemark(tableNameVO.getRemark());
                        List<DBCloumn> dbCloumns = new ArrayList<>();
                        dbModel.setDbCloumns(dbCloumns);


                        //获取表字段
                        List<TableColumnVO> tableColumnVOs = dbLoadTemplate.listTableColumn(getSelJDBCInfo(), tableNameVO.getTableName());
                        DBCloumn dbCloumn;
                        for (TableColumnVO tableColumnVO : tableColumnVOs) {
                            dbCloumn = new DBCloumn();
                            dbCloumn.setCloumnName(tableColumnVO.getCloumnName());
                            dbCloumn.setCloumnType(tableColumnVO.getCloumnType());
                            dbCloumn.setLength(tableColumnVO.getLength());
                            dbCloumn.setRemark(tableColumnVO.getRemark());
                            dbCloumn.setCloumnJavaName(GenerateNameUtils.getJavaName(tableColumnVO.getCloumnName()));

                            dbCloumn.setMethodName(GenerateNameUtils.getClassFileName(tableColumnVO.getCloumnName()));

                            dbCloumn.setCloumnJavaType(GenerateDataTypeUtils.getJavaTypeByDB(dbTtype, tableColumnVO.getCloumnType()));
                            dbCloumn.setCloumnJdbcType(GenerateDataTypeUtils.getJdbcTypeByDB(dbTtype, tableColumnVO.getCloumnType()));

                            dbCloumns.add(dbCloumn);
                        }
                        dbModels.add(dbModel);
                    }

                    TemplateClassLoad.BASE_CODE_GENERATE_TEMPLATE.stream().filter((script)-> script.getDBType().equals(dbTtype)).forEach(script -> {
                        script.generate(dbModels, generateConfig);
                    });

                    DialogFactory.getInstance().showSuccessMsg("保存成功", "代码生成成功", ()->{});
                }

            }

        }
        else
        {
            DialogFactory.getInstance().showFaildMsg("生成错误", "当前数据库没有表", ()->{});
        }
    }

    /**
     * 导入设置
     * @param event
     */
    @FXML
    void processImportSetting(ActionEvent event) {

    }

    /**
     * 使用说明
     * @param event
     */
    @FXML
    void processInstru(ActionEvent event) {

    }

    /**
     * 退出
     * @param event
     */
    @FXML
    void processQuit(ActionEvent event) {
        System.exit(1);
    }

    /**
     * 数据库设置
     * @param event
     */
    @FXML
    void processSettingDB(ActionEvent event) {
        showBlockStage("数据库设置", DBConfigController.class);
    }

    /**
     * 代码生成设置
     * @param event
     */
    @FXML
    void processSettingGenerate(ActionEvent event) {
        showBlockStage("代码生成设置", GenerateConfigController.class);
    }

    /**
     * 构建阻塞窗口
     * @param title
     * @param baseControllerClass
     * @param <T>
     */
    private <T extends BaseController> void showBlockStage(String title, Class<T> baseControllerClass) {
        final Stage innerStage = new Stage();
        innerStage.setTitle(title);
        innerStage.initModality(Modality.APPLICATION_MODAL);
        innerStage.initStyle(StageStyle.DECORATED);

        BaseController baseController = FXMLLoaderUtil.load(baseControllerClass);
        innerStage.setScene(new Scene(baseController));
        innerStage.initOwner(stage);
        innerStage.showAndWait();
    }


}
