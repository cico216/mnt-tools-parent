package com.mnt.protocol.callback;


import com.mnt.protocol.view.HeaderParamController;

/**
 * @author jiangbiao
 * @date 2018/9/13 15:20
 */
public interface ParamDeleteAction {

    /**
     * 删除的对象
     * @param headerParamController
     */
    void delete(HeaderParamController headerParamController);
}
