package generates;


import com.mnt.mybatis.generate.core.BaseCodeGenerateTemplate;
import com.mnt.mybatis.generate.model.db.DBModel;
import com.mnt.mybatis.generate.model.generate.CodeGenerateInfo;
import com.mnt.mybatis.generate.model.view.PropertyType;

import java.util.ArrayList;
import java.util.List;

/**
 * mysql 代码生成
 */
public class MysqlCodeGenerate extends BaseCodeGenerateTemplate {
    @Override
    public String getDBType() {
        return "mysql";
    }

    @Override
    public List<CodeGenerateInfo> loadPropertieskey() {
        List<CodeGenerateInfo>  result = new ArrayList<>();
        result.add(new CodeGenerateInfo("代码作者","project.author", "CICO", PropertyType.TEXT));
        result.add(new CodeGenerateInfo("项目路径","project.path", "", PropertyType.DIRECTOR));
        result.add(new CodeGenerateInfo("dao路径","project.dao.path", "", PropertyType.TEXT));
        result.add(new CodeGenerateInfo("mapper路径","project.mapper.path", "", PropertyType.TEXT));
        result.add(new CodeGenerateInfo("vo路径","project.vo.path", "", PropertyType.TEXT));
        result.add(new CodeGenerateInfo("query路径","project.query.path", "", PropertyType.TEXT));
        result.add(new CodeGenerateInfo("entity路径","project.entity.path", "", PropertyType.TEXT));
        result.add(new CodeGenerateInfo("service路径","project.service.path", "", PropertyType.TEXT));
        result.add(new CodeGenerateInfo("serviceImpl路径","project.serviceimpl.path", "", PropertyType.TEXT));

        return result;
    }

    @Override
    protected void generateImpl(DBModel dbModel) {



    }
}
