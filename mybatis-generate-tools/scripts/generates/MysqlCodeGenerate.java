package generates;


import com.mnt.mybatis.generate.core.BaseCodeGenerateTemplate;
import com.mnt.mybatis.generate.model.db.DBModel;
import com.mnt.mybatis.generate.model.generate.CodeGenerateInfo;

import java.util.List;

/**
 * mysql 代码生成
 */
public class MysqlCodeGenerate extends BaseCodeGenerateTemplate {
    @Override
    protected String getDBType() {
        return "mysql";
    }

    @Override
    protected List<CodeGenerateInfo> loadPropertieskey() {
        return null;
    }

    @Override
    protected void generateImpl(DBModel dbModel) {

    }
}
