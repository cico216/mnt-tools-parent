package com.mnt.mybatis.generate.component;

import com.mnt.gui.fx.base.BaseController;
import com.mnt.gui.fx.controls.dialog.DialogFactory;
import com.mnt.mybatis.generate.core.BaseCodeGenerateTemplate;
import com.mnt.mybatis.generate.core.load.TemplateClassLoad;
import com.mnt.mybatis.generate.model.UserData;
import com.mnt.mybatis.generate.model.db.JDBCInfo;
import com.mnt.mybatis.generate.model.generate.CodeGenerateInfo;
import com.mnt.mybatis.generate.model.generate.GenerateConfig;
import com.mnt.mybatis.generate.model.view.PropertyType;
import com.sun.istack.internal.Nullable;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成配置界面
 */
public class GenerateConfigController extends BaseController {

    @FXML
    private ListView<GenerateConfig> listGenerateConfigs;

    @FXML
    private VBox vbConfigProperties;

    @FXML
    private HBox hbDbTypes;

    @FXML
    private TextField txtConfigName;

    /**
     * 当前支持的数据库类型选项
     */
    private Map<String, RadioButton> dbTypesRadios;
    private Map<String, BaseCodeGenerateTemplate> codeGenerateScript;
    private ToggleGroup radioToggleGroup;


