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
import com.mnt.mybatis.generate.core.BaseDBLoadTemplate;
import com.mnt.mybatis.generate.core.load.TemplateClassLoad;
import com.mnt.mybatis.generate.model.UserData;
import com.mnt.mybatis.generate.model.db.DBCloumn;
import com.mnt.mybatis.generate.model.db.DBModel;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Mybatis代码生成工具
 */
@MainView(appName = "Mybatis代码生成工具")
public class MainViewController extends BaseController {

    @FXML
    private ComboBox<String> combDB;

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
    private ObservableList<String> itemDBConfigs;

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
        listTables.setItems(searchItemTableNames);

        addListener();

        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        for (BaseDBLoadTemplate baseDBLoadTemplate : TemplateClassLoad.BASE_DB_INFO_LOAD_TEMPLATE.getScripts()) {
            if(UserData.dbType.equals(baseDBLoadTemplate.getKey())) {
                dbLoadTemplate = baseDBLoadTemplate;
                break;
            }
        }

        if(null != dbLoadTemplate) {
            //初始化列表数据
            itemTableNames = dbLoadTemplate.listTableName();
            listTables.setItems(itemTableNames);
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

            }
        });

        //添加选择监听事件
        listTables.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TableNameVO>() {

            @Override
            public void changed(ObservableValue<? extends TableNameVO> observable, TableNameVO oldValue,
                                TableNameVO newValue) {
                if(null != newValue)
                {
                    tableFields.setItems(dbLoadTemplate.listTableColumn(newValue.getTableName()));
                }
            }
        });
    }

    /**
     * 初始化name列表
     */
    private void initList() {
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

                    DBModel dbModel ;
                    for (TableNameVO tableNameVO : selectTables) {
                        dbModel = new DBModel();
                        dbModel.setTableName(tableNameVO.getTableName());
                        dbModel.setRemark(tableNameVO.getRemark());
                        List<DBCloumn> dbCloumns = new ArrayList<>();
                        dbModel.setDbCloumns(dbCloumns);


                        //获取表字段
                        List<TableColumnVO> tableColumnVOs = dbLoadTemplate.listTableColumn(tableNameVO.getTableName());
                        DBCloumn dbCloumn;
                        for (TableColumnVO tableColumnVO : tableColumnVOs) {
                            dbCloumn = new DBCloumn();
                            dbCloumn.setCloumnName(tableColumnVO.getCloumnName());
                            dbCloumn.setCloumnType(tableColumnVO.getCloumnType());
                            dbCloumn.setLength(tableColumnVO.getLength());
                            dbCloumn.setRemark(tableColumnVO.getRemark());
                            dbCloumn.setCloumnJavaName(GenerateNameUtils.getJavaName(tableColumnVO.getCloumnName()));
                            dbCloumn.setCloumnJavaType(GenerateDataTypeUtils.getJavaTypeByMysql(tableColumnVO.getCloumnType()));
                            dbCloumn.setMethodName(GenerateNameUtils.getClassFileName(tableColumnVO.getCloumnName()));
                            dbCloumn.setCloumnJdbcType(GenerateDataTypeUtils.getJdbcTypeByMysql(tableColumnVO.getCloumnType()));

                            dbCloumns.add(dbCloumn);
                        }
                        dbModels.add(dbModel);
                    }

                    TemplateClassLoad.BASE_CODE_GENERATE_TEMPLATE.getScripts().forEach(script -> {
                        script.generate(dbModels);
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
        final Stage innerStage = new Stage();
        innerStage.setTitle("数据库设置");
        innerStage.initModality(Modality.APPLICATION_MODAL);
        innerStage.initStyle(StageStyle.DECORATED);

        DBConfigController dbConfigController = FXMLLoaderUtil.load(DBConfigController.class);
        innerStage.setScene(new Scene(dbConfigController));
        innerStage.initOwner(stage);
        innerStage.showAndWait();

    }

    /**
     * 代码生成设置
     * @param event
     */
    @FXML
    void processSettingGenerate(ActionEvent event) {

    }


}
