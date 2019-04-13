package com.mnt.mybatis.generate.view;

import com.mnt.gui.fx.base.BaseController;
import com.mnt.gui.fx.table.TabelCellFactory;
import com.mnt.gui.fx.table.TableViewSupport;
import com.mnt.gui.fx.view.anno.MainView;
import com.mnt.mybatis.generate.vo.TableColumnVO;
import com.mnt.mybatis.generate.vo.TableNameVO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    private TableViewSupport<TableColumnVO> tableViewSupport;
    private ObservableList<TableNameVO> itemListTables = FXCollections.observableArrayList();

    @Override
    public void init() {
        super.init();
        initTable();
        listTables.setItems(itemListTables);


    }

    /**
     * 初始化表格
     */
    private void initTable() {
        tableViewSupport = TabelCellFactory.createTableSupport(tableFields, TableColumnVO.class);
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
