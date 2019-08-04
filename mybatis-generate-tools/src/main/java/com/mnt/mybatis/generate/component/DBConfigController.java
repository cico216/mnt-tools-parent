package com.mnt.mybatis.generate.component;

import com.mnt.gui.fx.base.BaseController;
import com.mnt.mybatis.generate.model.db.JDBCInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * 数据库配置界面
 */
public class DBConfigController extends BaseController {

    @FXML
    private ListView<JDBCInfo> listDbConfigs;

    @FXML
    private VBox vbConfigProperties;

    @FXML
    private HBox hbDbTypes;

    @FXML
    private TextField txtConfigName;

    @FXML
    private TextField txtUrl;

    @FXML
    private TextField txtUserName;

    @FXML
    private PasswordField pwdDb;

    @Override
    public void init() {
        super.init();


    }



    @FXML
    void processAdd(ActionEvent event) {

    }

    @FXML
    void processSave(ActionEvent event) {

    }

    @FXML
    void processTest(ActionEvent event) {

    }



}
