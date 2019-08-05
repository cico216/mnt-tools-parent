package com.mnt.mybatis.generate.component;

import com.mnt.gui.fx.base.BaseController;
import com.mnt.gui.fx.controls.dialog.DialogFactory;
import com.mnt.mybatis.generate.core.BaseDBLoadTemplate;
import com.mnt.mybatis.generate.core.load.TemplateClassLoad;
import com.mnt.mybatis.generate.model.UserData;
import com.mnt.mybatis.generate.model.db.JDBCInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 当前支持的数据库类型选项
     */
    private Map<String, RadioButton> dbTypesRadios;
    private ToggleGroup radioToggleGroup;


    private ObservableList<JDBCInfo> itemDBConfigs = FXCollections.observableArrayList();

    @Override
    public void init() {
        super.init();

        initRadio();
        initList();

        loadData();

    }

    /**
     * 初始化数据库类型
     */
    private void initRadio() {
        hbDbTypes.getChildren().clear();

        List<BaseDBLoadTemplate> dbLoadTemplates = TemplateClassLoad.BASE_DB_INFO_LOAD_TEMPLATE.getScripts();
        dbTypesRadios = new HashMap<>(dbLoadTemplates.size());
        radioToggleGroup = new ToggleGroup();
        RadioButton radioButton;
        for (BaseDBLoadTemplate baseDBLoadTemplate : dbLoadTemplates) {
            radioButton = new RadioButton();
            radioButton.setToggleGroup(radioToggleGroup);
            radioButton.setUserData(baseDBLoadTemplate.getDriver());
            radioButton.setText(baseDBLoadTemplate.getKey());
            dbTypesRadios.put(baseDBLoadTemplate.getKey(), radioButton);
            hbDbTypes.getChildren().add(radioButton);
        }



    }

    /**
     * 初始化列表
     */
    private void initList() {

        listDbConfigs.setItems(itemDBConfigs);
        listDbConfigs.setCellFactory(new Callback<ListView<JDBCInfo>, ListCell<JDBCInfo>>() {
            @Override
            public ListCell<JDBCInfo> call(ListView<JDBCInfo> param) {
                return new ListCell<JDBCInfo>() {
                    @Override
                    protected void updateItem(JDBCInfo item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                           setGraphic(null);
                        } else {
                            HBox hBox = new HBox();
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            hBox.setSpacing(2);
                            hBox.getChildren().add(new Label(item.getConfigName() + "[" + item.getDbType() + "]"));
                            HBox hboxBtn = new HBox();
                            hboxBtn.setAlignment(Pos.CENTER_RIGHT);
                            HBox.setHgrow(hboxBtn, Priority.ALWAYS);
                            Button btnOpera = new Button("删除");
                            btnOpera.setOnAction((event) -> {
                                DialogFactory.getInstance().showConfirm("是否删除", "是否删除选择配置", ()-> {
                                    delConfig(item);
                                }, () -> { });
                            });

                            hboxBtn.getChildren().add(btnOpera);
                            hBox.getChildren().add(hboxBtn);

                            setGraphic(hBox);

                        }

                    }
                };
            }
        });

        listDbConfigs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(null != newValue) {
                selectConfig(newValue);
            }
        });



    }

    /**
     * 加载数据
     */
    private void loadData() {
        List<JDBCInfo> jdbcInfos = UserData.getJDBCInfos();

    }

    /**
     * 删除配置
     * @param jdbcInfo
     */
    private void delConfig(JDBCInfo jdbcInfo) {
        itemDBConfigs.remove(jdbcInfo);
        UserData.getJDBCInfos().remove(jdbcInfo);
        clearValue();
        UserData.saveJDBCInfos();
    }

    /**
     * 选择配置
     * @param jdbcInfo
     */
    private void selectConfig(JDBCInfo jdbcInfo) {
        RadioButton radioButton = dbTypesRadios.get(jdbcInfo.getDbType());
        radioToggleGroup.selectToggle(radioButton);


    }

    /**
     * 清除输入数据
     */
    private void clearValue() {
        txtConfigName.clear();
        txtUrl.clear();
        txtUserName.clear();
        pwdDb.clear();
        radioToggleGroup.getSelectedToggle().setSelected(false);

    }

    @FXML
    void processAdd(ActionEvent event) {

        clearValue();
        listDbConfigs.getSelectionModel().clearSelection();
    }

    @FXML
    void processSave(ActionEvent event) {
        if(null == listDbConfigs.getSelectionModel().getSelectedItem()) {
            JDBCInfo jdbcInfo = new JDBCInfo();
            RadioButton selectRadio = ((RadioButton)radioToggleGroup.getSelectedToggle());
            jdbcInfo.setDbType(selectRadio.getText());
            jdbcInfo.setConfigName(txtConfigName.getText());
            jdbcInfo.setDbDriver(String.valueOf(selectRadio.getUserData()));
            jdbcInfo.setDbUrl(txtUrl.getText());
            jdbcInfo.setDbUserName(txtUserName.getText());
            jdbcInfo.setDbPassword(pwdDb.getText());

            itemDBConfigs.add(jdbcInfo);
            UserData.getJDBCInfos().add(jdbcInfo);
        }


        UserData.saveJDBCInfos();


    }

    @FXML
    void processTest(ActionEvent event) {


    }



}
