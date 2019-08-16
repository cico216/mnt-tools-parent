package generates;


import com.mnt.mybatis.generate.core.BaseCodeGenerateTemplate;
import com.mnt.mybatis.generate.model.db.DBModel;
import com.mnt.mybatis.generate.model.generate.CodeGenerateInfo;
import com.mnt.mybatis.generate.model.generate.GenerateConfig;

import java.util.List;

/**
 * mysql 代码生成
 */
public class PostgresqlCodeGenerate extends BaseCodeGenerateTemplate {
    @Override
    public String getDBType() {
        return "postgresql";
    }

    @Override
    public List<CodeGenerateInfo> loadPropertieskey() {
        return null;
    }

    @Override
    protected void generateImpl(DBModel dbModel, GenerateConfig generateConfig) {

    }



}