    private ObservableList<GenerateConfig> itemGenerateConfigs = FXCollections.observableArrayList();

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
        listGenerateConfigs.setOnKeyPressed((event) -> {
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

        List<BaseCodeGenerateTemplate> baseCodeGenerateTemplates = TemplateClassLoad.BASE_CODE_GENERATE_TEMPLATE.getScripts();
        dbTypesRadios = new HashMap<>(baseCodeGenerateTemplates.size());
        codeGenerateScript = new HashMap<>(baseCodeGenerateTemplates.size());
        radioToggleGroup = new ToggleGroup();
        RadioButton radioButton;
        for (BaseCodeGenerateTemplate baseCodeGenerateTemplate : baseCodeGenerateTemplates) {
            radioButton = new RadioButton();
            radioButton.setToggleGroup(radioToggleGroup);
            radioButton.setText(baseCodeGenerateTemplate.getDBType());
            dbTypesRadios.put(baseCodeGenerateTemplate.getDBType(), radioButton);
            codeGenerateScript.put(baseCodeGenerateTemplate.getDBType(), baseCodeGenerateTemplate);
            hbDbTypes.getChildren().add(radioButton);
        }
        radioToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(null != newValue) {
                BaseCodeGenerateTemplate baseCodeGenerateTemplate = codeGenerateScript.get(((RadioButton)newValue).getText());
                List<CodeGenerateInfo> codeGenerateInfos = baseCodeGenerateTemplate.loadPropertieskey();
                loadProperties(codeGenerateInfos, null);
            }
        });


    }




    /**
     * 初始化列表
     */
    private void initList() {

        listGenerateConfigs.setItems(itemGenerateConfigs);
        listGenerateConfigs.setCellFactory(new Callback<ListView<GenerateConfig>, ListCell<GenerateConfig>>() {
            @Override
            public ListCell<GenerateConfig> call(ListView<GenerateConfig> param) {
                return new ListCell<GenerateConfig>() {
                    @Override
                    protected void updateItem(GenerateConfig item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                           setGraphic(null);
                        } else {
                            HBox hBox = new HBox();
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            hBox.setSpacing(2);
                            CheckBox cbUsed = new CheckBox();
                            cbUsed.setSelected(item.isUsed());
                            cbUsed.selectedProperty().bind(item.usedProperty());
                            hBox.getChildren().add(cbUsed);
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

        listGenerateConfigs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(null != newValue) {
                selectConfig(newValue);
            }
        });



    }

    /**
     * 加载数据
     */
    private void loadData() {
        List<GenerateConfig> generateConfigs = UserData.getGenerateConfigs();
        for (GenerateConfig generateConfig : generateConfigs) {
            itemGenerateConfigs.add(generateConfig);
        }


    }

    /**
     * 加载动态属性
     * @param codeGenerateInfos
     * @param generateConfig
     */
    private void loadProperties(List<CodeGenerateInfo> codeGenerateInfos, GenerateConfig generateConfig) {

        vbConfigProperties.getChildren().clear();
        for (CodeGenerateInfo codeGenerateInfo : codeGenerateInfos) {
            String value = codeGenerateInfo.getPropertyValue();
            if(null != generateConfig) {
                value = generateConfig.getProperties().get(codeGenerateInfo.getPropertyKey());
            }
            if(codeGenerateInfo.getType() == PropertyType.DIRECTOR) {
                vbConfigProperties.getChildren().add(buildDirProperties(codeGenerateInfo.getPropertyName(), value));
            } else if(codeGenerateInfo.getType() == PropertyType.TEXT) {
                vbConfigProperties.getChildren().add(buildTextProperties(codeGenerateInfo.getPropertyName(), value));
            }

        }


    }

    private HBox buildTextProperties(String name, String value) {
        HBox result = buildHBox(name);

        return result;
    }

    private HBox buildDirProperties(String name, String value) {
        HBox result = buildHBox(name);

        return result;
    }

    /**
     * 构建基础容器
     * @param name
     * @return
     */
    private HBox buildHBox(String name) {
        HBox result = new HBox();
        result.setSpacing(2);
        result.setAlignment(Pos.CENTER_LEFT);

        Label lblName = new Label(name);
        lblName.setPrefWidth(150);
        result.getChildren().add(lblName);
        return result;
    }


    /**
     * 删除配置
     * @param generateConfig
     */
    private void delConfig(GenerateConfig generateConfig) {
        itemGenerateConfigs.remove(generateConfig);
        UserData.getGenerateConfigs().remove(generateConfig);
        clearValue();
        UserData.saveGenerateConfigs();
    }

    /**
     * 选择配置
     * @param generateConfig
     */
    private void selectConfig(GenerateConfig generateConfig) {
        RadioButton radioButton = dbTypesRadios.get(generateConfig.getDbType());
        radioToggleGroup.selectToggle(radioButton);
        txtConfigName.setText(generateConfig.getConfigName());
        BaseCodeGenerateTemplate baseCodeGenerateTemplate = codeGenerateScript.get(generateConfig.getDbType());
        List<CodeGenerateInfo> codeGenerateInfos = baseCodeGenerateTemplate.loadPropertieskey();

        loadProperties(codeGenerateInfos, generateConfig);
    }

    /**
     * 清除输入数据
     */
    private void clearValue() {

        radioToggleGroup.getSelectedToggle().setSelected(false);

    }

    /**
     * 获取当前选择的链接
     * @return
     */
    @Nullable
    private GenerateConfig getCurrSelGenerateInfo() {
        return listGenerateConfigs.getSelectionModel().getSelectedItem();
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
    private GenerateConfig getEditGenerateConfig() {
        GenerateConfig jdbcInfo = new GenerateConfig();
        RadioButton selectRadio = ((RadioButton)radioToggleGroup.getSelectedToggle());
        jdbcInfo.setDbType(selectRadio.getText());
        jdbcInfo.setConfigName(txtConfigName.getText());

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
        GenerateConfig generateConfig = getEditGenerateConfig();

        if(StringUtils.isEmpty(generateConfig.getConfigName())) {
            DialogFactory.getInstance().showFaildMsg("信息错误", "请输入配置名称", ()-> {});
            return false;
        }


        return true;
    }


    @FXML
    void processAdd(ActionEvent event) {

        clearValue();
        listGenerateConfigs.getSelectionModel().clearSelection();
    }

    /**
     * 使用当前配置
     * @param event
     */
    @FXML
    void processUse(ActionEvent event) {
        GenerateConfig generateConfig = getCurrSelGenerateInfo();
        if(null == generateConfig) {
            DialogFactory.getInstance().showFaildMsg("使用失败", "请先保存配置后再使用", ()-> {});
            return;
        }




    }

    @FXML
    void processSave(ActionEvent event) {
        if(!checkEditJDBCInfo()) {
            return;
        }

        //新增的情况
        if(null == getCurrSelGenerateInfo()) {
            GenerateConfig generateConfig = getEditGenerateConfig();
            itemGenerateConfigs.add(generateConfig);
            listGenerateConfigs.getSelectionModel().select(generateConfig);
            UserData.getGenerateConfigs().add(generateConfig);
        } else {
            GenerateConfig generateConfig = getCurrSelGenerateInfo();
            RadioButton selectRadio = ((RadioButton)radioToggleGroup.getSelectedToggle());
            generateConfig.setDbType(selectRadio.getText());
            generateConfig.setConfigName(txtConfigName.getText());

        }

        //保存信息
        UserData.saveGenerateConfigs();
        DialogFactory.getInstance().showSuccessMsg("保存成功", "数据库信息已保存", ()-> {});
    }



}
