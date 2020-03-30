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
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
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
    private Map<String, BaseDBLoadTemplate> dbTypesScript;
    private ToggleGroup radioToggleGroup;


    private ObservableList<JDBCInfo> itemDBConfigs = FXCollections.observableArrayList();

    @Override
    public void init() {
        super.init();

        initRadio();
        initList();
        addListener();
        loadData();


    }

    /**
     * 添加监听
     */
    private void addListener() {
        this.setOnKeyPressed((event) -> {
            esc(event);
        });
        listDbConfigs.setOnKeyPressed((event) -> {
            esc(event);
        });

    }

    /**
     * 退出事件
     * @param event
     */
    private void esc(KeyEvent event) {
        KeyCode keyCode = event.getCode();
        if(keyCode == KeyCode.ESCAPE) {
            //quit
            ((Stage)getScene().getWindow()).close();
        }
    }

    /**
     * 初始化数据库类型
     */
    private void initRadio() {
        hbDbTypes.getChildren().clear();

        List<BaseDBLoadTemplate> dbLoadTemplates = TemplateClassLoad.BASE_DB_INFO_LOAD_TEMPLATE;
        dbTypesRadios = new HashMap<>(dbLoadTemplates.size());
        dbTypesScript = new HashMap<>(dbLoadTemplates.size());
        radioToggleGroup = new ToggleGroup();
        RadioButton radioButton;
        for (BaseDBLoadTemplate baseDBLoadTemplate : dbLoadTemplates) {
            radioButton = new RadioButton();
            radioButton.setToggleGroup(radioToggleGroup);
            radioButton.setUserData(baseDBLoadTemplate.getDriver());
            radioButton.setText(baseDBLoadTemplate.getKey());
            dbTypesRadios.put(baseDBLoadTemplate.getKey(), radioButton);
            dbTypesScript.put(baseDBLoadTemplate.getKey(), baseDBLoadTemplate);
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
        for (JDBCInfo jdbcInfo : jdbcInfos) {
            itemDBConfigs.add(jdbcInfo);
        }


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
        txtConfigName.setText(jdbcInfo.getConfigName());
        txtUrl.setText(jdbcInfo.getDbUrl());
        txtUserName.setText(jdbcInfo.getDbUserName());
        pwdDb.setText(jdbcInfo.getDbPassword());
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

    /**
     * 获取当前选择的链接
     * @return
     */
    private JDBCInfo getCurrSelJDBCInfo() {
        return listDbConfigs.getSelectionModel().getSelectedItem();
    }

    /**
     * 获取当前选择的数据库类型
     * @return
     */
    private String getCurrSelDBType() {
        RadioButton radioButton = ((RadioButton)radioToggleGroup.getSelectedToggle());
        if(null == radioButton) {
            return null;
        }
        return radioButton.getText();
    }

    /**
     * 获取当前编辑的jdbc
     * @return
     */
    private JDBCInfo getEditJDBCInfo() {
        JDBCInfo jdbcInfo = new JDBCInfo();
        RadioButton selectRadio = ((RadioButton)radioToggleGroup.getSelectedToggle());
        jdbcInfo.setDbType(selectRadio.getText());
        jdbcInfo.setConfigName(txtConfigName.getText());
        jdbcInfo.setDbDriver(String.valueOf(selectRadio.getUserData()));
        jdbcInfo.setDbUrl(txtUrl.getText());
        jdbcInfo.setDbUserName(txtUserName.getText());
        jdbcInfo.setDbPassword(pwdDb.getText());
        return jdbcInfo;
    }

    /**
     * 检测当前编辑的JDBCInfo
     * @return
     */
    private boolean checkEditJDBCInfo() {
        String currDBType = getCurrSelDBType();
        if(null == currDBType) {
            DialogFactory.getInstance().showFaildMsg("信息错误", "请选择数据库类型", ()-> {});
            return false;
        }
        JDBCInfo jdbcInfo = getEditJDBCInfo();

        if(StringUtils.isEmpty(jdbcInfo.getConfigName())) {
            DialogFactory.getInstance().showFaildMsg("信息错误", "请输入配置名称", ()-> {});
            return false;
        }

        if(StringUtils.isEmpty(jdbcInfo.getDbUrl())) {
            DialogFactory.getInstance().showFaildMsg("信息错误", "请输入连接URL", ()-> {});
            return false;
        }

        if(StringUtils.isEmpty(jdbcInfo.getDbUserName())) {
            DialogFactory.getInstance().showFaildMsg("信息错误", "请输入数据库用户名", ()-> {});
            return false;
        }

        return true;
    }


    @FXML
    void processAdd(ActionEvent event) {

        clearValue();
        listDbConfigs.getSelectionModel().clearSelection();
    }

    @FXML
    void processSave(ActionEvent event) {
        if(!checkEditJDBCInfo()) {
            return;
        }

        //新增的情况
        if(null == getCurrSelJDBCInfo()) {
            JDBCInfo jdbcInfo = getEditJDBCInfo();
            itemDBConfigs.add(jdbcInfo);
            listDbConfigs.getSelectionModel().select(jdbcInfo);
            UserData.getJDBCInfos().add(jdbcInfo);
        } else {
            JDBCInfo jdbcInfo = getCurrSelJDBCInfo();
            RadioButton selectRadio = ((RadioButton)radioToggleGroup.getSelectedToggle());
            jdbcInfo.setDbUrl(txtUrl.getText());
            jdbcInfo.setDbUserName(txtUserName.getText());
            jdbcInfo.setDbType(selectRadio.getText());
            jdbcInfo.setConfigName(txtConfigName.getText());
            jdbcInfo.setDbDriver(String.valueOf(selectRadio.getUserData()));
            jdbcInfo.setDbPassword(pwdDb.getText());

        }

        //保存信息
        UserData.saveJDBCInfos();
        DialogFactory.getInstance().showSuccessMsg("保存成功", "数据库信息已保存", ()-> {});
    }

    @FXML
    void processTest(ActionEvent event) {

        if(!checkEditJDBCInfo()) {
            return;
        }

        String currDBType = getCurrSelDBType();
        JDBCInfo jdbcInfo = getEditJDBCInfo();

        BaseDBLoadTemplate baseDBLoadTemplate = dbTypesScript.get(currDBType);
        //获取当前脚本的连接
        Connection collection = baseDBLoadTemplate.getConnection(jdbcInfo);
        if(null != collection)
        {
            DialogFactory.getInstance().showSuccessMsg("测试成功", "连接成功", ()-> {});
        }
        else
        {
            DialogFactory.getInstance().showFaildMsg("测试失败", "连接失败", ()-> {});
        }

    }



}
