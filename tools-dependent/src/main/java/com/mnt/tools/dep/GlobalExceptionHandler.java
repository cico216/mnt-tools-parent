//package com.mnt.tools.dep;
//
//import com.auth0.jwt.internal.com.fasterxml.jackson.databind.ObjectMapper;
//import com.jiangroom.common.logger.ErrorLogger;
//import com.jiangroom.common.model.AjaxResult;
//import org.apache.shiro.authz.UnauthorizedException;
//import org.springframework.boot.autoconfigure.web.ErrorController;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Controller;
//import org.springframework.validation.BindException;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.List;
//
///**
// * Created by yanjun on 2017/5/25.
// */
//@Controller
//@ControllerAdvice(annotations = Controller.class)
//public class GlobalExceptionHandler implements ErrorController {
//    private final static  String XML_HTTP_REQUEST = "XMLHttpRequest";
//    private final static  String X_REQUESTED_WITH = "x-requested-with";
//    private final static  String ERROR_PATH = "/error";
//    private final static  String ERROR_PAGE_403="pages/403";
//    private final static  String ERROR_PAGE_403_MESSAGE="UnauthorizedException:您访问的资源，未授权！";
//    private final static  String ERROR_PAGE_500="pages/500";
//    private final static  String ERROR_PAGE_500_MESSAGE="系统异常";
//    private final static  String OUT_JSON_FAILED_MESSAGE  ="outObjectToResponse failed";
//    @RequestMapping(value = ERROR_PATH)
//    public String handleError() {
//        return "pages/404";
//    }
//
//    @Override
//    public String getErrorPath() {
//        return ERROR_PATH;
//    }
//
//    /**
//     * 全局异常捕获
//     * @param request
//     * @param response
//     * @param e
//     * @return
//     */
//    @ExceptionHandler(value = {Exception.class})
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public Object handlerException(HttpServletRequest request,HttpServletResponse response,Exception e) {
//        //判断是否是Ajax请求
//        if(isAjaxRequest(request)){
//            return   resolveExceptionAjax(response,e);
//        }else{
//            return   resolveExceptionView(e);
//        }
//    }
//
//    /**
//     * Ajax请求异常处理
//     * @param response
//     * @param e
//     * @return
//     */
//    public Object resolveExceptionAjax(HttpServletResponse response,Exception e){
//        ErrorLogger.log(ERROR_PAGE_403_MESSAGE, e);
//        outObjectToResponse(AjaxResult.failed(AjaxResult.CODE_FAILED, ERROR_PAGE_403_MESSAGE),response);
//        return null;
//    }
//
//    /**
//     * 页面刷新请求异常处理
//     * @param e
//     * @return
//     */
//    public ModelAndView resolveExceptionView(Exception e){
//        ModelAndView resultModel =new ModelAndView();
//        if(e instanceof UnauthorizedException){
//            ErrorLogger.log(ERROR_PAGE_403_MESSAGE, e);
//            resultModel.setViewName(ERROR_PAGE_403);
//        }else{
//            resultModel.setViewName(ERROR_PAGE_500);
//            ErrorLogger.log(ERROR_PAGE_500_MESSAGE, e);
//        }
//        return resultModel;
//    }
//
//    /**
//     * outObjectToResponse
//     * @param object
//     * @param response
//     */
//    private void outObjectToResponse(Object object, HttpServletResponse response)  {
//        try {
//            PrintWriter out = response.getWriter();
//            new ObjectMapper().writer().writeValue(out, object);
//            out.flush();
//            object = null;
//        } catch (IOException e) {
//            ErrorLogger.log(e);
//            throw new RuntimeException(OUT_JSON_FAILED_MESSAGE,e);
//        }
//
//    }
//
//    public static boolean isAjaxRequest(HttpServletRequest request) {
//        String requestType = request.getHeader(X_REQUESTED_WITH);
//        return XML_HTTP_REQUEST.equals(requestType);
//    }
//
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(BindException.class)
//    @ResponseBody
//    public AjaxResult handleServiceException(BindException e) {
//        ErrorLogger.log("参数验证失败", e);
//        String errorMassage = "";
//        List<FieldError> errList = e.getFieldErrors();
//        for (FieldError error : errList
//                ) {
//            errorMassage += error.getDefaultMessage() + ",";
//        }
//        return AjaxResult.failed(errorMassage);
//    }
//}
//
