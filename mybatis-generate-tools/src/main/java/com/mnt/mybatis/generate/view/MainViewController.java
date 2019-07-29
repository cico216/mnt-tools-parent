package com.mnt.mybatis.generate.view;

import com.mnt.gui.fx.base.BaseController;
import com.mnt.gui.fx.controls.dialog.DialogFactory;
import com.mnt.gui.fx.table.TabelCellFactory;
import com.mnt.gui.fx.table.TableViewSupport;
import com.mnt.gui.fx.view.anno.MainView;
import com.mnt.mybatis.generate.vo.TableColumnVO;
import com.mnt.mybatis.generate.vo.TableNameVO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

import java.util.function.Predicate;

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
    }

    /**
     * 添加事件
     */
    private void addListener() {
        this.setOnKeyPressed((event) -> {
            if(event.isControlDown() && event.getCode() == KeyCode.S) {
                //save code
                processGenerateCode(null);
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

        DialogFactory.getInstance().showFaildMsg("生成错误", "请选择生成的数据库表格", ()->{});
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

    }

    /**
     * 代码生成设置
     * @param event
     */
    @FXML
    void processSettingGenerate(ActionEvent event) {

    }


}
