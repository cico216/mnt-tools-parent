package com.mnt.protocol.view;

import com.mnt.gui.fx.base.BaseController;
import com.mnt.gui.fx.controls.file.FileChooserFacotry;
import com.mnt.gui.fx.loader.FXMLLoaderUtil;
import com.mnt.protocol.model.UserData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.File;

/**
 * 代码生成设置界面
 * @author jiangbiao
 * @date 2018/9/13 9:41
 */
public class SettingController extends BaseController {

    @FXML
    private TextField txtUserName;

    @FXML
    private ComboBox<String> combType;

    @FXML
    private TextField txtProjectPath;


    private Stage currStage;

    @FXML
    private TextField txtTestCasePath;

    public SettingController(Stage stage) {
        this.currStage = stage;
        FXMLLoaderUtil.load(this);
    }


    @Override
    public void init() {
        super.init();

        String userName = UserData.getUserConfig().getUser();
        txtUserName.setText(userName);

        String projectPath = UserData.getUserConfig().getProjectPath();
        txtProjectPath.setText(projectPath);


        addListener();

    }

    /**
     * 添加监听
     */
    private void addListener() {
        this.setOnKeyPressed((event) -> {
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
            currStage.close();
        }
    }


    @FXML
    void processClose(ActionEvent event) {
        currStage.close();
    }

    @FXML
    void processConfirm(ActionEvent event) {

        UserData.getUserConfig().setUser(txtUserName.getText());

        UserData.getUserConfig().setProjectPath(txtProjectPath.getText());

        UserData.saveUserConfig();

        currStage.close();
    }


    @FXML
    void processSelectDir(ActionEvent event) {

        if(null != txtProjectPath.getText() && !"".equals(txtProjectPath.getText())) {
            final File dir = FileChooserFacotry.chooserDirectorControl(getMainStage(), txtProjectPath.getText());
            if(null != dir && dir.isDirectory()) {
                txtProjectPath.setText(dir.getAbsolutePath());
            }

        } else {
            final File dir = FileChooserFacotry.chooserDirectorControl(getMainStage());
            if(null != dir && dir.isDirectory()) {
                txtProjectPath.setText(dir.getAbsolutePath());
            }
        }

    }


}
